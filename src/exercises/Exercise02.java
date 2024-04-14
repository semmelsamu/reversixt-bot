package exercises;

import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import player.move.Move;
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
        Set<Move> validMoves = game.getValidMovesForCurrentPlayer();
        Logger.get().debug("All valid moves for current Player:");
        for (var move : validMoves) {
            Logger.get().debug(move.toString());
        }

        // User inputs move
        Move move = ConsoleInputHandler.selectMove(game);

        // Check if move is valid
        if (!isMoveValid(move, validMoves)){
            Logger.get().error("Move is not valid!");
            return;
        }

        // Execute move
        MoveExecutor moveExecutor = new MoveExecutor(game);
        moveExecutor.executeMove(move);

        // Print new board
        Logger.get().log(game.toString());

    }

    private static boolean isMoveValid(Move move, Set<Move> validMoves){
        boolean isValid = false;
        for(Move validMove : validMoves){
            if(validMove.getCoordinates().x == move.getCoordinates().x
                    && validMove.getCoordinates().y == move.getCoordinates().y) {
                isValid = true;
            }
        }
        return isValid;
    }
}
