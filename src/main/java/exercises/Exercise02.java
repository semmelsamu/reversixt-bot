package exercises;

import game.Game;
import game.GameFactory;
import game.MoveCalculator;
import game.MoveExecutor;
import move.Move;
import util.ConsoleInputHandler;
import util.Logger;

import java.util.Set;

public class Exercise02 {

    /**
     * Abnahme: Alle 11 bereitgestellten Maps (boeseMap01-11) müssen korrekt behandelt werden.
     * Ihr Programm muss zunächst die Karte und alle möglichen Züge für Spieler 1 ausgegeben.
     * Anschließend wird über die Konsole ein Zug eingegeben (z. B. per x- und y-Koordinate und
     * ggf. notwendiger Zusatzinformationen). Ihr Programm soll dann entweder das umgefärbte
     * Spielfeld ausgeben oder anzeigen, dass der Zug nicht gültig ist.
     */
    public static void aufgabe3() {

        // Load map
        String boeseMap = ConsoleInputHandler.selectMap();
        Game game = GameFactory.createFromFile(boeseMap);

        // Print board
        Logger.get().log(game.toString());

        // Get and print all valid moves
        Set<Move> validMoves = MoveCalculator.getValidMovesForPlayer(game, 1);

        Logger.get().log("All valid moves for current Player:");
        for (var move : validMoves) {
            Logger.get().log(move.toString());
        }

        // User inputs move
        Move move = ConsoleInputHandler.selectMove(game);

        // Check if move is valid
        if (!validMoves.contains(move)) {
            Logger.get().error("Move is not valid!");
            return;
        }

        // Execute move
        MoveExecutor moveExecutor = new MoveExecutor(game);
        moveExecutor.executeMove(move);

        // Print new board
        Logger.get().log(game.toString());

    }
}
