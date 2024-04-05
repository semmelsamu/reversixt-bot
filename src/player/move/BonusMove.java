package player.move;

import board.Board;
import board.Tile;
import player.Player;
import util.ConsoleInputHandler;

/**
 * A move where after the player sets a stone, he will receive a bonus.
 */
public class BonusMove extends Move {

    /**
     * Either receive a bomb or an overwrite stone.
     */
    private final Bonus bonus;

    public BonusMove(Player player, Tile tile, Bonus bonus) {
        super(player, tile);
        this.bonus = bonus;
    }

    @Override
    public void execute(Board board) {
        super.execute(board);
        Bonus bonus = ConsoleInputHandler.handleBonus(getPlayer());
        Player p = getPlayer();
        if (bonus == Bonus.BOMB) {
            p.increaseBombs();
        } else {
            p.increaseOverwriteStones();
        }
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Bonus getBonus() {
        return bonus;
    }
}
