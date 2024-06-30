package evaluation;

import board.Coordinates;
import board.Tile;
import game.Community;
import game.Game;
import game.logic.MoveCalculator;
import move.*;
import util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates the current game situation for one player
 */
public class GameEvaluator {

    private final int[] evalPhaseThresholds = {0, 70, 82, 95};
    private final double[] mobilityFactorsPerEvalPhase = {1, 0.5, 0.25, 0};
    private final double[] ratedTileFactorsPerEvalPhase = {1, 1.5, 1.25, 1};
    private final double[] rawTileFactorsPerEvalPhase = {0, 0, 0.5, 1};

    private final BoardInfo boardInfo;

    public boolean evaluateCommunities;

    /**
     * Stores how many cutoffs a move on a certain depth has achieved.
     */
    private Map<Integer, Map<Move, Integer>> moveCutoffs;

    // Values of bonus stones
    private final int OVERWRITESTONE_VAL = 50;
    private final int BOMB_VAL = 10;

    public GameEvaluator(BoardInfo boardInfo) {
        this.boardInfo = boardInfo;
        moveCutoffs = new HashMap<>();
    }

    /**
     * @return Evaluation for the current game situation
     */
    public int evaluate(Game game, int player) {

        if (evaluateCommunities && game.communities == null) {
            Logger.get().error("Communities were on at top but are off at bottom");
        }

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
        double rating = 0;
        rating += ratedTileFactorsPerEvalPhase[evalPhase] *
                sumUpAllRatingsForOccupiedTiles(game, player);
        rating += mobilityFactorsPerEvalPhase[evalPhase] * evaluateMobility(game, player);
        rating += rawTileFactorsPerEvalPhase[evalPhase] *
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                        game.getPlayer(player).getPlayerValue()).size();
        rating += evaluateOverwriteStones(game, player);
        rating += evaluateBombs(game, player);
        rating += evaluateDeadCommunity(game, player);
        return (int) rating;
    }

    // Evaluation in bomb phase just counts the occupied tiles
    private int evaluatePhase2(Game game, int player) {
        Tile playerTile = game.getPlayer(player).getPlayerValue();
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

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |    Evaluation of criteria
    |
    |-----------------------------------------------------------------------------------------------
    */

    private int evaluateOverwriteStones(Game game, int player) {
        return OVERWRITESTONE_VAL * game.getPlayer(player).getOverwriteStones();
    }

    private int evaluateBombs(Game game, int player) {
        return BOMB_VAL * game.getPlayer(player).getBombs();
    }

    private double evaluateMobility(Game game, int player) {
        // Only non overwrite moves are considered for evaluation, as number of overwrite moves
        // is not very significant
        int movesWithoutOverwrites =
                (int) MoveCalculator.getValidMovesForPlayer(game, player, null).stream()
                        .filter((move) -> !(move instanceof OverwriteMove)).count();

        // calculate value of function that has a logarithmic gradient (and a little linear one)
        // -> difference between 0 and 5 moves is huge, between 40 and 45 little
        return 2 * logarithm(1.5, movesWithoutOverwrites + 0.5) + 0.25 * movesWithoutOverwrites - 3;
    }

    private int sumUpAllRatingsForOccupiedTiles(Game game, int player) {
        int sum = 0;
        for (Coordinates tile : game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                game.getPlayer(player).getPlayerValue())) {
            sum += boardInfo.getTileRatings()[tile.y][tile.x];
        }
        return sum;
    }

    /**
     * If exists, values the currently simulated Community bad when there is only one Player
     * present. This is bad as then we cannot expand this Community further and thus not occupy more
     * Tiles from this Community, aka it is dead.
     */
    private int evaluateDeadCommunity(Game game, int player) {
        if (game.communities == null || game.communities.getSimulating() == null) {
            return 0;
        }

        // Fetch Community because we need it often
        Community community = game.communities.getSimulating();

        // Count Players in this Community
        int playersInCommunity = 0;
        for (var currentPlayer : game.getPlayers()) {
            if (community.getTileCount(currentPlayer.getPlayerValue()) > 0) {
                playersInCommunity++;
            }
        }

        if (playersInCommunity > 1) {
            // Everything OK.
            return 0;
        }

        // Community is dead!

        int actualCoordinates = community.getTileCount(game.getPlayer(player).getPlayerValue());
        int potentialCoordinates = community.getReachableCoordinates().size();

        // Should never happen
        if (potentialCoordinates == 0) {
            Logger.get().warn("Evaluating Community with 0 potential Coordinates");
            Logger.get().warn(community.toString());
            return -1000;
        }

        double wastedPotential = 1 - (double) actualCoordinates / potentialCoordinates;

        // Punish for potential wasted
        return -1 * (int) (wastedPotential * 100);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Tree Evaluation
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Add a cutoff to the statistics.
     * @param move  Which move achieved the cutoff
     * @param depth On which depth the cutoff was achieved
     */
    public void addCutoff(Move move, int depth) {
        moveCutoffs.putIfAbsent(depth, new HashMap<>());
        moveCutoffs.get(depth).put(move, moveCutoffs.get(depth).getOrDefault(move, 0) + 1);
    }

    // TODO: Move evaluateStats() from Search here

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    private double logarithm(double base, double x) {
        return Math.log(x) / Math.log(base);
    }

    private int getNumberOfTilesForPlayer(Game game, int player) {
        return game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                game.getPlayer(player).getPlayerValue()).size();
    }

    private int getTileRatingForMove(Move move) {
        int y = move.getCoordinates().y;
        int x = move.getCoordinates().x;
        return boardInfo.getTileRatings()[y][x];
    }

    public boolean isSpecialMove(Move move) {
        return move instanceof BonusMove || move instanceof ChoiceMove ||
                move instanceof InversionMove;
    }

    public static Set<Move> getRelevantMoves(Game game) {

        // TODO: What if we only have one non-overwrite move which gets us in a really bad
        //  situation, but we could use an overwrite move which would help us A LOT?

        // TODO: Make decision between bomb or overwrite bonus in evaluation

        Set<Move> movesWithoutOverwrites =
                game.getValidMoves().stream().filter((move) -> !(move instanceof OverwriteMove))
                        .collect(Collectors.toSet());

        if (movesWithoutOverwrites.isEmpty()) {
            return game.getValidMoves();
        } else {
            return movesWithoutOverwrites;
        }
    }

    public List<Move> prepareMoves(Game game) {

        List<Move> result = new LinkedList<>(getRelevantMoves(game));

        // Dirty sort
        result.sort((move1, move2) -> {
            Map<Move, Integer> cutoffsOnDepth =
                    moveCutoffs.getOrDefault(game.getMoveCounter(), new HashMap<>());
            int compareCutoffs = Integer.compare(cutoffsOnDepth.getOrDefault(move1, 0),
                    cutoffsOnDepth.getOrDefault(move2, 0));
            if (compareCutoffs != 0) {
                return compareCutoffs;
            }
            if (!isSpecialMove(move1) && isSpecialMove(move2)) {
                return -1;
            } else if (isSpecialMove(move1) && !isSpecialMove(move2)) {
                return 1;
            }

            return Integer.compare(getTileRatingForMove(move1), getTileRatingForMove(move2));
        });

        return result;
    }
}
