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

public class StaticGameStats {
    /**
     * The number of players this game usually starts with.
     */
    private final int initialPlayers;

    /**
     * The number of overwrite stones each player has in the beginning.
     */
    private final int initialOverwriteStones;

    /**
     * The number of bombs each player has in the beginning.
     */
    private final int initialBombs;

    /**
     * The amount of steps from the center of the explosion a bomb blows tiles up.
     */
    private final int bombRadius;
    /**
     * Width of board
     */
    private final int width;
    /**
     * Height of board
     */
    private final int height;

    private final int[][] tileRatings;
    /**
     * Number of all tiles that are not walls
     */
    private final short potentialReachableTiles;
    /**
     * Number of occupied tiles after one example game (heuristic!)
     */
    private short reachableTiles;
    /**
     * Constant that is used for in updateReachableTiles
     */

    private boolean isReachableTilesSetFinally;

    private final Boundaries noOverwriteTheMoveBefore = new Boundaries(0, 0);

    public StaticGameStats(Game initialGame, int initialPlayers, int initialOverwriteStones,
                           int initialBombs, int bombRadius) {
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        width = initialGame.getWidth();
        height = initialGame.getHeight();
        tileRatings = calculateTileRatings(initialGame);
        potentialReachableTiles = calculatePotentialReachableTiles(initialGame);
        reachableTiles = 0;
        isReachableTilesSetFinally = false;
    }

    private int[][] calculateTileRatings(Game game) {
        int[][] tileRatings = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
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
        int allTiles = width * height;
        int allWallTiles = initialGame.getAllCoordinatesWhereTileIs(Tile.WALL).size();
        return (short) (allTiles - allWallTiles);
    }

    /**
     * Simulates a whole game out the number of reachable tiles approximately
     */
    public void updateReachableTiles(Game game, int timelimit) {
        long time = System.currentTimeMillis();
        final int TIMECAP = Math.min(1000, timelimit);
        Game purposeGame = game.clone();

        // Is used to check if only overwrite moves were played for a whole round
        Boundaries boundaries = noOverwriteTheMoveBefore;

        while (System.currentTimeMillis() - time < TIMECAP &&
                purposeGame.getPhase() == GamePhase.BUILD) {
            Set<Move> validMovesForCurrentPlayer = purposeGame.getRelevantMovesForCurrentPlayer();
            List<Move> ListOfRelevantMoves = new ArrayList<>(validMovesForCurrentPlayer);
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

            if(System.currentTimeMillis() - time < TIMECAP){
                isReachableTilesSetFinally = true;
            }
            int randomIndex = (int) (Math.random() * validMovesForCurrentPlayer.size());
            Move randomMove = ListOfRelevantMoves.get(randomIndex);
            purposeGame.executeMove(randomMove);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (purposeGame.getTile(new Coordinates(x, y)).isPlayer()) {
                    reachableTiles++;
                }
            }
        }
        System.out.println("Potential reachable tiles: " + potentialReachableTiles);
        printAmountOfReachableTiles();
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

    public void printAmountOfReachableTiles() {
        System.out.println();
        System.out.println("\nNumber of reachable tiles: " + reachableTiles);
    }

    /**
     * Default tile rating: 1
     * Checks all 4 angles on, whether they are only open in one direction
     * Such angles are valuable, as tiles can "attack" in the angle but can not be "attacked"
     * 1 bonuspoint for every angle, that fullfills the criterion
     * Rating for WALLS: 0
     *
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

    /**
     * Getter
     */
    public int getBombRadius() {
        return bombRadius;
    }

    public int getInitialPlayers() {
        return initialPlayers;
    }

    public int getInitialOverwriteStones() {
        return initialOverwriteStones;
    }

    public int getInitialBombs() {
        return initialBombs;
    }

    public int[][] getTileRatings() {
        return tileRatings;
    }

    public short getReachableTiles() {
        return reachableTiles;
    }

    public short getPotentialReachableTiles() {
        return potentialReachableTiles;
    }

    public boolean isReachableTilesSetFinally() {
        return isReachableTilesSetFinally;
    }
}
