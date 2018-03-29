
package com.thingworx.sdk.steam;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeEvent;
import com.thingworx.communications.client.things.VirtualThingPropertyChangeListener;
import com.thingworx.communications.common.SecurityClaims;

// Refer to the "Steam Sensor Example" section of the documentation
// for a detailed explanation of this example's operation
public class SteamSensorClient extends ConnectedThingClient {
    public SteamSensorClient(ClientConfigurator config) throws Exception {
        super(config);
    }

    // Test example
    public static void main(String[] args) throws Exception {
        args = new String[4];
//        if (args.length < 3) {
//            System.out.println("Required arguments not found!");
//            System.out.println("URI AppKey ScanRate <StartSensor> <Number Of Sensors>");
//            System.out.println("Example:");
//            System.out.println("ws://localhost:80/Thingworx/WS xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx 1000 1 10");
//            return;
//        }

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
        config.setName("SteamSensorGateway");
        // This client is a SDK
        config.setAsSDKType();

        // This will allow us to test against a server using a self-signed certificate.
        // This should be removed for production systems.
        config.ignoreSSLErrors(true); // All self signed certs

        // Get the scan rate (milliseconds) that is specific to this example
        // The example will execute the processScanRequest of the VirtualThing
        // based on this scan rate
        int scanRate = Integer.parseInt(args[2]);

        int startSensor = 0;
        int nSensors = 2;

        if (args.length == 5) {
            startSensor = Integer.parseInt(args[3]);
            nSensors = Integer.parseInt(args[4]);
        }

        // Create the client passing in the configuration from above
        SteamSensorClient client = new SteamSensorClient(config);

        for (int sensor = 0; sensor < nSensors; sensor++) {
            int sensorID = startSensor + sensor;
            final SteamThing steamSensorThing = new SteamThing("ThingTemperature_dntichy", "Steam Sensor #" + sensorID, "SN000", client);
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
        }

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
