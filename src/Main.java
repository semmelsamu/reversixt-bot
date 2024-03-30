import game.Game;
import util.Logger;

public class Main {

    private static final String VERSION = "0.1";

    public static void main(String[] args) {
        Logger.NAME = "revxt-ss24-g04-v" + VERSION;

        // Set to 1 to prevent debug messages or 0 to allow
        Logger.PRIORITY = 0;

        Logger.log("Starting");

        Game game = Game.createFromFile("maps/boeseMaps/boeseMap04.map");
        game.getValidMovesForCurrentPlayer();
    }
}
