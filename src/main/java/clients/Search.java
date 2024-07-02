package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;
import util.Tuple;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Search {

    Logger logger = new Logger(this.getClass().getName());

    /**
     * The game for which to search a new move for.
     */
    private final Game game;

    /**
     * The number of the player to search a new move for.
     */
    private final int playerNumber;

    /**
     * The game evaluator used for this search.
     */
    private final GameEvaluator evaluator;

    private SearchStats stats;

    /**
     * Initialize a new move search.
     * @param game         The game for which to search the best move.
     * @param playerNumber The player for which to search the best move.
     */
    public Search(Game game, int playerNumber, GameEvaluator evaluator) {
        this.game = game;
        this.playerNumber = playerNumber;
        this.evaluator = evaluator;
    }

    /**
     * Start the search.
     * @param timeLimit The maximum time after which a move has to be returned in milliseconds.
     * @return A valid move.
     */
    public Move search(int timeLimit) {

        stats = new SearchStats(timeLimit);

        // Fallback
        Move result = game.getValidMoves().iterator().next();

        try {

            List<Move> preparedMoves = evaluator.prepareMoves(game);
            result = preparedMoves.get(preparedMoves.size() - 1);
            stats.incrementDepthsSearched(0);

            //            Set<Community> relevantCommunities = new HashSet<>();
            //
            //            if (game.communities != null) {
            //                if (!game.getPhase().equals(GamePhase.BUILD)) {
            //                    logger.log("Disabling Communities: Not in build phase");
            //                    game.communities = null;
            //                } else if (game.communities.get().size() < 2) {
            //                    logger.log("Disabling Communities: Not enough communities");
            //                    game.communities = null;
            //                } else if (GameEvaluator.getRelevantMoves(game).stream().anyMatch(
            //                        move -> move instanceof InversionMove || move instanceof
            //                        ChoiceMove)) {
            //                    logger.log("Disabling Communities: Identified special moves");
            //                    game.communities = null;
            //                } else {
            //                    relevantCommunities = game.communities.getRelevant();
            //                    if (relevantCommunities.isEmpty()) {
            //                        logger.warn("Disabling Communities: Didn't find a relevant
            //                        community");
            //                        game.communities = null;
            //                    }
            //                }
            //            } else {
            //                logger.log("Disabling Communities: No Communities");
            //            }
            //
            //            if (game.communities != null) {
            //                logger.log("Searching " + relevantCommunities.size() + " relevant
            //                Communities");
            //            } else {
            //                logger.log("Searching whole game");
            //                game.communities = null;
            //            }

            stats.checkFirstDepth(preparedMoves.size());

            // Iterative deepening search
            int depthLimit = 1;

            do {

                logger.log("Iterative deepening: Depth " + depthLimit);

                stats.reset();

                //                if (game.communities != null) {
                //                    Set<Coordinates> potentialReachableCoordinates = new
                //                    HashSet<>();
                //                    for (Community community : game.communities.get()) {
                //                        potentialReachableCoordinates.addAll(community
                //                        .getCoordinates());
                //                    }
                //                    potentialReachableCoordinates = CoordinatesExpander
                //                    .expandCoordinates(game,
                //                            potentialReachableCoordinates, depthLimit);
                //                    for (Coordinates coordinates :
                //                    potentialReachableCoordinates) {
                //                        if (game.getTile(coordinates).equals(Tile.INVERSION) ||
                //                                game.getTile(coordinates).equals(Tile.CHOICE)) {
                //                            logger.log("Disabling Communities: Potential
                //                            Inversion/Choice Moves");
                //                            game.communities = null;
                //                            break;
                //                        }
                //                    }
                //                }

                //                if (game.communities != null) {
                //                    result = findBestMoveInCommunity(relevantCommunities,
                //                    depthLimit);
                //                } else {
                result = findBestMove(game, sortMoves(game), depthLimit).first();
                //                }

                stats.incrementDepthsSearched(depthLimit);

                stats.checkAbort(depthLimit);

                depthLimit++;

            } while (game.getPhase().equals(GamePhase.BUILD));

            logger.log("Not going deeper as we are not in build phase");

        }
        catch (OutOfTimeException e) {
            logger.warn(e.getMessage());
        }
        catch (NotEnoughTimeException | GamePhaseNotValidException e) {
            logger.log(e.getMessage());
        }

        return result;

    }

    //    private Move findBestMoveInCommunity(Set<Community> relevantCommunities, int depthLimit)
    //            throws OutOfTimeException {
    //
    //        Move result = null;
    //        int score = Integer.MIN_VALUE;
    //
    //        Game game = this.game.clone();
    //
    //        for (Community community : relevantCommunities) {
    //
    //            logger.debug("Calculating best move for Community #" + community.hashCode());
    //
    //            Game clonedGame = game.clone();
    //            clonedGame.communities.simulate(clonedGame.communities.get(community));
    //
    //            Tuple<Move, Integer> communityResult =
    //                    findBestMove(clonedGame, sortMoves(game), depthLimit);
    //
    //            logger.debug("Best Move is " + communityResult.first() + " with a score of " +
    //                    communityResult.second());
    //
    //            if (communityResult.second() > score) {
    //                result = communityResult.first();
    //                score = communityResult.second();
    //            }
    //
    //        }
    //
    //        return result;
    //    }

    /**
     * Initialize the search, and thus begin the building of a search tree. If enough time, the
     * depth of the tree will be `depthLimit`.
     * @return The best move
     * @throws OutOfTimeException if we ran out of time
     */
    private Tuple<Move, Integer> findBestMove(Game game, List<Move> sortedMoves, int depth)
            throws OutOfTimeException {

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (var move : sortedMoves) {

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

            if (score > resultScore) {
                resultScore = score;
                resultMove = move;
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);

            if (depth == 1) {
                stats.firstDepthNodeCount++;
            }
        }

        return new Tuple<>(resultMove, resultScore);
    }

    /**
     * Recursive function for calculating the score of a game situation.
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int calculateScore(Game game, int depth, int alpha, int beta, boolean buildTree)
            throws OutOfTimeException {

        stats.checkTime();

        List<Move> moves = evaluator.prepareMoves(game);

        //        if (this.game.communities != null && game.communities == null) {
        //            logger.warn("Disabling Communities: Detected branch that disabled
        //            Communities");
        //            this.game.communities = null;
        //        }
        //
        //        if (game.communities != null && moves.stream()
        //                .anyMatch(move -> move instanceof InversionMove || move instanceof
        //                ChoiceMove)) {
        //            logger.warn("Disabling Communities: Inversion/Choice moves appeared in tree");
        //            this.game.communities = null;
        //        }

        if (depth == 0 || !game.getPhase().equals(GamePhase.BUILD)) {

            if (!game.getPhase().equals(GamePhase.BUILD)) {
                stats.incrementBombPhasesReached();
            }

            stats.incrementNodeCount();
            return evaluator.evaluate(game, playerNumber);
        }

        boolean isMaximizer = game.getCurrentPlayerNumber() == playerNumber;

        if (isMaximizer) {

            int result = Integer.MIN_VALUE;

            // Maximizer -> Reverse so that good moves are at the beginning
            Collections.reverse(moves);

            for (Move move : moves) {

                Game clonedGame = game.clone();
                clonedGame.executeMove(move);
                int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

                result = Math.max(result, score);

                // Update alpha for the maximizer
                alpha = Math.max(alpha, score);

                // Beta cutoff
                if (beta <= alpha) {
                    evaluator.addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else if (buildTree) {

            // Get Phi Move
            Move phi = moves.get(0);

            Game clonedGame = game.clone();
            clonedGame.executeMove(phi);
            int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

            int result = score;

            beta = Math.min(beta, result);


            for (var move : moves) {

                clonedGame = game.clone();
                clonedGame.executeMove(move);
                score = calculateScore(clonedGame, depth - 1, alpha, beta, false);

                result = Math.min(result, score);

                // Update beta for the minimizer
                beta = Math.min(beta, result);

                // Alpha cutoff
                if (beta <= alpha) {
                    evaluator.addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else {
            Move move = moves.get(0);

            // TODO: Instead of cloning every layer, loop over one cloned game until maximizer?
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            stats.incrementNodeCount();

            return calculateScore(clonedGame, depth - 1, alpha, beta, false);
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * @param game The game.
     */
    private List<Move> sortMoves(Game game) throws OutOfTimeException {

        List<Tuple<Move, Integer>> data = new LinkedList<>();

        // Get data
        for (Move move : GameEvaluator.getRelevantMoves(game)) {
            stats.checkTime();

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);

            data.add(new Tuple<>(move, evaluator.evaluate(clonedGame, playerNumber)));
        }

        stats.checkTime();

        // Sort by evaluation score
        data.sort(Comparator.comparingInt(Tuple::second));

        // Reduce
        List<Move> result = new LinkedList<>();
        for (var tuple : data) {
            result.add(tuple.first());
        }

        return result;
    }
}
