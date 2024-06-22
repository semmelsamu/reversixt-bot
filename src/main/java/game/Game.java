package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import exceptions.GamePhaseNotValidException;
import exceptions.MoveNotValidException;
import move.Move;
import move.OverwriteMove;
import stats.CoordinatesGroupedByTile;
import stats.TotalTilesOccupiedCounter;
import util.Logger;
import util.NullLogger;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game implements Cloneable {

    Logger logger = new Logger(this.getClass().getName());

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The actual game board.
     */
    private Board board;

    /**
     * Array containing all players with their information
     */
    private Player[] players;

    /**
     * The container for all stats about the game and the logic
     */
    public GameStats stats;

    public final GameConstants constants;

    private int currentPlayer;

    private int moveCounter;

    private GamePhase phase;

    private Set<Move> validMovesForCurrentPlayer;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Caches
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Caches the total number of tiles occupied.
     */
    public TotalTilesOccupiedCounter totalTilesOccupiedCounter;

    /**
     * For each tile value, caches the set of coordinates that have this value.
     */
    public CoordinatesGroupedByTile coordinatesGroupedByTile;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Create a new game.
     */
    public Game(int initialPlayers, int initialOverwriteStones, int initialBombs, int bombRadius,
                Board board) {

        logger.verbose("Creating game");

        // Set board
        this.board = board;

        // Initialize players
        players = new Player[initialPlayers];
        for (int i = 0; i < initialPlayers; i++) {
            players[i] = new Player(Tile.getTileForPlayerNumber(i + 1), initialOverwriteStones,
                    initialBombs);
        }

        constants =
                new GameConstants(initialPlayers, initialOverwriteStones, initialBombs, bombRadius);

        // Caching
        coordinatesGroupedByTile = new CoordinatesGroupedByTile(this);
        stats = new GameStats(this);
        totalTilesOccupiedCounter = new TotalTilesOccupiedCounter(this);

        phase = GamePhase.BUILD;

        moveCounter = 1;

        currentPlayer = 0;
        do {
            rotateCurrentPlayer();

            // Last player also has no valid moves
            if (currentPlayer == players.length && validMovesForCurrentPlayer.isEmpty()) {

                // No valid moves in build phase
                if (phase == GamePhase.BUILD) {
                    logger.log("No player has valid moves in build phase, entering bomb phase");
                    phase = GamePhase.BOMB;
                    // Set player again to first player
                    rotateCurrentPlayer();
                }

                // Also no valid moves in bomb phase. weird.
                else {
                    logger.warn("Map is not playable as there are no valid moves in any phase");
                    break;
                }
            }
        } while (validMovesForCurrentPlayer.isEmpty());
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Players
    |
    |-----------------------------------------------------------------------------------------------
    */

    private void rotateCurrentPlayer() {
        currentPlayer = (currentPlayer % players.length) + 1;
        validMovesForCurrentPlayer = MoveCalculator.getValidMovesForPlayer(this, currentPlayer);
    }

    public void nextPlayer() {
        if (phase == GamePhase.END) {
            throw new GamePhaseNotValidException("Cannot calculate next player in end phase");
        }

        int oldPlayer = currentPlayer;

        do {
            rotateCurrentPlayer();

            if (oldPlayer == currentPlayer && validMovesForCurrentPlayer.isEmpty()) {
                if (phase == GamePhase.BUILD) {
                    logger.log(
                            "No more player has any moves in the coloring phase, entering bomb " +
                                    "phase");
                    phase = GamePhase.BOMB;
                    rotateCurrentPlayer();
                    oldPlayer = currentPlayer;
                } else if (phase == GamePhase.BOMB) {
                    logger.log("No more player has any bomb moves, entering end");
                    phase = GamePhase.END;
                    // Set player to no player because the game ended
                    currentPlayer = 0;
                    return;
                }
            }

        } while (validMovesForCurrentPlayer.isEmpty() || getCurrentPlayer().isDisqualified());

        logger.debug("Current player is now " + currentPlayer);
    }

    public void disqualifyPlayer(int player) {
        getPlayer(player).disqualify();
        if (getCurrentPlayer().isDisqualified()) {
            nextPlayer();
        }
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayer);
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int playerNumber) {
        return players[playerNumber - 1];
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Moves
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void executeMove(Move move) {
        if (!validMovesForCurrentPlayer.contains(move)) {
            throw new MoveNotValidException("Tried to execute a move that is not valid: " + move);
        }
        MoveExecutor.executeMove(this, move);
        moveCounter++;
        nextPlayer();
    }

    public Set<Move> getValidMovesForCurrentPlayer() {
        return validMovesForCurrentPlayer;
    }

    // TODO: Refactor
    // TODO: What if we only have one non-overwrite move which gets us in a really bad situation,
    //       but we could use an overwrite move which would help us A LOT?
    public Set<Move> getRelevantMovesForCurrentPlayer() {
        Set<Move> movesWithoutOverwrites = validMovesForCurrentPlayer.stream()
                .filter((move) -> !(move instanceof OverwriteMove)).collect(Collectors.toSet());

        if (movesWithoutOverwrites.isEmpty()) {
            return validMovesForCurrentPlayer;
        } else {
            return movesWithoutOverwrites;
        }
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Board
    |
    |-----------------------------------------------------------------------------------------------
    */

    public int getHeight() {
        return board.getHeight();
    }

    public int getWidth() {
        return board.getWidth();
    }

    public Tile getTile(Coordinates position) {
        return board.getTile(position);
    }

    public void setTile(Coordinates position, Tile value) {
        coordinatesGroupedByTile.updateCoordinates(position, board.getTile(position), value);
        board.setTile(position, value);
    }

    public Map<Short, Short> getTransitions() {
        return board.getTransitions();
    }

    public boolean coordinatesLayInBoard(Coordinates position) {
        return board.coordinatesLayInBoard(position);
    }

    public GamePhase getPhase() {
        return phase;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   To string
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Game\n");

        result.append("Initial players: ").append(constants.initialPlayers()).append("\n");
        result.append("Initial overwrite stones: ").append(constants.initialOverwriteStones())
                .append("\n");
        result.append("Initial bombs: ").append(constants.initialBombs()).append("\n");
        result.append("Bomb radius: ").append(constants.bombRadius()).append("\n");

        result.append("Phase: ").append(phase).append("\n");
        result.append("Move: ").append(moveCounter).append("\n");

        result.append("Players (Overwrite Stones / Bombs)").append("\n");
        for (Player player : players) {
            result.append("- ").append(player.getPlayerValue().toString()).append(" (")
                    .append(player.getOverwriteStones()).append(" / ").append(player.getBombs())
                    .append(")");
            if (player.isDisqualified()) {
                result.append(" X");
            }
            if (player.getPlayerValue().toPlayerIndex() + 1 == currentPlayer) {
                result.append(" *");
            }
            result.append("\n");
        }

        result.append(board.toString());

        return result.toString();
    }

    @Override
    public Game clone() {
        try {
            Game clone = (Game) super.clone();

            clone.board = this.board.clone();

            clone.players = new Player[this.players.length];
            for (int i = 0; i < this.players.length; i++) {
                clone.players[i] = this.players[i].clone();
            }

            if (!(clone.logger instanceof NullLogger)) {
                clone.logger = new NullLogger("");
            }

            clone.stats = this.stats.clone();
            clone.coordinatesGroupedByTile =
                    (CoordinatesGroupedByTile) this.coordinatesGroupedByTile.clone();

            clone.totalTilesOccupiedCounter =
                    (TotalTilesOccupiedCounter) this.totalTilesOccupiedCounter.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
