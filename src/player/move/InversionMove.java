package player.move;

import board.Board;
import board.Tile;
import game.Game;
import player.Player;

/**
 *  Invert the order of players
 */
public class InversionMove extends Move{
    private Game game;
    public InversionMove(Player player, Tile tile, Game game) {
        super(player, tile);
        this.game = game;
    }

    @Override
    public void execute(Board board) {
        super.execute(board);
        Player[] players = game.getPlayers();

        Player temp = players[players.length - 1];

        // switch position to the right
        for (int i = players.length - 1; i > 0; i--) {
            players[i] = players[i - 1];
        }
        // fill up first one
        players[0] = temp;
    }
}
