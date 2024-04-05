package player;

import board.*;
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
    private TileValue playerValue;

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
    public Set<Move> getValidMoves(){
        Logger.log("Searching for all valid moves for Player" + this.playerValue);
        Set<Move> moves = new TreeSet<>();
        for(Tile s : occupiedTiles){
            if(s.getValue() != playerValue){
                Logger.error("Wrong coordinates in Player" + playerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(s));
        }
        for(Move m : moves){
            Logger.log("Valid move: " + m.getTile().getPosition());
        }
        return moves;
    }

    /**
     * @param ownPiece one piece of this player
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Tile ownPiece){
        Set<Move> moves = new TreeSet<>();
        for(Direction d : Direction.values()){
            Move move = getValidMoveForPieceInDirection(ownPiece, d);
            if(move != null){
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * @param ownPiece one piece of this player
     * @param direction one of eight directions
     * @return Valid moves for one piece for one of eight directions
     */
    private Move getValidMoveForPieceInDirection(Tile ownPiece, Direction direction){
        boolean firstTileOpponent = false;
        boolean foundEmptyTile = false;
        Direction currentDirection = direction;
        Tile currentTile = ownPiece;
        Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);
        // terminates  if an empty Tile was found or if the neighbour is an own piece or if there is no neighbour
        while(!(foundEmptyTile) && currentNeighbour != null &&
                currentNeighbour.tile().getValue() != ownPiece.getValue())
        {
            currentTile = currentNeighbour.tile();
            switch (currentTile.getValue()) {
                case EMPTY:
                case CHOICE:
                case INVERSION:
                case BONUS:
                    foundEmptyTile = true;
                    break;
                default:
                    firstTileOpponent = true;
                    if (currentNeighbour.directionChange() != null) {
                        currentDirection = currentNeighbour.directionChange();
                    }
                    currentNeighbour = currentTile.getNeighbour(currentDirection);
            }
        }
        // First neighbour in a direction must be a piece of an opponent in order to do a valid move
        if(!firstTileOpponent){
            return null;
        }
        if(foundEmptyTile){
            return new Move(this, currentTile);
        }
        else{
            return null;
        }
    }
}
