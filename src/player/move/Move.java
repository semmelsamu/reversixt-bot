package player.move;

import board.*;
import player.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * A most basic move where the player only sets a stone.
 */
public class Move implements Comparable<Move> {

    /**
     * The player this move belongs to.
     */
    private final Player player;

    /**
     * The tile the move targets.
     */
    private final Tile tile;

    public Move(Player player, Tile tile) {
        this.player = player;
        this.tile = tile;
    }

    public void execute(Board board){
        Set<Tile> tilesToColour = new HashSet<>();
        // check every direction
        for (Direction d : Direction.values()) {
            Neighbour neighbour = tile.getNeighbour(d);
            // check if there is a dead end
            if (neighbour == null) {
                continue;
            }
            TileValue neighbourValue = neighbour.tile().getValue();
            // check if tile value is seen as an enemy or if it's the same color
            if (TileValue.getAllFriendlyValues().contains(neighbourValue) || neighbourValue == player.getPlayerValue()) {
                continue;
            }
            tilesToColour.addAll(getTilesToColourInDirection(tile, d));
        }
        tilesToColour.add(tile);
        // colour all pieces
        for (Tile tile : tilesToColour) {
            board.setTileValue(tile.getPosition(), player.getPlayerValue());
        }
    }

    private Set<Tile> getTilesToColourInDirection(Tile currentTile, Direction currentDirection) {
        Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);
        Set<Tile> tilesToColourInDirection = new HashSet<>();
        // as long the neighbour has the same color as itself
        while (currentNeighbour.tile().getValue() != player.getPlayerValue()) {
            currentTile = currentNeighbour.tile();

            tilesToColourInDirection.add(currentTile);
            if (currentNeighbour.directionChange() != null) {
                currentDirection = currentNeighbour.directionChange();
            }
            currentNeighbour = currentTile.getNeighbour(currentDirection);

            // check if there is a dead end
            if(currentNeighbour == null){
                return new HashSet<>();
            }

            // check if there are values which are not enemies
            if(TileValue.getAllFriendlyValues().contains(currentNeighbour.tile().getValue())){
                return new HashSet<>();
            }
        }
        return tilesToColourInDirection;

    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Player getPlayer() {
        return player;
    }

    public Tile getTile() {
        return tile;
    }

    @Override
    public int compareTo(Move o) {
        Coordinates thisPosition = this.getTile().getPosition();
        Coordinates otherPosition = o.getTile().getPosition();
        if (thisPosition.x != otherPosition.x) {
            return Integer.compare(thisPosition.x, otherPosition.x);
        } else {
            return Integer.compare(thisPosition.y, otherPosition.y);
        }
    }

    @Override
    public String toString() {
        return tile.getPosition().toString();
    }
}
