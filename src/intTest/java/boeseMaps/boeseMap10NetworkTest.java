package boeseMaps;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;

public class boeseMap10NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap10.map", 2);
    }

    @Test
    public void time_2_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(1, 2);
    }

    @AfterEach
    public void tearDown()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        server.stopServer();
    }
}
