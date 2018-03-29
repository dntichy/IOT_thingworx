package com.thingworx.sdk.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.relationships.RelationshipTypes.ThingworxEntityTypes;
import com.thingworx.sdk.simplething.SimpleThing;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.DatetimePrimitive;
import com.thingworx.types.primitives.LocationPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

public class SimpleClient extends ConnectedThingClient {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleClient.class);

    private static String ThingName = "ThingTemperature_dntichy";

    public SimpleClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) {

        ClientConfigurator config = new ClientConfigurator();

        // Set the URI of the server that we are going to connect to.
        // You must include the port number in the URI.
        config.setUri("wss://academic-educatorsextension.portal.ptc.io:443/Thingworx/WS");
//        config.setUri("wss://academic.cloud.thingworx.com");

        // Set the ApplicationKey. This will allow the client to authenticate with the server.
        // It will also dictate what the client is authorized to do once connected.
        config.setAppKey("51da8847-9985-4f66-8b4c-a25a610572b0");

        // This will allow us to test against a server using a self-signed certificate.
        // This should be removed for production systems.
        config.ignoreSSLErrors(true); // All self signed certs

        try {

            // Create our client.
            SimpleClient client = new SimpleClient(config);

            // Start the client. The client will connect to the server and authenticate
            // using the ApplicationKey specified above.
            client.start();

            // Wait for the client to connect.
            if (client.waitForConnection(30000)) {

                LOG.info("The client is now connected.");

                // We may now interact with the ThingWorx Server
                InfoTable result;

                //
                // Reading a property of a Thing
                ///////////////////////////////////////////////////////////////

                // Request a property from a Thing on the Platform. Here we access the 'name'
                // property of a Thing.
                result = client.readProperty(ThingworxEntityTypes.Things, ThingName, "name", 10000);

                // Result is returned as an InfoTable, so we must extract the value. An InfoTable
                // is a collection of one or more rows. A row can have multiple fields. Each
                // field has a name and a base type. In this case, the field name is 'name' and
                // the base type is STRING, so we can use the getStringValue() helper.
                String name = result.getFirstRow().getStringValue("name");

                LOG.info("The name of the Thing {} is: {}", ThingName, name);

                // We can also access the value as a Primitive. This will work for all primitive
                // types.
                StringPrimitive prim = (StringPrimitive) result.getFirstRow().getPrimitive("name");

                LOG.info("The name of the Thing {} is: {}", ThingName, prim.getValue());

                //
                // Writing a property
                ///////////////////////////////////////////////////////////////

                LocationPrimitive location = new LocationPrimitive(42.36, -71.06, 10.0);

                // This will set the CurrentLocation property of the Thing to the GPS
                // coordinates of Boston, MA.
                client.writeProperty(ThingworxEntityTypes.Things, ThingName, "CurrentLocation",
                        location, 5000);

                LOG.info("Wrote to the property 'CurrentLocation' of Thing {}. value: {}",
                        ThingName, location.toString());

                //
                // Invoking a service on a Thing
                ///////////////////////////////////////////////////////////////

                // A ValueCollection is used to specify a service's parameters
//                ValueCollection params = new ValueCollection();
//
//                params.put("path", new StringPrimitive("/simple.txt"));
//                params.put("data", new StringPrimitive("Here is the contents of the file."));
//                params.put("overwrite", new BooleanPrimitive(true));

                // Use the SystemRepository Thing to create a text file on the Platform.
                // This service's result type is NOTHING, so we can ignore the response.
//                client.invokeService(ThingworxEntityTypes.Things, "SystemRepository",
//                        "CreateTextFile", params, 5000);

                // If a service does have a result, it is returned within an InfoTable.
//                params.clear(); // Clear the params used in the previous service invocation.
//                params.put("path", new StringPrimitive("/simple.txt"));

                // This service queries the SystemRepository for information about the file
                // we just created.
//                result = client.invokeService(ThingworxEntityTypes.Things, "SystemRepository",
//                        "GetFileInfo", params, 5000);

                // The rows of an InfoTable are ValueCollections.
//                ValueCollection row = result.getFirstRow();
//
//                LOG.info("The file info is: name: {}", row.getStringValue("name"));
//                LOG.info("                  path: {}", row.getStringValue("path"));
//                LOG.info("                  type: {}", row.getStringValue("fileType"));
//                LOG.info("                  date: {}",
//                        row.getPrimitive("lastModifiedDate").getStringValue());
//                LOG.info("                  size: {}", row.getValue("size"));

                //
                // Firing an event
                ///////////////////////////////////////////////////////////////

                // A ValueCollection is used to specify a event's payload
//                ValueCollection payload = new ValueCollection();
//
//                payload.put("name", new StringPrimitive("FileName"));
//                payload.put("path", new StringPrimitive("/file.txt"));
//                payload.put("fileType", new StringPrimitive("F"));
//                payload.put("lastModifiedDate", new DatetimePrimitive());
//                payload.put("size", new NumberPrimitive(256));

                // This will trigger the 'FileEvent' of a RemoteThing on the Platform.
//                client.fireEvent(ThingworxEntityTypes.Things, ThingName, "FileEvent", payload,
//                        5000);

                //
                // Create a VirtualThing and bind it to the client
                ///////////////////////////////////////////////////////////////

                // Create a new VirtualThing. The name parameter should correspond with the
                // name of a RemoteThing on the Platform.
                SimpleThing thing = new SimpleThing("ThingTemperature_dntichy", "A basic virtual thing", client);

                // Bind the VirtualThing to the client. This will tell the Platform that
                // the RemoteThing 'Simple1' is now connected and that it is ready to
                // receive requests.
                client.bindThing(thing);
                thing.processScanRequest();


            } else {
                // Log this as a warning. In production the application could continue
                // to execute, and the client would attempt to reconnect periodically.
                LOG.warn("Client did not connect within 30 seconds. Exiting");
            }

            client.shutdown();

        } catch (Exception e) {
            LOG.error("An exception occurred while initializing the client", e);
        }

        LOG.info("SimpleClient is done. Exiting");
    }
}
