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
import java.util.stream.Collectors;

public class MoveCalculator {

    private Logger logger = new Logger(this.getClass().getName());

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
     * @param playerNumber Number of player that moves are calculated for
     * @return All valid moves for this player
     */
    public Set<Move> getValidMovesForPlayer(int playerNumber) {

        Player player = game.getPlayer(playerNumber);

        logger.log("Searching for all valid moves for Player " + player);

        Set<Move> result = new HashSet<>();

        switch (game.getGamePhase()) {
            case PHASE_1 -> result.addAll(calculateAllColoringMoves(playerNumber));
            case PHASE_2 -> result.addAll(getAllBombMoves(playerNumber));
            default -> logger.error("No valid game phase to calculate moves for");
        }

        logger.debug("Valid moves for Player " + player + ":\n" +
                result.stream().map(move -> "    " + move).collect(Collectors.joining("\n")));

        return result;
    }

    private Set<Move> calculateAllColoringMoves(int playerNumber) {
        logger.log("Calculating all coloring moves");

        Player player = game.getPlayer(playerNumber);

        HashSet<Move> moves = new HashSet<>();

        for (Coordinates occupiedTile : game.getAllCoordinatesWhereTileIs(player.getPlayerValue())) {
            if (game.getTile(occupiedTile) != player.getPlayerValue()) {
                logger.error("Wrong coordinates in Player" + player + "'s List stones");
                continue;
            }
            for(Direction direction : Direction.values()) {
                TileReader tileReader = new TileReader(game, occupiedTile, direction);
                Set<Move> movesForPieceInDirection =
                        getValidMovesForPieceInDirection(tileReader, playerNumber);
                if(movesForPieceInDirection != null){
                    moves.addAll(movesForPieceInDirection);
                }
            }
            //moves.addAll(getValidMovesForPiece(occupiedTile, playerValue));
        }

        if(player.getOverwriteStones() > 0) {
            // Add overwrite moves on expansion tiles
            for(var coordinate : game.gameStats.getAllCoordinatesWhereTileIs(Tile.EXPANSION)) {
                moves.add(new OverwriteMove(playerNumber, coordinate));
            }
        }

        return moves;
    }

    public Set<Move> getAllBombMoves(int player) {
        logger.log("Calculating all bomb moves");

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
     * @param tileReader  tileReader with coordinates and direction of first neighbour of own tile
     * @param playerNumber Tile of player that moves are calculated for
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMovesForPieceInDirection(TileReader tileReader, int playerNumber) {
        Player player = game.getPlayer(playerNumber);
        Set<Move> movesPerDirection = new HashSet<>();
        // Coordinates of tile moves are searched for
        Coordinates ownTileCoordinates = tileReader.getCoordinates();
        if(!isFirstNeighbourValid(tileReader, player.getPlayerValue())){
            return null;
        }
        // TileReader points on the first neighbour now!

        // Coordinates of first Neighbour from the tile moves are searched for
        Coordinates firstNeighbourTileCoordinates = tileReader.getCoordinates();
        // Stats of the current tile which is updated in the while loop
        Tile currentTile = tileReader.getTile();
        Coordinates currentCoordinates = firstNeighbourTileCoordinates;

        // While there is an ococcupied tile
        while (!currentTile.isUnoccupied()) {

            // Check if there is a dead end
            if (!tileReader.hasNext()) {
                return movesPerDirection;
            }

            // Go to next tile in direction
            tileReader.next();
            currentTile = tileReader.getTile();
            currentCoordinates = tileReader.getCoordinates();

            // Check if piece that we started from is reached
            if(currentCoordinates.equals(ownTileCoordinates)){
                return movesPerDirection;
            }

            // Overwrite Logic
            if (player.getOverwriteStones() > 0) {
                // Check if current Tile is the neighbour from the tile moves are searched for
                if(!(currentCoordinates.equals(firstNeighbourTileCoordinates))){
                    movesPerDirection.add(new OverwriteMove(playerNumber, currentCoordinates));
                }
            }

            // If an own tile is overwritten, return because this tile is handled separately
            if (currentTile == player.getPlayerValue()) {
                return movesPerDirection;
            }
        }
        // If necessary create special move
        switch (currentTile) {
            case CHOICE -> {
                for(int playerToSwapWith = 1; playerToSwapWith < game.getPlayers().length; playerToSwapWith++) {
                        movesPerDirection.add(
                                new ChoiceMove(playerNumber, currentCoordinates, playerToSwapWith));
                }
            }
            case INVERSION ->
                    movesPerDirection.add(new InversionMove(playerNumber, currentCoordinates));
            case BONUS -> {
                movesPerDirection.add(new BonusMove(playerNumber, currentCoordinates, Bonus.BOMB));
                movesPerDirection.add(
                        new BonusMove(playerNumber, currentCoordinates, Bonus.OVERWRITE_STONE));
            }
            default -> movesPerDirection.add(new NormalMove(playerNumber, currentCoordinates));

        }

        return movesPerDirection;
    }

    /**
     * Check if firstNeighbour from own tile allows possible moves
     * Moves the pointer of tileReader on firstNeighbour by calling next()
     * @param tileReader Tile reader pointing on own tile
     *                   -> Points on first neighbour after method call
     * @param playerValue Tile of player that moves are calculated for
     * @return True if first neighbour allows moves, false if not
     */
    private boolean isFirstNeighbourValid(TileReader tileReader, Tile playerValue){
        Coordinates ownTileCoordinates = tileReader.getCoordinates();
        // Check if tile has a neighbour in this direction
        if (!tileReader.hasNext()) {
            return false;
        }
        tileReader.next();
        Tile firstNeighbourTile = tileReader.getTile();
        // Check if first neighbour tile is unoccupied
        if (firstNeighbourTile.isUnoccupied()) {
            return false;
        }

        // Check if first neighbour tile is an own tile
        if (firstNeighbourTile == playerValue) {
            return false;
        }

        // Check if the neighbour is the same tile due to a transition
        if (tileReader.getCoordinates() == ownTileCoordinates) {
            return false;
        }
        return true;
    }
}
