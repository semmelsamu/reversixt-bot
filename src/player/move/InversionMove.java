package player.move;

import board.Board;
import board.Tile;
import player.Player;

import java.util.Set;

/**
 *  Invert the order of players
 */
public class InversionMove extends Move{

    public InversionMove(Player player, Tile tile) {
        super(player, tile, false);
    }

    @Override
    public Player[] execute(Board board, Player[] players) {
        Player[] playersAfterColoring = super.execute(board, players);

        Player temp = playersAfterColoring[playersAfterColoring.length - 1];

        // switch position to the right
        for (int i = playersAfterColoring.length - 1; i > 0; i--) {
            playersAfterColoring[i] = playersAfterColoring[i - 1];
        }
        // fill up first one
        playersAfterColoring[0] = temp;
        return playersAfterColoring;
    }
}
