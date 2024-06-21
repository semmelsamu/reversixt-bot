package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import exceptions.GamePhaseNotValidException;
import exceptions.MoveNotValidException;
import move.Move;
import move.OverwriteMove;
import util.Logger;
import util.NullLogger;

import java.util.List;
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
    public GameStats gameStats;

    /**
     * The container for all stats that are valid for the whole game
     */
    // public StaticGameStats staticGameStats;

    public final GameConstants gameConstants;

    private int currentPlayer;

    private int moveCounter;

    private GamePhase gamePhase;

    private Set<Move> validMovesForCurrentPlayer;

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

        gameConstants =
                new GameConstants(initialPlayers, initialOverwriteStones, initialBombs, bombRadius);

        // TODO: Create static game stats
        //        staticGameStats =
        //                new StaticGameStats(this, initialPlayers, initialOverwriteStones,
        //                initialBombs,
        //                        bombRadius);

        gameStats = new GameStats(this);

        gamePhase = GamePhase.BUILD;

        moveCounter = 1;

        currentPlayer = 0;
        do {
            rotateCurrentPlayer();

            // Last player also has no valid moves
            if (currentPlayer == players.length && validMovesForCurrentPlayer.isEmpty()) {

                // No valid moves in build phase
                if (gamePhase == GamePhase.BUILD) {
                    logger.log("No player has valid moves in build phase, entering bomb phase");
                    gamePhase = GamePhase.BOMB;
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
    |   Player logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    private void rotateCurrentPlayer() {
        currentPlayer = (currentPlayer % players.length) + 1;
        validMovesForCurrentPlayer = MoveCalculator.getValidMovesForPlayer(this, currentPlayer);
    }

    public void nextPlayer() {
        if (gamePhase == GamePhase.END) {
            throw new GamePhaseNotValidException("Cannot calculate next player in end phase");
        }

        int oldPlayer = currentPlayer;

        do {
            rotateCurrentPlayer();

            if (oldPlayer == currentPlayer && validMovesForCurrentPlayer.isEmpty()) {
                if (gamePhase == GamePhase.BUILD) {
                    logger.log(
                            "No more player has any moves in the coloring phase, entering bomb " +
                                    "phase");
                    gamePhase = GamePhase.BOMB;
                    rotateCurrentPlayer();
                    oldPlayer = currentPlayer;
                } else if (gamePhase == GamePhase.BOMB) {
                    logger.log("No more player has any bomb moves, entering end");
                    gamePhase = GamePhase.END;
                    // Set player to no player because the game ended
                    currentPlayer = 0;
                    return;
                }
            }

        } while (validMovesForCurrentPlayer.isEmpty() || getCurrentPlayer().isDisqualified());

        logger.debug("Current player is now " + currentPlayer);
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayer);
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer;
    }

    public void disqualifyPlayer(int player) {
        getPlayer(player).disqualify();
        if (getCurrentPlayer().isDisqualified()) {
            nextPlayer();
        }
    }

    public void executeMove(Move move) {
        if (!validMovesForCurrentPlayer.contains(move)) {
            throw new MoveNotValidException("Tried to execute a move that is not valid: " + move);
        }
        MoveExecutor.executeMove(this, move);
        moveCounter++;
        nextPlayer();
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

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

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int playerNumber) {
        return players[playerNumber - 1];
    }

    public int getBombRadius() {
        return gameConstants.bombRadius();
    }

    public Tile getTile(Coordinates position) {
        return board.getTile(position);
    }

    public int getHeight() {
        return board.getHeight();
    }

    public int getWidth() {
        return board.getWidth();
    }

    public void setTile(Coordinates position, Tile value) {
        gameStats.replaceTileAtCoordinates(position, value);
        board.setTile(position, value);
    }

    public Map<Short, Short> getTransitions() {
        return board.getTransitions();
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return board.getAllCoordinatesWhereTileIs(tile);
    }

    public boolean coordinatesLayInBoard(Coordinates position) {
        return board.coordinatesLayInBoard(position);
    }

    public GamePhase getPhase() {
        return gamePhase;
    }

    public int getMoveCounter() {
        return moveCounter;
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

        result.append("Initial players: ").append(gameConstants.initialPlayers()).append("\n");
        result.append("Initial overwrite stones: ").append(gameConstants.initialOverwriteStones())
                .append("\n");
        result.append("Initial bombs: ").append(gameConstants.initialBombs()).append("\n");
        result.append("Bomb radius: ").append(gameConstants.bombRadius()).append("\n");

        result.append("Phase: ").append(gamePhase).append("\n");
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

            clone.gameStats = this.gameStats.clone();
            clone.staticGameStats = this.staticGameStats;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
