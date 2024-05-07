import util.ConsoleInputHandler;
import util.NetworkServerHelper;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        String map = ConsoleInputHandler.selectMap();

        NetworkServerHelper helper = new NetworkServerHelper();

        Runtime.getRuntime().addShutdownHook(new Thread(helper::stopServer));

        helper.startServer(map, 3);

    }
}