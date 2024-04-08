import exercises.Exercise02;
import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import player.move.Move;
import util.Logger;

public class Main {

    private static final String VERSION = "0.2";

    public static void main(String[] args) {
        // Initialize Logger
        Logger.get().name = "revxt-ss24-g04-v" + VERSION;
        Logger.get().log("Starting");

        // Launch whatever
        Exercise02.aufgabe3();
    }
}