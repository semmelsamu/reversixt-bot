import network.NetworkEventHandler;
import util.ArgumentParser;

public class Main {

    public static void main(String[] args) {

        // Parse arguments
        var argumentParser = getArgumentParser();
        var parsedArguments = argumentParser.parse(args);

        if ((Boolean) parsedArguments.get("h")) {
            // Print help dialog
            System.out.println(argumentParser);
            return;
        }

        launch((String) parsedArguments.get("i"), (int) parsedArguments.get("p"));

    }

    /**
     * @return The argument parser, which can parse the command line arguments and generate a help
     * string.
     */
    private static ArgumentParser getArgumentParser() {
        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.setParameter("i", new ArgumentParser.Parameter("IP", "127.0.0.1"));
        argumentParser.setParameter("p", new ArgumentParser.Parameter("Port", 7777));
        argumentParser.setParameter("c", new ArgumentParser.Parameter("Colors", false));
        argumentParser.setParameter("q", new ArgumentParser.Parameter("Quiet Mode", false));
        argumentParser.setParameter("h", new ArgumentParser.Parameter("Help", false));
        argumentParser.setParameter("l", new ArgumentParser.Parameter("Logger priority", 1));
        return argumentParser;
    }

    /**
     * Connect to the server and run the client.
     *
     * @param ip   The IP address to connect the client to.
     * @param port The port to connect the client to.
     */
    private static void launch(String ip, int port) {
        try {
            NetworkEventHandler handler = new NetworkEventHandler();
            handler.connect(ip, port);
            handler.launch();
            handler.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}