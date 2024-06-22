package stats;

import board.Tile;

import java.util.Objects;

public class PlayerTileAmountContainer implements Cloneable {
    final Tile player;
    private int tileAmount;

    public PlayerTileAmountContainer(Tile player, int tileAmount) {
        this.player = player;
        this.tileAmount = tileAmount;
    }

    public Tile getPlayer() {
        return player;
    }

    public int getTileAmount() {
        return tileAmount;
    }

    public void incrementTileAmount() {
        tileAmount++;
    }

    public void decrementTileAmount() {
        tileAmount--;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Overrides
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerTileAmountContainer that = (PlayerTileAmountContainer) o;
        return tileAmount == that.tileAmount && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, tileAmount);
    }

    @Override
    protected PlayerTileAmountContainer clone() throws CloneNotSupportedException {
        try {
            return (PlayerTileAmountContainer) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return "PlayerTileAmountContainer {\n" + "    player=" + player + ",\n" +
                "    tileAmount=" + tileAmount + "\n" + "  }";
    }

}
