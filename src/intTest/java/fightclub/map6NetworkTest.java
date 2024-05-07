package fightclub;

import clients.ParanoidClient;
import game.Game;
import game.MoveCalculator;
import game.MoveExecutor;
import network.NetworkClientAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Logger;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class map6NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        Logger.setPriority(MoveCalculator.class.getName(), 3);
        Logger.setPriority(MoveExecutor.class.getName(), 3);
        Logger.setPriority(Game.class.getName(), 3);
        Logger.setPriority(NetworkClientAdapter.class.getName(), 3);
        server = new NetworkServerHelper();
        server.startServer("maps/fightclub/map6.map");
    }


    @Test
    public void paranoidClient_depth2_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new ParanoidClient(2), 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();
    }
}
