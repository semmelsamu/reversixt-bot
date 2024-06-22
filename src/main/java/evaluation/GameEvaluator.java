package evaluation;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.MoveCalculator;

/**
 * Evaluates the current game situation for one player
 */
public class GameEvaluator {

    private final int[] evalPhaseThresholds = {0, 70, 82, 95};
    private final double[] mobilityFactorsPerEvalPhase = {1, 0.5, 0.25, 0};
    private final double[] ratedTileFactorsPerEvalPhase = {1, 1.5, 1.25, 1};
    private final double[] rawTileFactorsPerEvalPhase = {0, 0, 0.5, 1};
    private int[][] tileRatings;

    private BoardInfo boardInfo;

    public GameEvaluator(BoardInfo boardInfo) {
        this.boardInfo = boardInfo;
    }

    public void setBoardInfo(BoardInfo boardInfo) {
        this.boardInfo = boardInfo;
    }

    /**
     * @return Evaluation for the current game situation
     */
    public int evaluate(Game game, int player) {
        switch (game.getPhase()) {
            case BUILD -> {
                return evaluatePhase1(game, player);
            }
            case BOMB -> {
                return evaluatePhase2(game, player);
            }
            case END -> {
                return evaluateEnd(game, player);
            }
            // Exception if game phase will be added in the future
            default -> throw new IllegalArgumentException("Unknown gamePhase: " + game.getPhase());
        }
    }

    /**
     * Evaluation in phase 1 includes different criteria like rating of occupied tiles, mobility,
     * number of own overwrite stones / bombs ...
     */
    private int evaluatePhase1(Game game, int player) {
        int evalPhase = getEvalPhase(game);
        tileRatings = boardInfo.getTileRatings();
        double rating = 0;
        rating += ratedTileFactorsPerEvalPhase[evalPhase] *
                sumUpAllRatingsForOccupiedTiles(game, player);
        rating += mobilityFactorsPerEvalPhase[evalPhase] * evaluateMobility(game, player);
        rating += rawTileFactorsPerEvalPhase[evalPhase] *
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                        Tile.getTileForPlayerNumber(player)).size();
        rating += evaluateOverwriteStones(game, player, 50);
        rating += evaluateBombs(game, player, 10);
        return (int) rating;
    }

    // Evaluation in bomb phase just counts the occupied tiles
    private int evaluatePhase2(Game game, int player) {
        Tile playerTile = Tile.getTileForPlayerNumber(player);
        return game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(playerTile).size();
    }

    // Evaluation of an end game returns max int, if game is won. Otherwise same as phase 2
    private int evaluateEnd(Game game, int player) {
        int numberOfOwnTiles = 0;
        int maxNumberOfEnemyTiles = Integer.MIN_VALUE;
        for (int i = 1; i <= game.constants.initialPlayers(); i++) {
            if (i == player) {
                numberOfOwnTiles = getNumberOfTilesForPlayer(game, player);
            }
            maxNumberOfEnemyTiles = getNumberOfTilesForPlayer(game, player);
        }
        // If game is won, return max int
        if (numberOfOwnTiles >= maxNumberOfEnemyTiles) {
            return Integer.MAX_VALUE;
        }
        // If not return number of own tiles like in phase 2
        else {
            return numberOfOwnTiles;
        }
    }

    private int getEvalPhase(Game game) {
        int percentage = (int) ((double) game.totalTilesOccupiedCounter.getTotalTilesOccupied() /
                boardInfo.getReachableTiles() * 100 + 0.5);
        for (int i = 1; i < evalPhaseThresholds.length; i++) {
            if (percentage < evalPhaseThresholds[i]) {
                return (i - 1);
            }
        }
        return evalPhaseThresholds.length - 1;
    }

    private int evaluateOverwriteStones(Game game, int player, int valueOfOneStone) {
        return valueOfOneStone * game.getPlayer(player).getOverwriteStones();
    }

    private int evaluateBombs(Game game, int player, int valueOfOneStone) {
        return valueOfOneStone * game.getPlayer(player).getBombs();
    }

    private double evaluateMobility(Game game, int player) {
        int x = getNumberOfValidMoves(game, player);
        return 2 * logarithm(1.5, x + 0.5) + 0.25 * x - 3;
    }

    private int getNumberOfValidMoves(Game game, int player) {
        return MoveCalculator.getValidMovesForPlayer(game, player).size();
    }

    private int sumUpAllRatingsForOccupiedTiles(Game game, int player) {
        int sum = 0;
        for (Coordinates tile : game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                game.getPlayer(player).getPlayerValue())) {
            sum += tileRatings[tile.y][tile.x];
        }
        return sum;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility functions
    |
    |-----------------------------------------------------------------------------------------------
    */

    private double logarithm(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    private int getNumberOfTilesForPlayer(Game game, int player) {
        return game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                Tile.getTileForPlayerNumber(player)).size();
    }

    /*
    private void registerCriteria() {
        ratings.add(new CornerValuesCriterion(game));
        ratings.add(new AmountOverwriteStonesCriterion(game));
        ratings.add(new AmountBombsCriterion(game));
        ratings.add(new AmountValidMovesCriterion(game));
        ratings.add(new PrioritiseChoiceBonusMoveCriterion(game));
        ratings.add(new AmountTileCriterion(game));
        ratings.add(new TileFillLevelCriterion(game));

        // needs to be last one
        ratings.add(new InversionTileCriterion(game));
    }
    */
    /**
     * Returns the list of {@link MapTileRating} by {@link RatingType}
     * //TODO: may be unnecessary
     *
     * @param ratingType different types of ratings
     * @return list or null if not type found
     */
    /*
    public List<MapTileRating> getTileRatingsByRatingType(RatingType ratingType) {
        for (AbstractRating rating : ratings) {
            if (rating.getRatingType() == ratingType) {
                return rating.getMapTileRatings();
            }
        }
        return null;
    }

     */
    /*
    public int getPlayerRating() {
        return playerRating;
    }

    public void removeInversionTileCriterion() {
        ratings.removeIf(abstractRating -> abstractRating instanceof InversionTileCriterion);
    }

     */

    /**
     * Prints the player ratings by type
     *//*
    public void printRatings() {
        for (AbstractRating rating : ratings) {
            if (rating.getMapTileRatings().isEmpty()) {
                logger.log(rating.getRatingType().name() + ": " +
                        rating.getPlayerRatingByCriterion() + " with a weight of " +
                        rating.getWeight());
            } else {
                logger.log(rating.getRatingType().name() + ": " + rating.getMapTileRatings() +
                        " = " + rating.getPlayerRatingByCriterion() + " with a weight of " +
                        rating.getWeight());
            }
        }
    }
     */
}
