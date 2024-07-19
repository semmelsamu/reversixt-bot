import network.NetworkEventHandler;
import util.ArgumentParser;
import util.Logger;

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

        if ((Boolean) parsedArguments.get("q")) {
            // Quiet mode
            Logger.defaultPriority = 10;
        } else {
            // Initialize logger
            Logger.useColors = (Boolean) parsedArguments.get("c");
            Logger.defaultPriority = (int) parsedArguments.get("l");
        }

        Logger.get().log("Hello there.");

        welcome();

        Logger.get().log("Arguments: \"" + String.join(" ", args) + "\"");

        launch((String) parsedArguments.get("i"), (int) parsedArguments.get("p"));

        Logger.get().log("The Force will be with you. Always.");
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
     * Log the welcome message.
     */
    private static void welcome() {
        Logger.get().log("""
                This is Reversi++ Client
                Developed as part of the FWPM "ZOCK" in the summer semester 2024 at OTH Regensburg
                By Samuel Kroiss, Ludwig Schmidt, and Maximilian Strauss
                Use -h for help""");
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
            Logger.get().error(e);
        }
    }
}