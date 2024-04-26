package network;

import clients.RandomMoveClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import util.Logger;
import util.NetworkClientHelper;
import util.NetworkServerHelper;
import util.TestLogger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class boeseMap02NetworkTest {

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        NetworkClientHelper.createClientLogger();
    }

    @Test
    public void randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap02.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        server.stopServer();

        NetworkClientHelper.terminateLogger();
    }
}
