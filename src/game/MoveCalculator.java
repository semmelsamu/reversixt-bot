package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import player.Player;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MoveCalculator {
    private Game game;

    public MoveCalculator(Game game) {
        this.game = game;
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
        Player currentPlayer = game.getCurrentPlayer();
        Tile currentPlayerValue = currentPlayer.getPlayerValue();
        Logger.get().log("Searching for all valid moves for Player " + currentPlayerValue);
        Set<Move> moves = new HashSet<>();
        for (Coordinates occupiedTile : game.getAllCoordinatesWhereTileIs(currentPlayerValue)) {
            if (game.getTile(occupiedTile) != currentPlayerValue) {
                Logger.get().error("Wrong coordinates in Player" + currentPlayerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(occupiedTile, currentPlayerValue
            ));
        }
        return moves;
    }

    /**
     * @param ownTileCoordinates one piece of this player
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Coordinates ownTileCoordinates, Tile currentPlayerValue) {
        Logger.get().verbose("Searching for valid moves originating from piece " + ownTileCoordinates);
        Set<Move> moves = new TreeSet<>();
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, ownTileCoordinates, direction);
            // Check if tile has a neighbour in this direction
            if (!tileReader.hasNext()) {
                continue;
            }
            tileReader.next();
            Tile firstNeighbourTile = tileReader.getTile();
            // Check if first neighbour tile is unoccupied
            if (firstNeighbourTile.isUnoccupied()) {
                continue;
            }

            // Check if first neighbour tile is an own tile
            if (firstNeighbourTile == currentPlayerValue){
                continue;
            }

            // Check if first neighbour tile has the same coordinates as the own piece
            if (tileReader.getCoordinates() == ownTileCoordinates){
                continue;
            }

            Set<Move> movesInDirection = getValidMoveForPieceInDirection(tileReader, currentPlayerValue);
            if (movesInDirection != null) {
                moves.addAll(movesInDirection);
            }
        }
        return moves;
    }

    /**
     * @param tileReader
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMoveForPieceInDirection(TileReader tileReader,
                                                      Tile playerValue) {
        Logger.get().verbose("Searching for valid moves in direction ");

        Set<Move> movesPerDirection = new HashSet<>();
        Tile currentTile = tileReader.getTile();
        Coordinates currentCoordinates = tileReader.getCoordinates();
        boolean tilesBetweenExistingAndNewPiece = false;
        boolean hasOverwriteStones = false;
        if(game.hasPlayerOverwriteStones(playerValue)){
            hasOverwriteStones = true;
        }
        // As long as there is an ococcupied tile
        while (!currentTile.isUnoccupied()) {

            // Check if there is a dead end
            if (!tileReader.hasNext()) {
                return movesPerDirection;
            }

            tileReader.next();
            // If there are no tiles between existing and new piece
            if(!tilesBetweenExistingAndNewPiece){
                if(currentCoordinates != tileReader.getCoordinates()){
                    tilesBetweenExistingAndNewPiece = true;
                }
            }
            currentTile = tileReader.getTile();
            currentCoordinates = tileReader.getCoordinates();

            if (hasOverwriteStones) {
                // Overwrite stone logic
                if (currentTile.isPlayer() && tilesBetweenExistingAndNewPiece) {
                    movesPerDirection.add(new Move(currentTile, currentCoordinates));
                    // If an own tile is overwritten, return because this tile is handled separately
                    if (currentTile == playerValue) {
                        return movesPerDirection;
                    }
                }
            }
        }
        // If necessary create special move
        switch (currentTile) {
            case CHOICE -> {
                for (Tile playerTile : Tile.getAllPlayerTiles()) {
                    if (playerTile != currentTile) {
                        movesPerDirection.add(new ChoiceMove(currentTile, currentCoordinates, playerTile));
                    }
                }
            }
            case INVERSION -> movesPerDirection.add(new InversionMove(currentTile, currentCoordinates));
            case BONUS -> {
                movesPerDirection.add(new BonusMove(currentTile, currentCoordinates, Bonus.BOMB));
                movesPerDirection.add(new BonusMove(currentTile, currentCoordinates, Bonus.OVERWRITE_STONE));
            }
            default -> movesPerDirection.add(new Move(currentTile, currentCoordinates));

        }

        return movesPerDirection;
    }
}
