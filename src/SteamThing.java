

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.constants.CommonPropertyNames;
import org.joda.time.DateTime;

// Refer to the "Steam Sensor Example" section of the documentation
// for a detailed explanation of this example's operation

// Property Definitions- toto musi byt pre kazdu prop.
@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
        @ThingworxPropertyDefinition(name = "Temperature", description = "Aktuálna teplota",
                baseType = "NUMBER"),

        @ThingworxPropertyDefinition(name = "Timestamp", description = "Aktuálny čas",
                baseType = "DATETIME")
})

// Steam Thing virtual thing class that simulates a Steam Sensor
public class SteamThing extends VirtualThing implements Runnable {

    private int counter = 0;


    public SteamThing(String name, String description, String identifier,
                      ConnectedThingClient client) throws Exception {
        super(name, description, identifier, client);

        // Data Shape definition that is used by the steam sensor fault event
        // The event only has one field, the message
        FieldDefinitionCollection faultFields = new FieldDefinitionCollection();
        faultFields.addFieldDefinition(
                new FieldDefinition(CommonPropertyNames.PROP_MESSAGE, BaseTypes.STRING));
        defineDataShapeDefinition("SteamSensor.Fault", faultFields);

        // Populate the thing shape with the properties, services, and events that are annotated in
        // this code
        super.initializeFromAnnotations();

    }

    // From the VirtualThing class
    // This method will get called when a connect or reconnect happens
    // Need to send the values when this happens
    // This is more important for a solution that does not send its properties on a regular basis
    public void synchronizeState() {
        // Be sure to call the base class
        super.synchronizeState();
        // Send the property values to Thingworx when a synchronization is required
        super.syncProperties();
    }

    // The processScanRequest is called by the SensorClient every scan cycle
    @Override
    public void processScanRequest() throws Exception {
        // Be sure to call the base classes scan request
        super.processScanRequest();
        // Execute the code for this simulation every scan
        this.scanDevice();
    }

    // Performs the logic for the steam sensor, occurs every scan cycle
    public void scanDevice() throws Exception {
        ++counter;

        if ((counter % 1) == 0) {
            // Set the Temperature property value in the range of 400-440
            double temperature = 400 + 5 * Math.random();
            super.setProperty("Temperature", temperature);

        }
        if ((counter % 2) == 0) {
            DateTime dateTime = new DateTime();
            super.setProperty("Timestamp", dateTime);
        }


        // Update the subscribed properties and events to send any updates to Thingworx
        // Without calling these methods, the property and event updates will not be sent
        // The numbers are timeouts in milliseconds.
        super.updateSubscribedProperties(15000);
        super.updateSubscribedEvents(60000);
    }

//    @ThingworxServiceDefinition(name = "AddNumbers", description = "Add Two Numbers")
//    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result",
//            baseType = "NUMBER")
//    public Double AddNumbers(
//            @ThingworxServiceParameter(name = "a", description = "Value 1",
//                    baseType = "NUMBER") Double a,
//            @ThingworxServiceParameter(name = "b", description = "Value 2",
//                    baseType = "NUMBER") Double b)
//            throws Exception {
//
//        return a + b;
//    }

    @Override
    public void run() {
        try {
            // Delay for a period to verify that the Shutdown service will return
            Thread.sleep(1000);
            // Shutdown the client
            this.getClient().shutdown();
        } catch (Exception x) {
            // Not much can be done if there is an exception here
            // In the case of production code should at least log the error
        }
    }
}
