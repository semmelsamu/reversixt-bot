package util;

import clients.Client;
import network.Launcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class NetworkClientHelper {


    private static Logger loggerSpy;

    public static void createNetworkClients(Client client, int numClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numClients; i++) {
            Thread clientThread = new Thread(() -> {
                Launcher.launchClientOnNetwork(client, "127.0.0.1", 7777);
            });
            threads.add(clientThread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    public static void createClientLogger() throws NoSuchFieldException, IllegalAccessException {
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

    private static Answer<Void> createLoggingAnswer(Consumer<String> logMethod) {
        return invocation -> {
            String message = invocation.getArgument(0);
            logMethod.accept(message);
            return null;
        };
    }

    public static void terminateLogger() throws NoSuchFieldException, IllegalAccessException {
        Mockito.verify(loggerSpy, Mockito.times(0)).error(anyString());
        Mockito.verify(loggerSpy, Mockito.times(0)).fatal(anyString());

        Field instance = Logger.class.getDeclaredField("logger");
        instance.setAccessible(true);
        instance.set(null, null);
    }
}
