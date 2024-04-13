package player.move;

import board.Coordinates;
import board.Tile;

/**
 * A move where after the player sets a stone, he will receive a bonus.
 */
public class BonusMove extends Move {

    /**
     * Either receive a bomb or an overwrite stone.
     */
    private final Bonus bonus;

    public BonusMove(Tile player, Coordinates coordinates, Bonus bonus) {
        super(player, coordinates);
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
        return "BonusMove{player=" + player + ", coordinates=" + coordinates + ", bonus=" + bonus +
                "}";
    }
}
