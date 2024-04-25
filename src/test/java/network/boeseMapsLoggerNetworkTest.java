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

public class boeseMapsLoggerNetworkTest {

    private Logger loggerSpy;

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        loggerSpy = mock(Logger.class);

        Answer<Void> logAnswer = createLoggingAnswer(TestLogger.get()::log);
        Answer<Void> errorAnswer = createLoggingAnswer(TestLogger.get()::error);
        Answer<Void> debugAnswer = createLoggingAnswer(TestLogger.get()::debug);
        Answer<Void> fatalAnswer = createLoggingAnswer(TestLogger.get()::fatal);
        Answer<Void> warnAnswer = createLoggingAnswer(TestLogger.get()::warn);

        // Set the behaviors on the mock logger
        doAnswer(logAnswer).when(loggerSpy).log(anyString());
        doAnswer(errorAnswer).when(loggerSpy).error(anyString());
        doAnswer(debugAnswer).when(loggerSpy).debug(anyString());
        doAnswer(fatalAnswer).when(loggerSpy).fatal(anyString());
        doAnswer(warnAnswer).when(loggerSpy).warn(anyString());


        // Verwende Reflection, um das private Feld zu finden
        Field field = Logger.class.getDeclaredField("logger");
        field.setAccessible(true); // Mache das private Feld zugänglich
        field.set(field, loggerSpy);


    }

    private Answer<Void> createLoggingAnswer(Consumer<String> logMethod) {
        return invocation -> {
            String message = invocation.getArgument(0);
            logMethod.accept(message);
            return null;
        };
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
