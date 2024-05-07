package util;

import clients.Client;
import move.Move;
import network.Launcher;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

public class NetworkClientHelper {


    private static Logger loggerSpy;

    private static final List<Client> clients = new ArrayList<>();

    public static void createNetworkClients(Client client, int numClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Thread(() -> {
                clients.add(client);
                Launcher.launchClientOnNetwork(client, "127.0.0.1", 7777);
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

    public static void spyLogger() throws NoSuchFieldException, IllegalAccessException {
        loggerSpy = mock(Logger.class);
    }

    public static void verifyLogger() throws NoSuchFieldException, IllegalAccessException {
        Mockito.verify(loggerSpy, Mockito.times(0)).error(anyString());
        Mockito.verify(loggerSpy, Mockito.times(0)).fatal(anyString());
    }

    public static void validateMove(Move move) {
        List<Move> moves = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(clients.size());

        for (Client client : clients) {
            executorService.execute(() -> {
                moves.add(client.sendMove(any(), move.getPlayerNumber(), 0, 0));
            });
        }

        executorService.shutdown();

        assertTrue(moves.contains(move), "Move was executed");
    }
}
