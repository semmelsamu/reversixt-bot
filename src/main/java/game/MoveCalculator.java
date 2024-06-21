package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import exceptions.GamePhaseNotValidException;
import move.*;

import java.util.HashSet;
import java.util.Set;

public final class MoveCalculator {

    /**
     * @param playerNumber Number of player that moves are calculated for
     * @return All valid moves for this player
     */
    public static Set<Move> getValidMovesForPlayer(Game game, int playerNumber) {

        Set<Move> result = new HashSet<>();

        switch (game.getPhase()) {
            case BUILD -> result.addAll(calculateAllColoringMoves(game, playerNumber));
            case BOMB -> result.addAll(getAllBombMoves(game, playerNumber));
            default -> throw new GamePhaseNotValidException(
                    "No valid game phase to calculate moves for");
        }

        return result;
    }

    private static Set<Move> calculateAllColoringMoves(Game game, int playerNumber) {

        Player player = game.getPlayer(playerNumber);

        HashSet<Move> moves = new HashSet<>();

        for (Coordinates occupiedTile : game.getAllCoordinatesWhereTileIs(
                player.getPlayerValue())) {
            if (game.getTile(occupiedTile) != player.getPlayerValue()) {
                throw new AssertionError("Wrong coordinates in Player" + player + "'s List stones");
            }
            for (Direction direction : Direction.values()) {
                TileReader tileReader = new TileReader(game, occupiedTile, direction);
                Set<Move> movesForPieceInDirection =
                        getValidMovesForPieceInDirection(game, tileReader, playerNumber);
                if (movesForPieceInDirection != null) {
                    moves.addAll(movesForPieceInDirection);
                }
            }
            //moves.addAll(getValidMovesForPiece(occupiedTile, playerValue));
        }

        if (player.getOverwriteStones() > 0) {
            // Add overwrite moves on expansion tiles
            for (var coordinate : game.stats.getAllCoordinatesWhereTileIs(Tile.EXPANSION)) {
                moves.add(new OverwriteMove(playerNumber, coordinate));
            }
        }

        return moves;
    }

    private static Set<Move> getAllBombMoves(Game game, int player) {

        if (game.getPlayer(player).getBombs() == 0) {
            return new HashSet<>();
        }

        Set<Move> result = new HashSet<>();

        // Bombs can be thrown on every tile which is not a wall.
        for (Tile tile : Tile.values()) {
            if (tile == Tile.WALL) {
                continue;
            }

            for (Coordinates position : game.stats.getAllCoordinatesWhereTileIs(tile)) {
                result.add(new BombMove(player, position));
            }

        }
        return result;
    }


    /**
     * @param tileReader   tileReader with coordinates and direction of first neighbour of own tile
     * @param playerNumber Tile of player that moves are calculated for
     * @return Valid moves for one piece for one of eight directions
     */
    private static Set<Move> getValidMovesForPieceInDirection(Game game, TileReader tileReader,
                                                              int playerNumber) {
        Player player = game.getPlayer(playerNumber);
        Set<Move> movesPerDirection = new HashSet<>();
        // Coordinates of tile moves are searched for
        Coordinates ownTileCoordinates = tileReader.getCoordinates();
        if (!isFirstNeighbourValid(tileReader, player.getPlayerValue())) {
            return null;
        }
        // TileReader points on the first neighbour now!

        // Coordinates of first Neighbour from the tile moves are searched for
        Coordinates firstNeighbourTileCoordinates = tileReader.getCoordinates();
        // Stats of the current tile which is updated in the while loop
        Tile currentTile = tileReader.getTile();
        Coordinates currentCoordinates = firstNeighbourTileCoordinates;

        // While there is an occupied tile
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
            if (currentCoordinates.equals(ownTileCoordinates)) {
                return movesPerDirection;
            }

            // Overwrite Logic
            // Check if player has overwrite stones an if the current tile can be overwritten
            if (player.getOverwriteStones() > 0 && currentTile.isPlayer()) {
                // Check if current Tile is the neighbour from the tile moves are searched for
                if (!(currentCoordinates.equals(firstNeighbourTileCoordinates))) {
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
                for (int playerToSwapWith = 1; playerToSwapWith <= game.getPlayers().length;
                     playerToSwapWith++) {
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
     * moves the pointer of tileReader on firstNeighbour by calling next()
     *
     * @param tileReader  Tile reader pointing on own tile
     *                    -> Points on first neighbour after method call
     * @param playerValue Tile of player that moves are calculated for
     * @return True if first neighbour allows moves, false if not
     */
    private static boolean isFirstNeighbourValid(TileReader tileReader, Tile playerValue) {
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
        return tileReader.getCoordinates() != ownTileCoordinates;
    }
}
