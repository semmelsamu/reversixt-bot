package boeseMaps;

import clients.RandomMoveClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Logger;
import util.NetworkClientHelper;
import util.NetworkServerHelper;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class boeseMap01NetworkTest {

    private Logger loggerSpy;

    private NetworkServerHelper server;

    @BeforeEach
    public void setUp()
            throws NoSuchFieldException, IllegalAccessException, IOException, InterruptedException {
        loggerSpy = spy(Logger.get());

        // Verwende Reflection, um das private Feld zu finden
        Field field = Logger.class.getDeclaredField("logger");
        field.setAccessible(true); // Mache das private Feld zugänglich
        field.set(null, loggerSpy);

        server = new NetworkServerHelper();
        server.startServer("maps/boeseMaps/boeseMap01.map");

    }


    @Test
    public void randomClient_test() throws InterruptedException {

        NetworkClientHelper.create2NetworkClients(new RandomMoveClient());

    }


    @AfterEach
    public void tearDown() {

        server.stopServer();

        verify(loggerSpy, times(0)).error(anyString());
        verify(loggerSpy, times(0)).fatal(anyString());
    }
}
