package exercises;

import clients.OptimizedParanoidClient;
import network.Launcher;

public class Exercise06 {
    public static void abnahme(String ip, int port, int depth) {
        Launcher.launchClientOnNetwork(new OptimizedParanoidClient(depth), ip, port);
    }
}
