package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import evaluation.StaticGameStats;
import exceptions.GamePhaseNotValidException;
import exceptions.MoveNotValidException;
import move.Move;
import util.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public StaticGameStats staticGameStats;

    private int currentPlayer;

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
            players[i] =
                    new Player(Tile.getTileForPlayerNumber(i + 1), initialOverwriteStones, initialBombs);
        }

        // Create static game stats
        staticGameStats =
                new StaticGameStats(this, initialPlayers, initialOverwriteStones, initialBombs,
                        bombRadius);

        gameStats = new GameStats(this);

        gamePhase = GamePhase.PHASE_1;

        currentPlayer = 1;
        validMovesForCurrentPlayer = MoveCalculator.getValidMovesForPlayer(this, currentPlayer);
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
                if (gamePhase == GamePhase.PHASE_1) {
                    logger.log(
                            "No more player has any moves in the coloring phase, entering bomb " +
                                    "phase");
                    gamePhase = GamePhase.PHASE_2;
                    rotateCurrentPlayer();
                    oldPlayer = currentPlayer;
                } else if (gamePhase == GamePhase.PHASE_2) {
                    logger.log("No more player has any bomb moves, entering end");
                    gamePhase = GamePhase.END;
                    // Set player to no player because the game ended
                    currentPlayer = 0;
                    return;
                }
            }

        } while (validMovesForCurrentPlayer.isEmpty() || getCurrentPlayer().isDisqualified());

        logger.verbose("Current player is now " + currentPlayer);
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
        if(!validMovesForCurrentPlayer.contains(move)) {
            logger.error(this.toString());
            logger.error(move.toString());
            throw new MoveNotValidException("Tried to execute a move that is not valid");
        }
        MoveExecutor.executeMove(this, move);
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

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int playerNumber) {
        return players[playerNumber - 1];
    }

    public int getBombRadius() {
        return staticGameStats.getBombRadius();
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

        result.append("Initial players: ").append(staticGameStats.getInitialPlayers()).append("\n");
        result.append("Initial overwrite stones: ")
                .append(staticGameStats.getInitialOverwriteStones()).append("\n");
        result.append("Initial bombs: ").append(staticGameStats.getInitialBombs()).append("\n");
        result.append("Bomb radius: ").append(staticGameStats.getBombRadius()).append("\n");

        result.append("Phase: ").append(gamePhase).append("\n");

        result.append("Players (Overwrite Stones / Bombs)").append("\n");
        for (Player player : players) {
            result.append("- ").append(player.getPlayerValue().toString()).append(" (")
                    .append(player.getOverwriteStones()).append(" / ").append(player.getBombs())
                    .append(")");
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

            clone.gameStats = this.gameStats.clone();
            clone.staticGameStats = this.staticGameStats;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
