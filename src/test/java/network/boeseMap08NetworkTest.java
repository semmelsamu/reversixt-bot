package network;

import clients.RandomMoveClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class boeseMap08NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        NetworkClientHelper.createClientLogger();
    }

    @Test
    public void randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap08.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();

        NetworkClientHelper.terminateLogger();
    }
}
