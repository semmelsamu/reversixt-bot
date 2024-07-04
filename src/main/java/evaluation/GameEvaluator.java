package evaluation;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import clients.SearchTimer;
import exceptions.OutOfTimeException;
import game.Game;
import game.logic.MoveCalculator;
import move.*;
import util.Timer;
import util.Triple;
import util.Tuple;

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
    private final Map<Integer, Map<Move, Integer>> moveCutoffs;

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
     * Evaluate the current Game situation and calculate a score. Higher is better.
     */
    public int evaluate(Game game, int player) {

        switch (game.getPhase()) {
            case BUILD -> {
                return evaluateBuildPhase(game, player);
            }
            case BOMB -> {
                return evaluateBombPhase(game, player);
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
    private int evaluateBuildPhase(Game game, int player) {
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
        return (int) rating;
    }

    /**
     * Evaluation in bomb phase considers the ranking of the player and how large the differences
     * between the number of tiles of him or her and his or her closest competitors (enemies 2
     * rankings ahead and behind if existing) are
     */
    private int evaluateBombPhase(Game game, int player) {
        List<Tuple<Integer, Integer>> numberOfTilesList =
                getTilesForEachPlayerSortedDescending(game);

        int ourIndex = findIndexByKey(numberOfTilesList, player);
        int ourRanking = ourIndex + 1;
        int ourTiles = numberOfTilesList.get(ourIndex).second();

        List<Integer> tileDifferencesOfCompetitorsAhead = new ArrayList<>();
        List<Integer> tileDifferencesOfCompetitorsBehind = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            int indexOfTeamAhead = ourIndex - i;
            int indexOfTeamBehind = ourIndex + i;

            if (indexOfTeamAhead >= 0) {
                tileDifferencesOfCompetitorsAhead.add(
                        numberOfTilesList.get(indexOfTeamAhead).second() - ourTiles);
            }

            if (indexOfTeamBehind < numberOfTilesList.size()) {
                tileDifferencesOfCompetitorsBehind.add(
                        ourTiles - numberOfTilesList.get(indexOfTeamBehind).second());
            }
        }

        return getRankingBonus(game, ourRanking) +
                rateTileDifferences(tileDifferencesOfCompetitorsAhead) -
                rateTileDifferences(tileDifferencesOfCompetitorsBehind);
    }

    /**
     * Only the ranking is evaluated, as it is the final rating. Return max int if game is won
     */
    private int evaluateEnd(Game game, int player) {
        List<Tuple<Integer, Integer>> numberOfTilesList =
                getTilesForEachPlayerSortedDescending(game);

        int ourRanking = findIndexByKey(numberOfTilesList, player) + 1;

        if (ourRanking == 1) {
            return Integer.MAX_VALUE;
        } else {
            return game.constants.initialPlayers() - ourRanking;
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
        if (game.getCurrentPlayerNumber() == player) {
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

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Move sorting
    |
    |-----------------------------------------------------------------------------------------------
    */

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
     * Quickest and roughest Move sorting. Sorts by special Moves, Cutoffs and Tile ratings. Should
     * be used in the Search Tree.
     */
    public List<Move> sortMovesQuicker(Game game) {

        List<Move> result = new LinkedList<>(getRelevantMoves(game));

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

    /**
     * Quick and rough Move sorting. Sorts by Tile ratings and number of potential colored Tiles.
     * Should be used in Phi Move and in the beginning.
     */
    public List<Move> sortMovesQuick(Game game) {

        List<Tuple<Move, Integer>> data = new LinkedList<>();

        // Gather data
        for (Move move : getRelevantMoves(game)) {
            int tileRating = getTileRatingForMove(move);
            int tilesColored = getTilesColored(game, move);
            // Score should value both equal. Not using the product because then one rating being
            // zero leads to the whole score being zero.
            int score = tileRating * tilesColored + tileRating + tilesColored;
            data.add(new Tuple<>(move, score));
        }

        // Sort by score
        data.sort(Comparator.comparingInt(Tuple::second));

        // Reduce
        List<Move> result = new LinkedList<>();
        for (var tuple : data) {
            result.add(tuple.first());
        }

        return result;
    }

    /**
     * Slowest and most accurate Move sorting. Sorts by the full Game evaluation score. Should be
     * used at the beginning of the iterative deepening search.
     */
    public List<Tuple<Move, Game>> sortMoves(Game game, SearchTimer timer)
            throws OutOfTimeException {

        List<Triple<Move, Game, Integer>> data = new LinkedList<>();

        SearchTimer.timePerMove = Integer.MAX_VALUE;
        Timer clock = new Timer();
        int i = 0;

        // Get data
        for (Move move : GameEvaluator.getRelevantMoves(game)) {

            timer.checkTime();

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);

            data.add(new Triple<>(move, clonedGame,
                    evaluate(clonedGame, game.getCurrentPlayerNumber())));

            i++;
            SearchTimer.timePerMove = (int) (clock.timePassed() / i);
            SearchTimer.incrementNodeCount();
        }

        // Sort by evaluation score
        data.sort(Comparator.comparingInt(Triple::third));

        // Reduce
        List<Tuple<Move, Game>> result = new LinkedList<>();
        for (var triple : data) {
            result.add(new Tuple<>(triple.first(), triple.second()));
        }

        return result;
    }

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

    private int getTileRatingForMove(Move move) {
        int y = move.getCoordinates().y;
        int x = move.getCoordinates().x;
        return boardInfo.getTileRatings()[y][x];
    }

    public boolean isSpecialMove(Move move) {
        return move instanceof BonusMove || move instanceof ChoiceMove ||
                move instanceof InversionMove;
    }

    private List<Tuple<Integer, Integer>> getTilesForEachPlayerSortedDescending(Game game) {
        List<Tuple<Integer, Integer>> tileCountsPerPlayer = new LinkedList<>();

        for (int player = 1; player <= game.constants.initialPlayers(); player++) {
            tileCountsPerPlayer.add(new Tuple<>(player,
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(Tile.fromInt(player))
                            .size()));
        }

        // Sort players by number of tiles descending
        tileCountsPerPlayer.sort(
                (entry1, entry2) -> Integer.compare(entry2.second(), entry1.second()));

        return tileCountsPerPlayer;
    }

    public <K, V> int findIndexByKey(List<Tuple<K, V>> entryList, K keyToFind) {
        for (int i = 0; i < entryList.size(); i++) {
            if (entryList.get(i).first().equals(keyToFind)) {
                return i;
            }
        }
        return -1;
    }

    private int getRankingBonus(Game game, int ranking) {
        return -(ranking - game.constants.initialPlayers()) * 1000;
    }

    private int rateTileDifferences(List<Integer> tileDifferences) {
        double result = 0;
        for (int difference : tileDifferences) {
            result += Math.pow(0.95, difference) * 480;
        }
        return (int) result;
    }

    public int getTilesColored(Game game, Move move) {
        Set<Coordinates> tilesColored = new HashSet<>();
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, move.getCoordinates(), direction);
            while (tileReader.hasNext()) {
                tileReader.next();
                if (game.getTile(tileReader.getCoordinates()) ==
                        game.getPlayer(game.getCurrentPlayerNumber()).getPlayerValue()) {
                    break;
                }
                if (!tilesColored.add(tileReader.getCoordinates())) {
                    break;
                }
            }
        }
        return tilesColored.size();
    }
}
