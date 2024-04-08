package player.move;

import board.Board;
import board.Tile;
import player.Player;
import util.ConsoleInputHandler;
import util.Logger;

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

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Bonus getBonus() {
        return bonus;
    }

    @Override
    public String toString() {
        return "BonusMove{" +
                "bonus=" + bonus +
                ", player=" + player +
                ", tile=" + tile +
                '}';
    }
}
