package network;

import clients.Client;
import clients.RandomMoveClient;
import util.Logger;

import java.io.IOException;

/**
 * The only purpose of this class is to make the code in the main shorter lol
 */
public class Launcher {

    public static void launchClientOnNetwork(Client client, String ip, int port) {
        new NetworkEventHandler(new NetworkClientAdapter(client), ip, port);
    }

}
