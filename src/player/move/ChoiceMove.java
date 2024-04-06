package player.move;

import board.Board;
import board.Tile;
import player.Player;
import util.ConsoleInputHandler;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends Move {

    public ChoiceMove(Player player, Tile tile) {
        super(player, tile, false);
    }

    @Override
    public Player[] execute(Board board, Player[] players) {
        Player[] playersAfterColoring = super.execute(board, players);
        int playerToSwapWithIndex = getPlayerToSwapWithIndex(players, getPlayer());
        int currentPlayerIndex = getCurrentPlayerIndex(players, getPlayer());

        Player temp = getPlayer();
        playersAfterColoring[currentPlayerIndex] = playersAfterColoring[playerToSwapWithIndex];
        playersAfterColoring[playerToSwapWithIndex] = temp;
        return playersAfterColoring;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    private int getPlayerToSwapWithIndex(Player[] players, Player currentPlayer) {
        return ConsoleInputHandler.handleChoice(players, currentPlayer);
    }

    private int getCurrentPlayerIndex(Player[] players, Player currentPlayer) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == currentPlayer) {
                return i;
            }
        }
        return -1;
    }
}
