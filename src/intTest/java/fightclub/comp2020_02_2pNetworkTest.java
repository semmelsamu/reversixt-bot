package fightclub;

import clients.IterativeDeepeningAlphaBetaSearchClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class comp2020_02_2pNetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        server = new NetworkServerHelper();
        server.startServer("maps/fightclub/comp2020_02_2p.map", 2);
    }


    @Test
    public void time_2_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new IterativeDeepeningAlphaBetaSearchClient(true),
                1, 1);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();
    }
}
