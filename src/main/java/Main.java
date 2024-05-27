import clients.IterativeDeepeningAlphaBetaSearchClient;
import clients.OptimizedParanoidClient;
import clients.ParanoidClient;
import exercises.*;
import game.Game;
import util.ArgumentParser;
import util.Logger;

public class Main {

    private static final String[] welcomeMessage = {
            "This is Reversi++ Client",
            "Developed as part of the FWPM \"ZOCK\" in the summer semester 2024 at OTH Regensburg",
            "By Samuel Kroiss, Ludwig Schmidt, and Maximilian Strauss", "Use -h for help"
    };

    public static void main(String[] args) {

        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.setParameter("i", new ArgumentParser.Parameter("IP", "127.0.0.1"));
        argumentParser.setParameter("p", new ArgumentParser.Parameter("Port", 7777));
        argumentParser.setParameter("e", new ArgumentParser.Parameter("Exercise", 0));
        argumentParser.setParameter("c", new ArgumentParser.Parameter("Colors", false));
        argumentParser.setParameter("n", new ArgumentParser.Parameter("Move sorting", true));
        argumentParser.setParameter("q", new ArgumentParser.Parameter("Quiet Mode", false));
        argumentParser.setParameter("h", new ArgumentParser.Parameter("Help", false));
        ArgumentParser.ParsedArguments parsedArguments = argumentParser.parse(args);

        if ((Boolean) parsedArguments.get("h")) {
            System.out.println(argumentParser);
            return;
        }

        if ((Boolean) parsedArguments.get("q")) {
            Logger.defaultPriority = 10;
        } else {
            Logger.useColors = (Boolean) parsedArguments.get("c");
            Logger.defaultPriority = 2;
            Logger.setPriority(Game.class.getName(), 3);
            Logger.setPriority(ParanoidClient.class.getName(), 1);
            Logger.setPriority(OptimizedParanoidClient.class.getName(), 0);
            Logger.setPriority(IterativeDeepeningAlphaBetaSearchClient.class.getName(), 0);

            for(var line : welcomeMessage) Logger.get().log(line);
        }

        // Launch whatever

        String ip = (String) parsedArguments.get("i");
        int port = (int) parsedArguments.get("p");

        switch ((Integer) parsedArguments.get("e")) {
            case 1 -> Exercise01.aufgabe3();
            case 2 -> Exercise02.aufgabe3();
            case 4 -> Exercise04.abnahme(ip, port);
            case 5 -> Exercise05.abnahme(ip, port);
            case 6 -> Exercise06.abnahme(ip, port);
            default -> Exercise07.abnahme(ip, port, (Boolean) parsedArguments.get("n"));
            case 8 -> Exercise08.abnahme();
        }
    }
}