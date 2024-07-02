package evaluation;

import board.Coordinates;
import board.Tile;
import game.Community;
import game.Game;
import game.logic.MoveCalculator;
import move.*;
import util.Constants;

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

    /**
     * Stores how many cutoffs a move on a certain depth has achieved.
     */
    private Map<Integer, Map<Move, Integer>> moveCutoffs;

    /**
     * Value of an overwrite stone for a player
     */
    private static final int OVERWRITE_STONE_VALUE = 50;
    /**
     * Value of a bomb for a player
     */
    private static final int BOMB_VALUE = 10;

    public GameEvaluator(BoardInfo boardInfo) {
        this.boardInfo = boardInfo;
        moveCutoffs = new HashMap<>();
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

    /**
     * Evaluation in bomb phase considers the ranking of the player and how large the differences
     * between the number of tiles of him or her and his or her closest competitors (enemies 2
     * rankings ahead and behind if existing) are
     */
    private int evaluatePhase2(Game game, int player) {
        Map<Integer, Integer> numberOfTilesForEachPlayer = new HashMap<>();
        for (int i = 1; i <= game.constants.initialPlayers(); i++) {
            numberOfTilesForEachPlayer.put(i,
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(Tile.fromInt(i))
                            .size());
        }

        // Sort players by number of tiles descending
        List<Map.Entry<Integer, Integer>> numberOfTilesList =
                new ArrayList<>(numberOfTilesForEachPlayer.entrySet());
        numberOfTilesList.sort(
                (entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));

        int ourIndex = findIndexByKey(numberOfTilesList, player);
        int ourTiles = numberOfTilesList.get(ourIndex).getValue();

        List<Integer> tileDifferencesOfCompetitorsAhead = new ArrayList<>();
        List<Integer> tileDifferencesOfCompetitorsBehind = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            int indexOfTeamAhead = ourIndex - i;
            int indexOfTeamBehind = ourIndex + i;

            if(indexOfTeamAhead >= 0){
                tileDifferencesOfCompetitorsAhead.add(
                        numberOfTilesList.get(indexOfTeamAhead).getValue() - ourTiles);
            }

            if(indexOfTeamBehind < numberOfTilesList.size()){
                tileDifferencesOfCompetitorsBehind.add(
                        ourTiles - numberOfTilesList.get(indexOfTeamBehind).getValue());
            }
        }

        return getRankingBonus(game, ourIndex) +
                rateTileDifferences(tileDifferencesOfCompetitorsAhead) -
                rateTileDifferences(tileDifferencesOfCompetitorsBehind);
    }

    // Evaluation of an end game returns max int, if game is won. Otherwise same as phase 2
    private int evaluateEnd(Game game, int player) {
        int numberOfOwnTiles = 0;
        int maxNumberOfEnemyTiles = java.lang.Integer.MIN_VALUE;
        for (int i = 1; i <= game.constants.initialPlayers(); i++) {
            if (i == player) {
                numberOfOwnTiles = getNumberOfTilesForPlayer(game, player);
            }
            maxNumberOfEnemyTiles = getNumberOfTilesForPlayer(game, player);
        }
        // If game is won, return max int
        if (numberOfOwnTiles >= maxNumberOfEnemyTiles) {
            return java.lang.Integer.MAX_VALUE;
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
        return OVERWRITE_STONE_VALUE * game.getPlayer(player).getOverwriteStones();
    }

    private int evaluateBombs(Game game, int player) {
        return BOMB_VALUE * game.getPlayer(player).getBombs();
    }

    private double evaluateMobility(Game game, int player) {
        Set<Move> moves;

        // Check if Moves are already cached
        if (game.getCurrentPlayerNumber() == player && game.communities != null) {
            moves = game.getValidMoves();
        } else {
            moves = MoveCalculator.getValidMovesForPlayer(game, player, null);
        }

        // Only non overwrite moves are considered for evaluation, as number of overwrite moves
        // is not very significant
        int movesWithoutOverwrites =
                (int) moves.stream().filter((move) -> !(move instanceof OverwriteMove)).count();

        // Calculate value of function that has a logarithmic gradient (and a little linear one)
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
     * Evaluates the current state of the simulated community in the game. If there is only one
     * player present in the community, it is considered "dead" because it cannot expand further,
     * thus limiting the ability to occupy more tiles. This function calculates and returns a
     * penalty value based on the wasted potential of the community.
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

        int missedCoordinates = potentialCoordinates - actualCoordinates;

        // Punish for potential wasted
        return -1 * missedCoordinates * Constants.DEAD_COMMUNITY_PUNISHMENT_FACTOR;
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

    public <K, V> int findIndexByKey(List<Map.Entry<K, V>> entryList, K keyToFind) {
        for (int i = 0; i < entryList.size(); i++) {
            if (entryList.get(i).getKey().equals(keyToFind)) {
                return i;
            }
        }
        return -1;
    }

    private int getRankingBonus(Game game, int index){
        return -(index + 1 - game.constants.initialPlayers()) * 1000;
    }

    private int rateTileDifferences(List<Integer> tileDifferences) {
        double result = 0;
        for (int difference : tileDifferences) {
            result += Math.pow(0.95, difference) * 480;
        }
        return (int) result;
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

    /**
     * Return all relevant Moves, sorted by relevance ascending. This means, Moves that are most
     * relevant for the Max-Player are at the end of the list.
     */
    public List<Move> prepareMoves(Game game) {

        List<Move> result = new LinkedList<>(getRelevantMoves(game));

        // Dirty sort
        // TODO: Sort by amount of tiles colored?
        result.sort((move1, move2) -> {

            Map<Move, Integer> cutoffsOnDepth =
                    moveCutoffs.getOrDefault(game.getMoveCounter(), new HashMap<>());

            int compareCutoffs = java.lang.Integer.compare(cutoffsOnDepth.getOrDefault(move1, 0),
                    cutoffsOnDepth.getOrDefault(move2, 0));
            if (compareCutoffs != 0) {
                return compareCutoffs;
            }

            if (!isSpecialMove(move1) && isSpecialMove(move2)) {
                return -1;
            } else if (isSpecialMove(move1) && !isSpecialMove(move2)) {
                return 1;
            }

            return java.lang.Integer.compare(getTileRatingForMove(move1),
                    getTileRatingForMove(move2));
        });

        return result;
    }
}
