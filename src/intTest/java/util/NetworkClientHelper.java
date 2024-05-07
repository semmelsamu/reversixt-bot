package util;

import clients.Client;
import move.Move;
import network.Launcher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class NetworkClientHelper {

    private static final List<Client> clients = new ArrayList<>();

    public static void createNetworkClients(Client client, int numClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Thread(() -> {
                try {
                    Client spy = spy(client);
                    clients.add(spy);
                    Launcher.launchClientOnNetwork(spy, "127.0.0.1", 7777);
                } catch (Exception e) {
                    fail(e.getMessage());
                }

            });
            threads.add(clientThread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static void validateMove(Move move) {
        verify(clients.get(move.getPlayerNumber() - 1), times(1)).sendMove(anyInt(), anyInt());
    }
}
