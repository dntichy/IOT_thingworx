package com.thingworx.sdk.simplething;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.IntegerPrimitive;

@ThingworxPropertyDefinitions(properties = {

        // This property is setup for collecting time series data. Each value
        // that is collected will be pushed to the platfrom from within the
        // processScanRequest() method.
        @ThingworxPropertyDefinition(name = "Temperature", description = "The device temperature",
                baseType = "NUMBER",
                aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:0",
                        "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:ALWAYS",
                        "isFolded:FALSE", "defaultValue:0" }),

        // This property is also pushed to the platform, but only when the value
        // of the property has changed.
        @ThingworxPropertyDefinition(name = "Humidity", description = "The device humidity",
                baseType = "NUMBER",
                aspects = { "dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                        "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:VALUE",
                        "defaultValue:0" }),

        // This property is never pushed to the platform. The platform will always
        // request the values current value from the application.
        @ThingworxPropertyDefinition(name = "SetPoint", description = "The desired temperature",
                baseType = "NUMBER",
                aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
                        "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:NEVER",
                        "defaultValue:70" }) })

/**
 * A very basic VirtualThing with two properties and a service implementation. It also implements
 * processScanRequest to handle periodic actions.
 */
public class SimpleThing extends VirtualThing {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleThing.class);

    /**
     * A custom constructor. We implement this so we can call initializeFromAnnotations, which
     * processes all of the VirtualThing's annotations and applies them to the object.
     *
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thing is associated with.
     */
    public SimpleThing(String name, String description, ConnectedThingClient client)
            throws Exception {

        super(name, description, client);
        this.initializeFromAnnotations();

        try {
            this.setPropertyValue("SetPoint", new IntegerPrimitive(70));
        } catch (Exception e) {
            LOG.warn("Could not ser default value for SetPoint");
        }
    }

    /**
     * This method provides a common interface amongst VirtualThings for processing periodic
     * requests. It is an opportunity to access data sources, update property values, push new
     * values to the server, and take other actions.
     */
    @Override
    public void processScanRequest() {

        // We'll use this to generate a random temperature and humidity value.
        // On an actual system you would access a sensor or some other data source.
        Random random = new Random();

        int temperature = 50 + random.nextInt(51);
        int humidity = random.nextInt(101);

        try {

            // Here we set the thing's internal property values to the new values
            // that we accessed above. This does not update the server. It simply
            // sets the new property value in memory.
            this.setPropertyValue("Temperature", new IntegerPrimitive(temperature));
            this.setPropertyValue("Humidity", new IntegerPrimitive(humidity));

            // This call evaluates all properties and determines if they should be pushed
            // to the server, based on their pushType aspect. A pushType of ALWAYS means the
            // property will always be sent to the server when this method is called. A
            // setting of VALUE means it will be pushed if has changed since the last
            // push. A setting of NEVER means it will never be pushed.
            //
            // Our Temperature property is set to ALWAYS, so its value will be pushed
            // every time processScanRequest is called. This allows the platform to get
            // periodic updates and store the time series data. Humidity is set to
            // VALUE, so it will only be pushed if it changed.
            this.updateSubscribedProperties(10000);

        } catch (Exception e) {
            // This will occur if we provide an unknown property name. We'll ignore
            // the exception in this case and just log it.
            LOG.error("Exception occurred while updating properties.", e);
        }
    }

    /**
     * This is where we handle property writes from the server. The only property we want to update
     * is the SetPoint. Temperature and Humidity write requests should be rejected, since their
     * values are controlled from within this application.
     *
     * @see VirtualThing#processPropertyWrite(PropertyDefinition, IPrimitiveType)
     */
    @Override
    public void processPropertyWrite(PropertyDefinition property,
            @SuppressWarnings("rawtypes") IPrimitiveType value) throws Exception {

        // Find out which property is being updated
        String propName = property.getName();

        if (!"SetPoint".equals(propName)) {
            throw new Exception("The property " + propName + " is read only on the simple device.");
        }

        this.setPropertyValue(propName, value);
    }

    // The following annotation allows you to make a method available to the
    // ThingWorx Server for remote invocation. The annotation includes the
    // name of the server, the name and base types for its parameters, and
    // the base type of its result.
    @ThingworxServiceDefinition(name = "Add", description = "Add two numbers")
    @ThingworxServiceResult(name = "result", description = "The sum of the two parameters",
            baseType = "NUMBER")
    public Double Add(
            @ThingworxServiceParameter(name = "p1",
                    description = "The first addend of the operation",
                    baseType = "NUMBER") Double p1,
            @ThingworxServiceParameter(name = "p2",
                    description = "The second addend of the operation",
                    baseType = "NUMBER") Double p2)
            throws Exception {

        LOG.info("Adding the numbers {} and {}", p1, p2);
        return p1 + p2;
    }
}
