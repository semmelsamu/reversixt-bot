package boeseMaps;

import board.Coordinates;
import clients.OptimizedParanoidClient;
import clients.ParanoidClient;
import clients.RandomMoveClient;
import move.InversionMove;
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
        server.startServer("maps/boeseMaps/boeseMap10.map", 3);
    }

    @Test
    public void randomClient_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 3);
    }

    @Test
    public void paranoidClient_depth3_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new ParanoidClient(), 3);
        NetworkClientHelper.validateMove(new InversionMove(1, new Coordinates(4, 4)));
    }

    @Test
    public void optimizedParanoidClient_depth3_test() throws InterruptedException, IOException {
        NetworkClientHelper.createNetworkClients(new OptimizedParanoidClient(), 3);
        NetworkClientHelper.validateMove(new InversionMove(1, new Coordinates(4, 4)));
    }

    @AfterEach
    public void tearDown()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        server.stopServer();
    }
}
