package boeseMaps;

import clients.ParanoidClient;
import clients.RandomMoveClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class boeseMap07NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        NetworkClientHelper.spyLogger();
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap07.map");
    }

    @Test
    public void randomClient_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void paranoidClient_depth3_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new ParanoidClient(3), 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();

        NetworkClientHelper.verifyLogger();
    }
}
