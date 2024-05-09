import clients.OptimizedParanoidClient;
import clients.ParanoidClient;
import exercises.*;
import game.Game;
import util.ArgumentParser;
import util.Logger;

public class Main {
    public static void main(String[] args) {

        ArgumentParser argumentParser = new ArgumentParser();
        argumentParser.setParameter("i", new ArgumentParser.Parameter("IP", "127.0.0.1"));
        argumentParser.setParameter("p", new ArgumentParser.Parameter("Port", 7777));
        argumentParser.setParameter("e", new ArgumentParser.Parameter("Exercise", 0));
        argumentParser.setParameter("c", new ArgumentParser.Parameter("Colors", false));
        argumentParser.setParameter("q", new ArgumentParser.Parameter("Quiet Mode", false));
        argumentParser.setParameter("h", new ArgumentParser.Parameter("Help", false));
        ArgumentParser.ParsedArguments parsedArguments = argumentParser.parse(args);

        if((Boolean) parsedArguments.get("h")) {
            System.out.println(argumentParser);
            return;
        }

        if((Boolean) parsedArguments.get("q")) {
            Logger.defaultPriority = 10;
        }
        else {
            Logger.useColors = (Boolean) parsedArguments.get("c");
            Logger.defaultPriority = 2;
            Logger.setPriority(Game.class.getName(), 3);
            Logger.setPriority(ParanoidClient.class.getName(), 1);
            Logger.setPriority(OptimizedParanoidClient.class.getName(), 1);

            Logger.get().log("Arguments: \"" + String.join(" ", args) + "\"");
        }

        // Launch whatever

        String ip = (String) parsedArguments.get("i");
        int port = (int) parsedArguments.get("p");

        switch ((Integer) parsedArguments.get("e")) {
            case 1 -> Exercise01.aufgabe3();
            case 2 -> Exercise02.aufgabe3();
            case 3 -> Exercise04.abnahme(ip, port);
            case 5 -> Exercise05.abnahme(ip, port);
            default -> Exercise06.abnahme(ip, port);
        }
    }
}