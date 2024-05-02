package evaluation;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import game.Game;
import game.MoveCalculator;

import java.util.Arrays;

/**
 * Evaluates the current game situation for one player
 */
public class GameEvaluator {

    // TODO: should be placed in a higher standing class, as it is valid for the whole game
    // private static int[][] tileRatings;

    /**
     * @return Evaluation for the current game situation
     */
    public static int evaluate(Game game, int player) {
        // tileRatings = calculateTileRatings(game, player);
        double rating = 0;
        // rating += sumUpAllRatingsForOccupiedTiles(game, player);
        rating += evaluateMobility(game, player);
        rating += evaluateOverwriteStones(game, player, 5);
        // As there are currently only bombs with radius 0 allowed, the value of bombs is only 1
        rating += evaluateBombs(game, player, 1);

        return (int) rating;
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
        MoveCalculator moveCalculator = new MoveCalculator(game);
        return moveCalculator.getValidMovesForPlayer(player).size();
    }

    /**
     * TileRatings
     */
    /*
    private static int sumUpAllRatingsForOccupiedTiles(Game game, int player) {
        int sum = 0;
        for (Coordinates tile : game.getAllCoordinatesWhereTileIs(game.getPlayer(player).getPlayerValue())) {
            sum += tileRatings[tile.y][tile.x];
        }
        return sum;
    }
     */

    /**
     * Calls calculateParticularTileRating(x, y) for each coordinate
     * @return Array of tileRatings
     */
    private static int[][] calculateTileRatings(Game game, int player) {
        int width = game.getWidth();
        int height = game.getHeight();
        int[][] tileRatings = new int[height][width];
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(game.getTile(new Coordinates(j, i)) == Tile.WALL){
                    tileRatings[i][j] = 0;
                    continue;
                }
                tileRatings[i][j] = calculateParicularTileRating(game, player, j, i);
            }
        }
        return tileRatings;
    }

    /**
     * Default tile rating: 1
     * Checks all 4 angles on, whether they are only open in one direction
     * Such angles are valuable, as tiles can "attack" in the angle but can not be "attacked"
     * 1 bonuspoint for every angle, that fullfills the criterion
     * Rating for WALLS: 0
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Tile rating as an Integer
     */
    private static int calculateParicularTileRating(Game game, int player, int x, int y){
        int tileRating = 1;
        Direction[] halfOfAllDirections = Arrays.copyOfRange(Direction.values(),0, 4);
        for(Direction direction : halfOfAllDirections) {
            TileReader tileReader = new TileReader(game, new Coordinates(x, y), direction);
            TileReader oppositeDirectionTileReader =
                    new TileReader(game, new Coordinates(x, y), direction.getOppositeDirection());
            if(tileReader.hasNext() && !oppositeDirectionTileReader.hasNext()){
                tileRating++;
                continue;
            }
            if(!tileReader.hasNext() && oppositeDirectionTileReader.hasNext()){
                tileRating++;
            }
        }
        return tileRating;
    }

    /**
     * Math
     */
    private static double logarithm(double base, double x) {
        return Math.log(x) / Math.log(base);
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
     */
    /*
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
