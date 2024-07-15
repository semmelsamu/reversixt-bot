package util;

import network.NetworkEventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Helper class which creates either our own clients or the trivial_ai clients on a specific number
 * as a process
 */
public class NetworkClientHelper {

    private static final String arch = System.getProperty("os.arch");

    /**
     * Creates the network clients as process. Either trivial_ai clients or our own clients.
     * @param numOwnClients Our own clients
     * @param numAiClients  trivial_ai clients
     */
    public static void createNetworkClients(int numOwnClients, int numAiClients)
            throws InterruptedException, IOException {

        ExecutorService executorService =
                Executors.newFixedThreadPool(numAiClients + numOwnClients);

        for (int i = 0; i < numOwnClients; i++) {
            executorService.execute(() -> {
                try {
                    Logger.defaultPriority = 3;
                    Logger.setPriority(NetworkEventHandler.class.getName(), 2);
                    NetworkEventHandler handler = new NetworkEventHandler();
                    handler.connect("127.0.0.1", 7777);
                    handler.launch();
                    handler.disconnect();
                }
                catch (Exception e) {
                    fail(e.getMessage());
                }

            });
        }

        Path currentDirectory = getUserDirPath();
        for (int i = 0; i < numAiClients; i++) {

            ProcessBuilder processBuilder;
            if (arch != null && arch.contains("aarch64")) {
                // Create trivial_ai on arm architecture
                Path serverBinaryPath =
                        currentDirectory.resolve("binaries/arm/ai_trivial").toAbsolutePath();
                processBuilder = new ProcessBuilder(serverBinaryPath.toString());
            } else {
                Path serverBinaryPath =
                        currentDirectory.resolve("binaries/x86/ai_trivial").toAbsolutePath();
                // Start the server process in WSL
                processBuilder = new ProcessBuilder("wsl",
                        convertWindowsPathToWSL(serverBinaryPath.toString()));
            }
            Process serverProcess;
            try {
                serverProcess = processBuilder.start();
                new Thread(() -> {
                    try (var reader = new BufferedReader(
                            new InputStreamReader(serverProcess.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Don't ask me why but this works, here the AI don't time out
                            // Optional: the print can be replaced with println(line)
                            System.out.print("");
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    private static Path getUserDirPath() {
        String userDir = System.getProperty("user.dir");
        if (userDir.contains("src")) {
            userDir = userDir.substring(0, userDir.indexOf("src"));
        }
        return Paths.get(userDir);
    }

    private static String convertWindowsPathToWSL(String windowsPath) {
        String unixStylePath = windowsPath.replace("\\", "/");

        if (unixStylePath.matches("[A-Za-z]:.*")) {
            String driveLetter = unixStylePath.substring(0, 1).toLowerCase();
            unixStylePath = "/mnt/" + driveLetter + unixStylePath.substring(2);
        }
        return unixStylePath;
    }
}
