package boeseMaps;

import clients.RandomMoveClient;
import network.Launcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class boeseMap01NetworkTest {

    private static Process serverProcess;

    private Logger loggerSpy;

    @BeforeEach
    public void setUp()
            throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        loggerSpy = spy(Logger.get());

        // Verwende Reflection, um das private Feld zu finden
        Field field = Logger.class.getDeclaredField("logger");
        field.setAccessible(true); // Mache das private Feld zugänglich
        field.set(null, loggerSpy);

        Semaphore semaphore = new Semaphore(0);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        Path currentDirectory = Paths.get(System.getProperty("user.dir"));

        Path serverParameterPath =
                currentDirectory.resolve("maps/boeseMaps/boeseMap01.map").toAbsolutePath();
        Path serverBinaryPath =
                currentDirectory.resolve("binaries/x86/server_nogl").toAbsolutePath();


        // Start the server process in WSL
        ProcessBuilder processBuilder =
                new ProcessBuilder("wsl", convertWindowsPathToWSL(serverBinaryPath.toString()),
                        convertWindowsPathToWSL(serverParameterPath.toString()));
        serverProcess = processBuilder.start();

        // Start a thread to continuously read and print the server output
        Thread outputReaderThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(serverProcess.getInputStream()))) {
                int c;
                boolean released = false;
                StringBuilder lineBuilder = new StringBuilder();
                while ((c = reader.read()) != -1) {
                    System.out.print((char) c);
                    lineBuilder.append((char) c);
                    if (!released && lineBuilder.toString().contains("7778")) {
                        semaphore.release();
                        fail("Port instance is already running");
                    }
                    if (!released &&
                            lineBuilder.toString().contains("Waiting client 1 to connect...")) {
                        released = true;
                        executorService.schedule(() -> semaphore.release(), 1, TimeUnit.SECONDS);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outputReaderThread.start();
        semaphore.acquire();
        executorService.shutdown();
    }

    public static String convertWindowsPathToWSL(String windowsPath) throws IOException {
        String unixStylePath = windowsPath.replace("\\", "/");

        if (unixStylePath.matches("[A-Za-z]:.*")) {
            String driveLetter = unixStylePath.substring(0, 1).toLowerCase();
            unixStylePath = "/mnt/" + driveLetter + unixStylePath.substring(2);
        }
        return unixStylePath;
    }


    @Test
    public void moveCalculator_test() throws InterruptedException {

        Thread client1Thread = new Thread(() -> {
            Launcher.launchClientOnNetwork(new RandomMoveClient(), "127.0.0.1", 7777);
        });

        Thread client2Thread = new Thread(() -> {
            Launcher.launchClientOnNetwork(new RandomMoveClient(), "127.0.0.1", 7777);
        });

        client1Thread.start();
        client2Thread.start();

        client1Thread.join();
        client2Thread.join();


    }


    @AfterEach
    public void tearDown() {

        // Stop the server process
        assertNotNull(serverProcess, "serverProcess is null");
        serverProcess.destroy();


        // Optional: Wait for the server process to terminate
        try {
            serverProcess.waitFor(5,
                    TimeUnit.SECONDS); // Wait for 5 seconds for the server process to terminate
        } catch (InterruptedException e) {
            // Handle the interruption if needed
            Thread.currentThread().interrupt();
        }
        verify(loggerSpy, times(0)).error(anyString());
        verify(loggerSpy, times(0)).fatal(anyString());
    }
}
