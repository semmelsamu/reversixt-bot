package network;

import clients.RandomMoveClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.Logger;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class boeseMapsLoggerNetworkTest {

    private static Logger loggerSpy;

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        loggerSpy = Mockito.mock(Logger.class);

        // Verwende Reflection, um das private Feld zu finden
        Field field = Logger.class.getDeclaredField("logger");
        field.setAccessible(true); // Mache das private Feld zugänglich
        field.set(field, loggerSpy);


    }


    @Test
    public void boeseMap01_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap01.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap02_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap02.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap03_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap03.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap04_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap04.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap05_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap05.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap06_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap06.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap07_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap07.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap08_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap08.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap09_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap09.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @Test
    public void boeseMap10_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap10.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 3);
    }

    @Test
    public void boeseMap11_randomClient_test() throws InterruptedException, IOException {
        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap11.map");
        NetworkClientHelper.createNetworkClients(new RandomMoveClient(), 2);
    }

    @AfterEach
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {

        server.stopServer();

        verify(loggerSpy, times(0)).error(anyString());
        verify(loggerSpy, times(0)).fatal(anyString());

        Field instance = Logger.class.getDeclaredField("logger");
        instance.setAccessible(true);
        instance.set(null, null);
    }
}
