package exercises;

import clients.GameLengthClient;
import network.Launcher;

public class Exercise11 {
    public static void abnahme(String ip, int port) {
        Launcher.launchClientOnNetwork(new GameLengthClient(), ip, port);
    }
}
