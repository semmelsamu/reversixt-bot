import game.Game;
import test.MapReadTest;
import test.Test;
import util.Logger;

public class Main {

    private static final String VERSION = "0.2";

    public static void main(String[] args) {
        Logger.NAME = "revxt-ss24-g04-v" + VERSION;

        // Set to 1 to prevent debug messages or 0 to allow
        Logger.PRIORITY = 0;

        Logger.log("Starting");

        // Specify map to read and load map
        Game game = Game.createFromFile("maps/initialMaps/window.map");

        Logger.log(game.getValidMovesForCurrentPlayer().toString());

        // Input

        // Execute

        // Print error if not valid or calculate and print new game if valid
        Logger.log(game.toString());
    }
}
