package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @param playerValue Tile of player that moves are calculated for
     * @return All valid moves for this player
     */
    public Set<Move> getValidMovesForPlayer(Tile playerValue) {
        Logger.get().log("Searching for all valid moves for Player " + playerValue);
        Set<Move> moves = new HashSet<>();
        for (Coordinates occupiedTile : game.getAllCoordinatesWhereTileIs(playerValue)) {
            if (game.getTile(occupiedTile) != playerValue) {
                Logger.get().error("Wrong coordinates in Player" + playerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(occupiedTile, playerValue));
        }

        if(game.playerHasOverwriteStones(playerValue)) {
            // Add overwrite moves on expansion tiles
            for(var coordinate : game.gameStats.getAllCoordinatesWhereTileIs(Tile.EXPANSION)) {
                moves.add(new OverwriteMove(playerValue, coordinate));
            }
        }

        Logger.get().debug("Valid moves for Player " + playerValue + ":\n" +
                moves.stream().map(move -> "    " + move).collect(Collectors.joining("\n")));

        return moves;
    }

    public Set<Move> getAllBombMoves(Tile player) {

        Set<Move> result = new HashSet<>();

        // Bombs can be thrown on every tile which is not a wall.
        for (Tile tile : Tile.values()) {
            if (tile == Tile.WALL) {
                continue;
            }

            for (Coordinates position : game.getGameStats().getAllCoordinatesWhereTileIs(tile)) {
                result.add(new BombMove(player, position));
            }

        }
        return result;
    }

    /**
     * @param ownTileCoordinates one piece of this player
     * @param playerValue        Tile of player that moves are calculated for
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Coordinates ownTileCoordinates, Tile playerValue) {
        Logger.get()
                .verbose("Searching for valid moves originating from piece " + ownTileCoordinates);
        Set<Move> moves = new HashSet<>();
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
            if (firstNeighbourTile == playerValue) {
                continue;
            }

            // Check if the neighbour is the same tile due to a transition
            if (tileReader.getCoordinates() == ownTileCoordinates) {
                continue;
            }

            Set<Move> movesInDirection = getValidMovesForPieceInDirection(tileReader, playerValue, ownTileCoordinates);
            if (movesInDirection != null) {
                moves.addAll(movesInDirection);
            }
        }

        return moves;
    }

    /**
     * @param tileReader  tileReader with coordinates and direction of first neighbour of own tile
     * @param playerValue Tile of player that moves are calculated for
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMovesForPieceInDirection(TileReader tileReader, Tile playerValue, Coordinates ownTileCoordinates) {
        Logger.get().verbose("Searching for valid moves in direction ");

        Set<Move> movesPerDirection = new HashSet<>();
        Tile currentTile = tileReader.getTile();
        Coordinates firstNeighbourTileCoordinates = tileReader.getCoordinates();
        Coordinates currentCoordinates = firstNeighbourTileCoordinates;
        //boolean tilesBetweenExistingAndNewPiece = false;
        // As long as there is an ococcupied tile
        while (!currentTile.isUnoccupied()) {

            // Check if there is a dead end
            if (!tileReader.hasNext()) {
                return movesPerDirection;
            }

            tileReader.next();
            currentTile = tileReader.getTile();
            currentCoordinates = tileReader.getCoordinates();

            // Check if piece that we started from is reached
            if(currentCoordinates.equals(ownTileCoordinates)){
                return movesPerDirection;
            }

            // Overwrite Logic
            if (game.playerHasOverwriteStones(playerValue)) {
                //Check if currentTile is a Player and if it is not a neighbour from the own tile we started from
                if (currentTile.isPlayer() && !(currentCoordinates.equals(firstNeighbourTileCoordinates))) {
                    movesPerDirection.add(new OverwriteMove(playerValue, currentCoordinates));
                }
            }

            // If an own tile is overwritten, return because this tile is handled separately
            if (currentTile == playerValue) {
                return movesPerDirection;
            }
        }
        // If necessary create special move
        switch (currentTile) {
            case CHOICE -> {
                for (Tile playerTile : game.getAllParticipatingPlayers()) {
                    if (playerTile != currentTile) {
                        movesPerDirection.add(
                                new ChoiceMove(playerValue, currentCoordinates, playerTile));
                    }
                }
            }
            case INVERSION ->
                    movesPerDirection.add(new InversionMove(playerValue, currentCoordinates));
            case BONUS -> {
                movesPerDirection.add(new BonusMove(playerValue, currentCoordinates, Bonus.BOMB));
                movesPerDirection.add(
                        new BonusMove(playerValue, currentCoordinates, Bonus.OVERWRITE_STONE));
            }
            default -> movesPerDirection.add(new Move(playerValue, currentCoordinates));

        }

        return movesPerDirection;
    }
}
