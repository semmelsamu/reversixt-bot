package util;

import clients.Client;
import move.Move;
import network.Launcher;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NetworkClientHelper {

    private static Logger loggerSpy;

    private static final List<Client> clients = new ArrayList<>();

    public static void createNetworkClients(Client client, int numClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Thread(() -> {
                Client spy = spy(client);
                clients.add(spy);
                Launcher.launchClientOnNetwork(spy, "127.0.0.1", 7777);
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
        Mockito.verify(loggerSpy, Mockito.times(0)).warn(anyString());
        Mockito.verify(loggerSpy, Mockito.times(0)).error(anyString());
        Mockito.verify(loggerSpy, Mockito.times(0)).fatal(anyString());
    }

    public static void validateMove(Move move) {
        verify(clients.get(move.getPlayerNumber() - 1), times(1)).sendMove(any(),
                eq(move.getPlayerNumber()));
    }
}
