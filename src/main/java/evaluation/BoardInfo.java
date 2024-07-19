package evaluation;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import game.Game;
import game.GamePhase;
import move.Move;
import move.OverwriteMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BoardInfo {

    private final int[][] tileRatings;

    /**
     * Number of all tiles that are not walls
     */
    private final short potentialReachableTiles;

    /**
     * Number of occupied tiles after one example game (heuristic!)
     */

    private Game simulationGame = null;

    private short reachableTiles;

    private boolean wasWholeGameSimulated;

    private boolean noSignificantDifferenceToValueBefore;

    private boolean wereReachableTilesCalculated;

    private short sizeOfUpdateInterval;

    private short lastUpdate;

    private final Boundaries noOverwriteTheMoveBefore = new Boundaries(0, 0);

    public BoardInfo(Game initialGame) {
        tileRatings = calculateTileRatings(initialGame);
        potentialReachableTiles = calculatePotentialReachableTiles(initialGame);
        reachableTiles = potentialReachableTiles;
    }

    private int[][] calculateTileRatings(Game game) {
        int[][] tileRatings = new int[game.getHeight()][game.getWidth()];
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                if (game.getTile(new Coordinates(x, y)) == Tile.WALL) {
                    tileRatings[y][x] = 0;
                    continue;
                }
                tileRatings[y][x] = calculateParicularTileRating(x, y, game);
            }
        }
        return tileRatings;
    }

    private short calculatePotentialReachableTiles(Game initialGame) {
        int allTiles = initialGame.getWidth() * initialGame.getHeight();
        int allWallTiles =
                initialGame.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(Tile.WALL).size();
        return (short) (allTiles - allWallTiles);
    }

    /**
     * Simulates a whole game out the number of reachable tiles approximately
     */
    public void updateReachableTiles(Game game, int timelimit) {
        short newReachableTiles = 0;
        long time = System.currentTimeMillis();
        final int TIMECAP_MS = Math.min(1000, timelimit);
        Game purposeGame;
        // Check if simulation should be continued
        if(simulationGame != null){
            purposeGame = simulationGame;
        }
        else{
            purposeGame = game.clone();
        }

        // Is used to check if only overwrite moves were played for a whole round
        Boundaries boundaries = noOverwriteTheMoveBefore;

        while (System.currentTimeMillis() - time < TIMECAP_MS &&
                purposeGame.getPhase() == GamePhase.BUILD) {
            Set<Move> relevantMovesForCurrentPlayer = GameEvaluator.getRelevantMoves(purposeGame);
            List<Move> ListOfRelevantMoves = new ArrayList<>(relevantMovesForCurrentPlayer);

            if (ListOfRelevantMoves.get(0) instanceof OverwriteMove) {
                int playerNumber = purposeGame.getCurrentPlayerNumber();

                // Check if only overwrite moves were played for a whole round
                if (!boundaries.equals(noOverwriteTheMoveBefore) &&
                        (playerNumber >= boundaries.lowerBoundary() ||
                                playerNumber <= boundaries.upperBoundary())) {
                    break;
                } else {
                    boundaries = updateBoundaries(boundaries, playerNumber);
                }
            } else {
                boundaries = noOverwriteTheMoveBefore;
            }

            int randomIndex = (int) (Math.random() * relevantMovesForCurrentPlayer.size());
            Move randomMove = ListOfRelevantMoves.get(randomIndex);
            purposeGame.executeMove(randomMove);
        }

        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                if (purposeGame.getTile(new Coordinates(x, y)).isPlayer()) {
                    newReachableTiles++;
                }
            }
        }

        // Check if simulation went far enough
        if (System.currentTimeMillis() - time >= TIMECAP_MS &&
                3 * game.totalTilesOccupiedCounter.getTotalTilesOccupied() >=  newReachableTiles){
            simulationGame = purposeGame;
            return;
        }
        else{
            simulationGame = null;
        }

        int previousReachableTiles = reachableTiles;

        // Check if the next to last game was simulated to the end
        if (wasWholeGameSimulated) {
            reachableTiles = (short) avgOf(reachableTiles, newReachableTiles);
        } else {
            reachableTiles = newReachableTiles;
        }

        // Check if the last two simulations had a similar result
        if (reachableTiles > 0.95 * previousReachableTiles &&
                reachableTiles < 1.05 * previousReachableTiles) {
            noSignificantDifferenceToValueBefore = true;
        }
        long currentTime = System.currentTimeMillis() - time;

        // Check if the game was simulated to the end
        if (currentTime < TIMECAP_MS) {
            wasWholeGameSimulated = true;
            sizeOfUpdateInterval = (short) (reachableTiles * 0.3);
        }

        wereReachableTilesCalculated = true;
        lastUpdate = game.totalTilesOccupiedCounter.getTotalTilesOccupied();

    }

    public boolean hasReachableTilesToBeUpdated(Game game) {

        if (!wereReachableTilesCalculated) {
            return true;
        }

        // Check if calculated reachable tiles varied
        if (wasWholeGameSimulated && !noSignificantDifferenceToValueBefore) {
            // Reachable tiles are updated after each update interval
            if (game.totalTilesOccupiedCounter.getTotalTilesOccupied() - lastUpdate >=
                    sizeOfUpdateInterval) {
                return true;
            }
        }

        // Check if the progress of the current game requires an update of reachable tiles
        if (!wasWholeGameSimulated &&
                game.totalTilesOccupiedCounter.getTotalTilesOccupied() >= 0.6 * reachableTiles) {
            return true;
        }
        return false;
    }

    private Boundaries updateBoundaries(Boundaries boundaries, int playerNumber) {
        if (boundaries.equals(noOverwriteTheMoveBefore)) {
            return new Boundaries(getLowerBoundary(playerNumber), getUpperBoundary(playerNumber));
        }
        return new Boundaries(getLowerBoundary(playerNumber), boundaries.upperBoundary());
    }

    private int getLowerBoundary(int playerNumber) {
        return (playerNumber - 2) % 8 + 1;
    }

    private int getUpperBoundary(int playerNumber) {
        return playerNumber % 8 + 1;
    }

    /**
     * Default tile rating: 1. Checks all 4 angles on, whether they are only open in one direction,
     * Such angles are valuable, as tiles can "attack" in the angle but can not be "attacked". 1
     * bonuspoint for every angle, that fullfills the criterion. Rating for WALLS: 0
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Tile rating as an Integer
     */
    private int calculateParicularTileRating(int x, int y, Game game) {
        int tileRating = 1;
        Direction[] halfOfAllDirections = Arrays.copyOfRange(Direction.values(), 0, 4);
        for (Direction direction : halfOfAllDirections) {
            TileReader tileReader = new TileReader(game, new Coordinates(x, y), direction);
            TileReader oppositeDirectionTileReader =
                    new TileReader(game, new Coordinates(x, y), direction.getOppositeDirection());
            if (tileReader.hasNext() && !oppositeDirectionTileReader.hasNext()) {
                tileRating++;
                continue;
            }
            if (!tileReader.hasNext() && oppositeDirectionTileReader.hasNext()) {
                tileRating++;
            }
        }
        return tileRating;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public int[][] getTileRatings() {
        return tileRatings;
    }

    public short getReachableTiles() {
        return reachableTiles;
    }

    public short getPotentialReachableTiles() {
        return potentialReachableTiles;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    public int avgOf(int a, int b){
        return (int) ((a + b) / 2.0 + 0.5);
    }
}
