package com.thingworx.sdk.simple;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.filetransfer.FileTransferVirtualThing;

public class FileTransferExample {

    // Substitute your thing name here
    private static final String ThingName = "FileTransferExample";

    public static void main(String[] args) {

        // Create a client config
        ClientConfigurator config = new ClientConfigurator();

        // Basic configuration. See SimpleClient.java for additional info.
        config.setUri("wss://localhost:443/Thingworx/WS");
        config.setAppKey("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
        config.ignoreSSLErrors(true);

        try {

            ConnectedThingClient client = new ConnectedThingClient(config);

            FileTransferVirtualThing myThing =
                    new FileTransferVirtualThing(ThingName, "File Transfer Example", client);

            // Add two virtual directories that will act as the root directories in this
            // application's virtual file system.
            myThing.addVirtualDirectory("in", "/home/username/files/in");
            myThing.addVirtualDirectory("out", "/home/username/files/out");

            client.bindThing(myThing);
            client.start();

            while (!client.isShutdown()) {
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
