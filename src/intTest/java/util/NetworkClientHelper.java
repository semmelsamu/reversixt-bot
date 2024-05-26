package util;

import clients.Client;
import move.Move;
import network.Launcher;
import network.NetworkEventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class NetworkClientHelper {

    private static final List<Client> clients = new ArrayList<>();
    private static final String arch = System.getProperty("os.arch");

    public static void createNetworkClients(Client client, int numOwnClients, int numAiClients)
            throws InterruptedException, IOException {

        ExecutorService executorService =
                Executors.newFixedThreadPool(numAiClients + numOwnClients);

        for (int i = 0; i < numOwnClients; i++) {
            executorService.execute(() -> {
                try {
                    Client spy = spy(client);
                    clients.add(spy);
                    Logger.defaultPriority = 3;
                    Logger.setPriority(NetworkEventHandler.class.getName(), 2);
                    Launcher.launchClientOnNetwork(spy, "127.0.0.1", 7777);
                } catch (Exception e) {
                    fail(e.getMessage());
                }

            });
        }


        Path currentDirectory = getUserDirPath();
        for(int i = 0; i < numAiClients; i++) {

            ProcessBuilder processBuilder;
            if (arch != null && arch.contains("aarch64")) {
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        executorService.shutdown();

        while (!executorService.isTerminated()) {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    public static void validateMove(Move move) {
        Optional<Client> client =
                clients.stream().filter(c -> c.getME() == move.getPlayerNumber()).findFirst();
        if (client.isPresent()) {
            verify(client.get(), atLeastOnce()).sendMove(anyInt(), anyInt());
        } else {
            fail("Client not found");
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
