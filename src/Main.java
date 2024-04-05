import game.Game;
import player.move.Move;
import test.MapReadTest;
import test.Test;
import util.ConsoleInputHandler;
import util.Logger;

public class Main {

    private static final String VERSION = "0.2";

    public static void main(String[] args) {
        Logger.NAME = "revxt-ss24-g04-v" + VERSION;

        // Set to 1 to prevent debug messages or 0 to allow
        Logger.PRIORITY = 0;

        Logger.log("Starting");

        /*
         * ABGABE ÜBUNG 2
         */
        Logger.log("Abgabe Übung 2");

        // TODO: User input which map to load
        String map = "maps/initialMaps/window.map";

        // Load map
        Game game = Game.createFromFile(map);

        // Print board
        Logger.log(game.getBoard().toString());

        // TODO: Print all valid moves
        Logger.log(game.getValidMovesForCurrentPlayer().toString());
        while(true){
            // User inputs move
            Move move = ConsoleInputHandler.createMove(game);

            // Execute move
            game.executeMove(move);

            // Print new board
            Logger.log(game.toString());
        }


        // TODO: If move is not valid, print error
    }
}
