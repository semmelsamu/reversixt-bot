package network;

import clients.Client;
import clients.RandomMoveClient;

import java.io.IOException;

/**
 * The only purpose of this class is to make the code in the main shorter lol
 */
public class Launcher {

    public static void launchClientOnNetwork(Client client, String ip, int port)
            throws IOException {
        new NetworkEventHandler(ip, port, new NetworkClientAdapter(client));
    }

}
