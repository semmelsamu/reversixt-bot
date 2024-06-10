package exercises;

import clients.CommunitiesClient;
import network.Launcher;

public class Exercise10 {
    public static void abnahme(String ip, int port) {
        Launcher.launchClientOnNetwork(new CommunitiesClient(), ip, port);
    }
}
