import game.Game;
import player.move.Move;
import util.ConsoleInputHandler;
import util.Logger;

import java.util.Set;

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

        String boeseMap = ConsoleInputHandler.selectBoeseMap();

        // Load map
        Game game = Game.createFromFile(boeseMap);

        // Print board
        Logger.log(game.getBoard().toString());

        int unableToMovePlayers = 0;
        while (!(unableToMovePlayers == game.getPlayers().length)) {

            // Print all valid moves
            Set<Move> validMovesForCurrentPlayer = game.getValidMovesForCurrentPlayer();
            Logger.log(validMovesForCurrentPlayer.toString());
            if (validMovesForCurrentPlayer.isEmpty()) {
                unableToMovePlayers++;
                game.nextPlayer();
                continue;
            }
            // User inputs move
            Move move = ConsoleInputHandler.selectMove(game.getCurrentPlayer());
            // Execute move
            game.executeMove(move);

            // Print new board
            Logger.log(game.toString());
        }

        Logger.log("Game finished");
    }
}
