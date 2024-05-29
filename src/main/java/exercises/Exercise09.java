package exercises;

import clients.BestReplySearchKillerHeuristicClient;
import network.Launcher;

public class Exercise09 {
    public static void abnahme(String ip, int port) {
        Launcher.launchClientOnNetwork(new BestReplySearchKillerHeuristicClient(), ip, port);
    }
}
