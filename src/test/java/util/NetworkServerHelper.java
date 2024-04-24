package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class NetworkServerHelper {

    private static Process serverProcess;

    public void startServer(String map) throws InterruptedException, IOException {
        Semaphore semaphore = new Semaphore(0);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        Path currentDirectory = Paths.get(System.getProperty("user.dir"));

        Path serverParameterPath = currentDirectory.resolve(map).toAbsolutePath();
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
                StringBuilder portBuilder = new StringBuilder();
                while (serverProcess.isAlive()) {
                    if (reader.ready()) {
                        c = reader.read();

                        if (c == -1) {
                            break;
                        }

                        if (!released && lineBuilder.toString().contains("Port number is ")) {
                            portBuilder.append((char) c);
                            if (portBuilder.toString().length() == 4 &&
                                    Integer.parseInt(portBuilder.toString()) != 7777) {
                                semaphore.release();
                                fail("Another Port instance is already running");
                            }
                        }
                        System.out.print((char) c);
                        lineBuilder.append((char) c);
                        if (!released &&
                                lineBuilder.toString().contains("Opening port...FAILED.")) {
                            semaphore.release();
                            fail("Port instance is already running");
                        }
                        if (!released &&
                                lineBuilder.toString().contains("Waiting client 1 to connect...")) {
                            released = true;
                            executorService.schedule(() -> semaphore.release(), 1,
                                    TimeUnit.SECONDS);

                        }
                    } else {
                        // Sleep für eine kurze Zeit, um die CPU zu entlasten, wenn keine Daten
                        // verfügbar sind
                        Thread.sleep(100);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        outputReaderThread.start();
        semaphore.acquire();
        executorService.shutdown();
    }


    private String convertWindowsPathToWSL(String windowsPath) {
        String unixStylePath = windowsPath.replace("\\", "/");

        if (unixStylePath.matches("[A-Za-z]:.*")) {
            String driveLetter = unixStylePath.substring(0, 1).toLowerCase();
            unixStylePath = "/mnt/" + driveLetter + unixStylePath.substring(2);
        }
        return unixStylePath;
    }

    public void stopServer() {
        // Stop the server process
        assertNotNull(serverProcess, "serverProcess is null");
        serverProcess.destroy();

        try {
            stopWSLApplication();
        } catch (IOException | InterruptedException e) {
            // Handle exceptions if needed
            e.printStackTrace();
        }

        // Optional: Wait for the server process to terminate
        try {
            serverProcess.waitFor(5,
                    TimeUnit.SECONDS); // Wait for 5 seconds for the server process to terminate
        } catch (InterruptedException e) {
            // Handle the interruption if needed
            Thread.currentThread().interrupt();
        }
    }

    private void stopWSLApplication() throws IOException, InterruptedException {
        // Stop the WSL application process
        ProcessBuilder processBuilder = new ProcessBuilder("wsl", "--terminate", "server_nogl");
        Process wslProcess = processBuilder.start();

        // Optional: Wait for the WSL application process to terminate
        if (!wslProcess.waitFor(5, TimeUnit.SECONDS)) {
            // If the process did not terminate within 5 seconds, forcefully destroy it
            wslProcess.destroyForcibly();
        }
    }
}
