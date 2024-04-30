import clients.ParanoidClient;
import exercises.Exercise01;
import exercises.Exercise02;
import exercises.Exercise04;
import exercises.Exercise05;
import game.Game;
import game.MoveCalculator;
import game.MoveExecutor;
import network.NetworkClientAdapter;
import network.NetworkEventHandler;
import player.Player;
import util.CommandLineArguments;
import util.Logger;

public class Main {
    public static void main(String[] args) {

        var parsedArgs = CommandLineArguments.parse(args);

        Logger.defaultPriority = Integer.parseInt(parsedArgs.getOrDefault("-l", "2"));
        Logger.setPriority(Game.class.getName(), 3);

        // Launch whatever

        switch (parsedArgs.getOrDefault("-e", "0")) {
            case "1" -> Exercise01.aufgabe3();
            case "2" -> Exercise02.aufgabe3();
            case "4" -> Exercise04.abnahme(parsedArgs.getOrDefault("-i", "127.0.0.1"),
                    Integer.parseInt(parsedArgs.getOrDefault("-p", "7777")));
            default -> Exercise05.abnahme(parsedArgs.getOrDefault("-i", "127.0.0.1"),
                    Integer.parseInt(parsedArgs.getOrDefault("-p", "7777")),
                    Integer.parseInt(parsedArgs.getOrDefault("-d", "3")));
        }

    }
}