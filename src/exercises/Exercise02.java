package exercises;

import game.Game;
import player.move.Move;
import util.ConsoleInputHandler;
import util.Logger;

import java.util.Set;

public class Exercise02 {

    /**
     * Abnahme: Alle 11 bereitgestellten Maps (boeseMap01-11) müssen korrekt behandelt werden. Ihr Pro-
     * gramm muss zunächst die Karte und alle möglichen Züge für Spieler 1 ausgegeben. Anschließend wird
     * über die Konsole ein Zug eingegeben (z. B. per x- und y-Koordinate und ggf. notwendiger
     * Zusatzinformationen). Ihr Programm soll dann entweder das umgefärbte Spielfeld ausgeben oder anzeigen,
     * dass der Zug nicht gültig ist.
     */
    public static void aufgabe3() {

        // Load map
        String boeseMap = ConsoleInputHandler.selectBoeseMap();
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
