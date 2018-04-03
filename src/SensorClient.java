

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeEvent;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeListener;
import com.thingworx.communications.common.SecurityClaims;

// Refer to the "Steam Sensor Example" section of the documentation
// for a detailed explanation of this example's operation
public class SensorClient extends ConnectedThingClient {
    public SensorClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    // Test example
    public static void main(String[] args) throws Exception {
        args = new String[3];
        args[0] = "wss://academic-educatorsextension.portal.ptc.io:443/Thingworx/WS";
        args[1] = "51da8847-9985-4f66-8b4c-a25a610572b0";
        args[2] = "1000";

        // Set the required configuration information
        ClientConfigurator config = new ClientConfigurator();
        // The uri for connecting to Thingworx
        config.setUri(args[0]);
        // Reconnect every 15 seconds if a disconnect occurs or if initial connection cannot be made
        config.setReconnectInterval(15);

        // Set the security using an Application Key
        String appKey = args[1];
        SecurityClaims claims = SecurityClaims.fromAppKey(appKey);
        config.setSecurityClaims(claims);

        // Set the name of the client
        config.setName("SensorGateway");
        // This client is a SDK
        config.setAsSDKType();

        // This will allow us to test against a server using a self-signed certificate.
        // This should be removed for production systems.
        config.ignoreSSLErrors(true); // All self signed certs

        // Get the scan rate (milliseconds) that is specific to this example
        // The example will execute the processScanRequest of the VirtualThing
        // based on this scan rate
        int scanRate = Integer.parseInt(args[2]);


        int nSensors = 2;

        // Create the client passing in the configuration from above
        SensorClient client = new SensorClient(config);

//        for (int sensor = 0; sensor < nSensors; sensor++) {

            final SteamThing steamSensorThing = new SteamThing("ThingTemperature_dntichy", "Senzor id: ...dorob ID ak viac senzorov" , "SN000", client);
            client.bindThing(steamSensorThing);

            steamSensorThing.addPropertyChangeListener(new VirtualThingPropertyChangeListener() {
                @Override
                public void propertyChangeEventReceived(VirtualThingPropertyChangeEvent evt) {
                    if ("TemperatureLimit".equals(evt.getPropertyDefinition().getName())) {
                        System.out.println(String.format("Temperature limit on %s has been changed to %s°.", steamSensorThing.getName(),
                            evt.getPrimitiveValue().getValue()));
                    }
                }
            });
//        }

        try {
            // Start the client
            client.start();
        } catch (Exception eStart) {
            System.out.println("Initial Start Failed : " + eStart.getMessage());
        }

        // As long as the client has not been shutdown, continue
        while (!client.isShutdown()) {
            // Only process the Virtual Things if the client is connected
            if (client.isConnected()) {
                // Loop over all the Virtual Things and process them
                for (VirtualThing thing : client.getThings().values()) {
                    try {
                        thing.processScanRequest();
                    } catch (Exception eProcessing) {
                        System.out.println("Error Processing Scan Request for [" + thing.getName() + "] : " + eProcessing.getMessage());
                    }
                }
            }
            // Suspend processing at the scan rate interval
            Thread.sleep(scanRate);
        }
    }
}
