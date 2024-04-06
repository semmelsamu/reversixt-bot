package player;

import board.*;
import player.move.BonusMove;
import player.move.ChoiceMove;
import player.move.InversionMove;
import player.move.Move;
import util.Logger;

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
            Logger.log("Valid move: " + m.getTile().getPosition());
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
            Move move = getValidMoveForPieceInDirection(ownPiece, d);
            if(move != null){
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * @param currentTile      one piece of this player
     * @param currentDirection one of eight directions
     * @return Valid moves for one piece for one of eight directions
     */
    private Move getValidMoveForPieceInDirection(Tile currentTile, Direction currentDirection) {
        Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);
        while (!TileValue.getAllFriendlyValues().contains(currentTile.getValue())) {
            if (currentNeighbour.directionChange() != null) {
                currentDirection = currentNeighbour.directionChange();
            }
            currentNeighbour = currentTile.getNeighbour(currentDirection);

            if(currentNeighbour == null || currentNeighbour.tile().getValue() == playerValue){
                return null;
            }
            currentTile = currentNeighbour.tile();

        }

        return switch (currentTile.getValue()) {
            case CHOICE -> new ChoiceMove(this, currentTile);
            case INVERSION -> new InversionMove(this, currentTile);
            case BONUS -> new BonusMove(this, currentTile);
            default -> new Move(this, currentTile);
        };
    }

    public void increaseOverwriteStones() {
        overwriteStones++;
    }

    public void increaseBombs() {
        bombs++;
    }
}
