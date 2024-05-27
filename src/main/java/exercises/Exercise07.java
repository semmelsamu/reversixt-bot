package exercises;

import clients.IterativeDeepeningAlphaBetaSearchClient;
import network.Launcher;

public class Exercise07 {
    public static void abnahme(String ip, int port, boolean moveSorting) {
        Launcher.launchClientOnNetwork(new IterativeDeepeningAlphaBetaSearchClient(moveSorting), ip,
                port);
    }
}
