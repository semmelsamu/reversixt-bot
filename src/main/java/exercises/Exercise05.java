package exercises;

import clients.ParanoidClient;
import clients.RandomMoveClient;
import network.Launcher;
public class Exercise05 {

    public static void abnahme(String ip, int port) {
        Launcher.launchClientOnNetwork(new ParanoidClient(), ip, port);
    }
}
