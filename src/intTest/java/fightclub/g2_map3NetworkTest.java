package fightclub;

import clients.IterativeDeepeningAlphaBetaSearchClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class g2_map3NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        server = new NetworkServerHelper();
        server.startServer("maps/fightclub/g2_map3.map", 1);
    }

    @Test
    public void paranoidClient_depth3_test() throws InterruptedException {
        NetworkClientHelper.createNetworkClients(new IterativeDeepeningAlphaBetaSearchClient(true),
                1, 4);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();
    }
}
