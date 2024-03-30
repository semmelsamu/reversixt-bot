package player;

import board.*;
import util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private TileValue playerValue;
    private int overwriteStones;
    private int bombs;
    private List<Tile> occupiedTiles;

    public Player(TileValue playerValue, int overwriteStones, int bombs, List<Tile> occupiedTiles) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.occupiedTiles = occupiedTiles;
    }

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

    /**
     *
     * Valid moves for one player
     */
    public List<Coordinates> getValidMoves(){
        List<Coordinates> moves = new ArrayList<>();
        for(Tile s : occupiedTiles){
            if(s.getValue() != playerValue){
                Logger.error("Wrong coordinates in " + playerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(s));
        }
        System.out.println("Valid moves for " + playerValue);
        for(Coordinates c : moves){
            System.out.println((c.x + 1) + " " +  (c.y + 1));
        }
        return moves;
    }

    /**
     *
     * @param ownPiece one piece of this player
     * @return Valid moves for one piece of this player
     */
    private List<Coordinates> getValidMovesForPiece(Tile ownPiece){
        List<Coordinates> moves = new ArrayList<>();
        for(Direction d : Direction.values()){
            //currentDirection = d;

            moves.add(getValidMovesForPieceInDirection(ownPiece, d));

        }
        return moves;
    }

    /**
     *
     * @param ownPiece one piece of this player
     * @param direction one of eight directions
     * @return Valid moves for one piece for one of eight directions
     */
    private Coordinates getValidMovesForPieceInDirection(Tile ownPiece, Direction direction){
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
                    currentNeighbour = currentTile.getNeighbour(currentDirection);
                    if (currentNeighbour.directionChange() != null) {
                        currentDirection = currentNeighbour.directionChange();
                    }
                    currentNeighbour = currentTile.getNeighbour(currentDirection);
            }
        }
        if(foundEmptyTile){
            return currentTile.getPosition();
        }
        else{
            return null;
        }
    }
}
