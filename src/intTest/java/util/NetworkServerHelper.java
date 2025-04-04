package util;

import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Helper class which creates the whole server as a process
 */
public class NetworkServerHelper {

    private static Process serverProcess;
    private static final String arch = System.getProperty("os.arch");

    /**
     * Creates the server as a process. Either on Windows as WSL service or as normal process on
     * arm. The server output is also printed in the console.
     * @param map       The map it is played on.
     * @param timeLimit The time limit on the server where a move ist allowed.
     */
    public void startServer(String map, int timeLimit) throws InterruptedException, IOException {
        Semaphore semaphore = new Semaphore(0);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        startProcess(map, timeLimit);

        // Start a thread to continuously read and print the server output
        Thread outputReaderThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(serverProcess.getInputStream()))) {
                int c;
                boolean released = false;
                StringBuilder lineBuilder = new StringBuilder();
                StringBuilder portBuilder = new StringBuilder();
                while (serverProcess.isAlive()) {

                    c = reader.read();

                    if (c == -1) {
                        break;
                    }

                    // check for right port number -> 7777
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

                    // check if port is open
                    if (!released && lineBuilder.toString().contains("Opening port...FAILED.")) {
                        semaphore.release();
                        fail("Port instance is already running");
                    }

                    //continue if first client is connected
                    if (!released &&
                            lineBuilder.toString().contains("Waiting client 1 to connect...")) {
                        released = true;
                        executorService.schedule(() -> semaphore.release(), 1, TimeUnit.SECONDS);

                    }

                }
            }
            catch (IOException e) {
                e.printStackTrace();
                semaphore.release();
            }
        });
        outputReaderThread.start();
        semaphore.acquire();
        executorService.shutdown();
    }

    /**
     * Start the process on windows or arm
     */
    private void startProcess(String map, int timeLimit) throws IOException {
        String userDir = System.getProperty("user.dir");
        if (userDir.contains("src")) {
            userDir = userDir.substring(0, userDir.indexOf("src"));
        }
        Path currentDirectory = Paths.get(userDir);

        Path serverParameterPath = currentDirectory.resolve(map).toAbsolutePath();
        ProcessBuilder processBuilder;
        if (arch != null && arch.contains("aarch64")) {
            Path serverBinaryPath =
                    currentDirectory.resolve("binaries/arm/server_nogl").toAbsolutePath();
            processBuilder =
                    new ProcessBuilder(serverBinaryPath.toString(), serverParameterPath.toString(),
                            "-t", String.valueOf(timeLimit));
        } else {
            Path serverBinaryPath =
                    currentDirectory.resolve("binaries/x86/server_nogl").toAbsolutePath();
            // Start the server process in WSL
            processBuilder =
                    new ProcessBuilder("wsl", convertWindowsPathToWSL(serverBinaryPath.toString()),
                            convertWindowsPathToWSL(serverParameterPath.toString()), "-t",
                            String.valueOf(timeLimit));
        }
        serverProcess = processBuilder.start();
    }

    private String convertWindowsPathToWSL(String windowsPath) {
        String unixStylePath = windowsPath.replace("\\", "/");

        if (unixStylePath.matches("[A-Za-z]:.*")) {
            String driveLetter = unixStylePath.substring(0, 1).toLowerCase();
            unixStylePath = "/mnt/" + driveLetter + unixStylePath.substring(2);
        }
        return unixStylePath;
    }

    /**
     * Stops the server process. It's also a safety function to really kill wsl to avoid multiple
     * ports.
     */
    public void stopServer() {
        Assertions.assertNotNull(serverProcess, "serverProcess is null");
        serverProcess.destroy();

        if (arch != null && !arch.contains("aarch64")) {
            try {
                stopWSLApplication();
            }
            catch (IOException | InterruptedException e) {
                // Handle exceptions if needed
                e.printStackTrace();
            }
        }

        // Optional: Wait for the server process to terminate
        try {
            serverProcess.waitFor(5,
                    TimeUnit.SECONDS); // Wait for 5 seconds for the server process to terminate
        }
        catch (InterruptedException e) {
            // Handle the interruption if needed
            Thread.currentThread().interrupt();
        }
    }

    private void stopWSLApplication() throws IOException, InterruptedException {
        // Stop the WSL application process
        ProcessBuilder processBuilder = new ProcessBuilder("wsl", "--terminate", "ubuntu");
        Process wslProcess = processBuilder.start();

        // Optional: Wait for the WSL application process to terminate
        if (!wslProcess.waitFor(5, TimeUnit.SECONDS)) {
            // If the process did not terminate within 5 seconds, forcefully destroy it
            wslProcess.destroyForcibly();
        }
    }
}
