package player.move;

import board.Board;
import board.Tile;
import player.Player;
import util.ConsoleInputHandler;

/**
 * A move where after the player sets a stone, he will receive a bonus.
 */
public class BonusMove extends Move {


    public BonusMove(Player player, Tile tile) {
        super(player, tile, false);
    }

    @Override
    public Player[] execute(Board board, Player[] players) {
        Player[] playersAfterColoring = super.execute(board, players);
        Bonus bonus = ConsoleInputHandler.handleBonus(getPlayer());
        Player p = getPlayer();
        if (bonus == Bonus.BOMB) {
            p.incrementBombs();
        } else {
            p.incrementOverwriteStone();
        }
        return playersAfterColoring;
    }
}
