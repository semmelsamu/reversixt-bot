import clients.ParanoidClient;
import exercises.Exercise05;
import game.MoveCalculator;
import network.NetworkEventHandler;
import player.Player;
import util.CommandLineArguments;
import util.Logger;

public class Main {
    public static void main(String[] args) {

        Logger.defaultPriority = 3;
        Logger.setPriority(NetworkEventHandler.class.getName(), 1);
        Logger.setPriority(ParanoidClient.class.getName(), 0);
        Logger.setPriority(Player.class.getName(), 2);

        var parsedArgs = CommandLineArguments.parse(args);

        // Launch whatever
        Exercise05.abnahme(parsedArgs.getOrDefault("-i", "127.0.0.1"),
                Integer.parseInt(parsedArgs.getOrDefault("-p", "7777")));
    }
}