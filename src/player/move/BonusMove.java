package player.move;

import board.Tile;
import player.Player;

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
        return "BonusMove{tile=" + tile + ", player=" + player + ", bonus=" + bonus + "}";
    }
}
