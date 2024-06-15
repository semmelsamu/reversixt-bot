package evaluation;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.GamePhase;
import game.MoveCalculator;

/**
 * Evaluates the current game situation for one player
 */
public final class GameEvaluator {
    private static int[][] tileRatings;

    /**
     * @return Evaluation for the current game situation
     */
    public static int evaluate(Game game, int player) {
        switch (game.getPhase()){
            case BUILD -> {
                return evaluatePhase1(game, player);
            }
            case BOMB -> {
                return evaluatePhase2(game, player);
            }
            case END ->{
                return evaluateEnd(game, player);
            }
            // Exception if game phase will be added in the future
            default -> throw new IllegalArgumentException("Unknown gamePhase: " + game.getPhase());
        }
    }

    /** Evaluation in phase 1 includes different criteria like rating of occupied tiles, mobility,
       number of own overwrite stones / bombs ...
     */
    private static int evaluatePhase1(Game game, int player) {
        tileRatings = game.staticGameStats.getTileRatings();
        double rating = 0;
        rating += sumUpAllRatingsForOccupiedTiles(game, player);
        if(game.getPhase().equals(GamePhase.BUILD))
            rating += evaluateMobility(game, player);
        rating += evaluateOverwriteStones(game, player, 10);
        // As there are currently only bombs with radius 0 allowed, the value of bombs is only 1
        rating += evaluateBombs(game, player, 1);
        return (int) rating;
    }

    // Evaluation in bomb phase just counts the occupied tiles
    private static int evaluatePhase2(Game game, int player) {
        Tile playerTile = Tile.getTileForPlayerNumber(player);
        return game.getAllCoordinatesWhereTileIs(playerTile).size();
    }

    // Evaluation of an end game returns max int, if game is won. Otherwise same as phase 2
    private static int evaluateEnd(Game game, int player) {
        int numberOfOwnTiles = 0;
        int maxNumberOfEnemyTiles = Integer.MIN_VALUE;
        for(int i = 1; i <= game.staticGameStats.getInitialPlayers(); i++){
            if(i == player){
                numberOfOwnTiles = getNumberOfTilesForPlayer(game, player);
            }
            maxNumberOfEnemyTiles = getNumberOfTilesForPlayer(game, player);
        }
        // If game is won, return max int
        if(numberOfOwnTiles >= maxNumberOfEnemyTiles){
            return Integer.MAX_VALUE;
        }
        // If not return number of own tiles like in phase 2
        else{
            return numberOfOwnTiles;
        }
    }

    /**
     * OverwriteStones
     */
    private static int evaluateOverwriteStones(Game game, int player, int valueOfOneStone){
        return valueOfOneStone * game.getPlayer(player).getOverwriteStones();
    }

    /**
     *
     */
    private static int evaluateBombs(Game game, int player, int valueOfOneStone){
        return valueOfOneStone * game.getPlayer(player).getBombs();
    }

    /**
     * Mobility
     */
    private static double evaluateMobility(Game game, int player){
        int x = getNumberOfValidMoves(game, player);
        return 2 * logarithm(1.5, x + 0.5) + 0.25 * x - 3;
    }

    private static int getNumberOfValidMoves(Game game, int player){
        return MoveCalculator.getValidMovesForPlayer(game, player).size();
    }

    /**
     * TileRatings
     */

    private static int sumUpAllRatingsForOccupiedTiles(Game game, int player) {
        int sum = 0;
        for (Coordinates tile : game.getAllCoordinatesWhereTileIs(game.getPlayer(player).getPlayerValue())) {
            sum += tileRatings[tile.y][tile.x];
        }
        return sum;
    }

    /**
     * util
     */
    private static double logarithm(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    private static int getNumberOfTilesForPlayer(Game game, int player) {
        return game.getAllCoordinatesWhereTileIs(Tile.getTileForPlayerNumber(player)).size();
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
