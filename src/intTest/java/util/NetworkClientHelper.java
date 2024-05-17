package util;

import clients.Client;
import move.Move;
import network.Launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class NetworkClientHelper {

    private static final List<Client> clients = new ArrayList<>();
    private static final String arch = System.getProperty("os.arch");

    public static void createNetworkClients(Client client, int numOwnClients, int numAiClients)
            throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numOwnClients; i++) {
            Thread clientThread = new Thread(() -> {
                try {
                    Client spy = spy(client);
                    clients.add(spy);
                    Logger.defaultPriority = 3;
                    Launcher.launchClientOnNetwork(spy, "127.0.0.1", 7777);
                } catch (Exception e) {
                    fail(e.getMessage());
                }

            });
            threads.add(clientThread);
        }


        Path currentDirectory = getUserDirPath();
        for(int i = 0; i < numAiClients; i++) {
            Thread clientThread = new Thread(() -> {
                try {
                    startProcess(currentDirectory);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

    public static void validateMove(Move move) {
        Optional<Client> client =
                clients.stream().filter(c -> c.getME() == move.getPlayerNumber()).findFirst();
        if (client.isPresent()) {
            verify(client.get(), atLeastOnce()).sendMove(anyInt(), anyInt());
        } else {
            fail("Client not found");
        }
    }

    private static void startProcess(Path currentDirectory) throws IOException {
        ProcessBuilder processBuilder;
        if (arch != null && arch.contains("aarch64")) {
            Path serverBinaryPath =
                    currentDirectory.resolve("binaries/arm/ai_trivial").toAbsolutePath();
            processBuilder = new ProcessBuilder(serverBinaryPath.toString());
        } else {
            Path serverBinaryPath =
                    currentDirectory.resolve("binaries/x86/ai_trivial").toAbsolutePath();
            // Start the server process in WSL
            processBuilder =
                    new ProcessBuilder("wsl", convertWindowsPathToWSL(serverBinaryPath.toString()));
        }
        processBuilder.start();
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
