package util;

import board.Tile;
import clients.Client;
import game.Game;
import network.Launcher;
import network.NetworkClientAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkClientHelper {

    NetworkClientAdapter client;

    public NetworkClientHelper(String path) {
        NetworkClientAdapter client = new NetworkClientAdapter(new ClientDummy());
        client.receiveMap(readFileAsString(path));
        this.client = client;
    }

    private static String readFileAsString(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator()); // Füge Zeilenumbruch hinzu
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public void receiveMove(int x, int y, int type, Tile player) {
        client.receiveMove((short) x, (short) y, (byte) type, tileToUint8(player));
    }

    public static byte tileToUint8(Tile tile) {
        return (byte) (tile.character - '0');
    }

    public Game getGame() {
        return client.getGame();
    }

    public static void createNetworkClients(Client client, int numClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Thread(() -> {
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
}
