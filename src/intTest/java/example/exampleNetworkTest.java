package example;

import clients.BestReplySearchKillerHeuristicClient;
import clients.CommunitiesClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class exampleNetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        server = new NetworkServerHelper();
        server.startServer("maps/exampleIntTest.map", 2);
    }

    @Test
    public void time_2_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new CommunitiesClient(), 1, 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();
    }
}
