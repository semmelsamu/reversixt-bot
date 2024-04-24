package util;

import board.Tile;
import clients.Client;
import clients.RandomMoveClient;
import game.Game;
import network.Launcher;
import network.NetworkClientAdapter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    public static void create2NetworkClients(Client client) throws InterruptedException {
        Thread client1Thread = new Thread(() -> {
            Launcher.launchClientOnNetwork(client, "127.0.0.1", 7777);
        });

        Thread client2Thread = new Thread(() -> {
            Launcher.launchClientOnNetwork(client, "127.0.0.1", 7777);
        });

        client1Thread.start();
        client2Thread.start();

        client1Thread.join();
        client2Thread.join();
    }
}
