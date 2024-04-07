package player;

import board.*;
import player.move.BonusMove;
import player.move.ChoiceMove;
import player.move.InversionMove;
import player.move.Move;
import util.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Player {

    /*
    |--------------------------------------------------------------------------
    | Member variables
    |--------------------------------------------------------------------------
    */

    /**
     * The "color" of the player
     */
    private final TileValue playerValue;

    /**
     * The number of overwrite stones this player has
     */
    private int overwriteStones;

    /**
     * The number of bombs this player has
     */
    private int bombs;

    /**
     * A list of all tiles the player has occupied
     */
    private List<Tile> occupiedTiles;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Player(TileValue playerValue, int overwriteStones, int bombs, List<Tile> occupiedTiles) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.occupiedTiles = occupiedTiles;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public TileValue getPlayerValue() {
        return playerValue;
    }

    public int getOverwriteStones() {
        return overwriteStones;
    }

    public int getBombs() {
        return bombs;
    }

    public List<Tile> getOccupiedTiles() {
        return occupiedTiles;
    }

    /*
    |--------------------------------------------------------------------------
    | Functions for calculating valid moves
    |--------------------------------------------------------------------------
    */

    /**
     * Get all valid moves for this player
     */
    public Set<Move> getValidMoves() {
        Logger.log("Searching for all valid moves for Player" + this.playerValue);
        Set<Move> moves = new TreeSet<>();
        for (Tile occupiedTile : occupiedTiles) {
            if (occupiedTile.getValue() != playerValue) {
                Logger.error("Wrong coordinates in Player" + playerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(occupiedTile));
        }
        for (Move m : moves) {
            Logger.log("Valid move: " + m.getTile().getPosition() + " overwriteStone: " + m.isOverwriteStone());
        }
        return moves;
    }

    /**
     * @param ownPiece one piece of this player
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Tile ownPiece) {
        Set<Move> moves = new TreeSet<>();
        for (Direction d : Direction.values()) {
            Neighbour neighbour = ownPiece.getNeighbour(d);
            // check if there is a dead end
            if (neighbour == null) {
                continue;
            }
            TileValue neighbourValue = neighbour.tile().getValue();
            // check if tile value is seen as an enemy or if it's the same color
            if (TileValue.getAllFriendlyValues().contains(neighbourValue) || neighbourValue == getPlayerValue()) {
                continue;
            }
            Set<Move> move = getValidMoveForPieceInDirection(ownPiece, d);
            if (move != null) {
                moves.addAll(move);
            }
        }
        return moves;
    }

    /**
     * @param currentTile      one piece of this player
     * @param currentDirection one of eight directions
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMoveForPieceInDirection(Tile currentTile, Direction currentDirection) {
        Tile firstTile = currentTile;
        Set<Move> movesPerDirection = new HashSet<>();
        Set<Tile> alreadyVisited = new HashSet<>();
        int howFarFromFirstTile = 0;
        // as long as there is an empty field
        while (!TileValue.getAllFriendlyValues().contains(currentTile.getValue())) {
            alreadyVisited.add(currentTile);
            Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);

            // check for a dead end
            if (currentNeighbour == null) {
                return movesPerDirection;
            }

            currentTile = currentNeighbour.tile();


            if (currentNeighbour.directionChange() != null) {
                currentDirection = currentNeighbour.directionChange();
            }

            if(alreadyVisited.contains(currentTile)){
                if(currentTile == firstTile){
                    return movesPerDirection;
                }
                continue;
            }

            if (overwriteStones != 0) {
                howFarFromFirstTile++;
                // overwrite stone logic
                if (currentTile.getValue().isPlayer() && currentTile.getValue() != playerValue && howFarFromFirstTile > 1) {
                    movesPerDirection.add(new Move(this, currentTile, true));
                }



                // tile has the same value as another tile, but isn't the same tile and is more fare away than 1 -> overwrite stone on the first tile
                if (currentTile.getValue() == playerValue && howFarFromFirstTile > 1) {
                    movesPerDirection.add(new Move(this, firstTile, true));
                    return movesPerDirection;
                }
            }

        }
        // decide which move
        switch (currentTile.getValue()) {
            case CHOICE -> movesPerDirection.add(new ChoiceMove(this, currentTile));
            case INVERSION -> movesPerDirection.add(new InversionMove(this, currentTile));
            case BONUS -> movesPerDirection.add(new BonusMove(this, currentTile));
            default -> movesPerDirection.add(new Move(this, currentTile, false));
        }

        return movesPerDirection;
    }

    /**
     * Increment overwrite stones by 1
     */
    public void incrementOverwriteStone() {
        overwriteStones++;
    }

    /**
     * Increment bombs by 1
     */
    public void incrementBombs() {
        bombs++;
    }

    /**
     * Decrement overwrite stones by 1
     */
    public void decrementOverwriteStone() {
        overwriteStones--;
    }
}
