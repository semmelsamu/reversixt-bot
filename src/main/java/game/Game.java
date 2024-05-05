package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import board.TransitionPart;
import evaluation.StaticGameStats;
import util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
     * The number of players this game usually starts with.
     */
    //private final int initialPlayers;

    /**
     * The number of overwrite stones each player has in the beginning.
     */
    //private final int initialOverwriteStones;

    /**
     * The number of bombs each player has in the beginning.
     */
    //private final int initialBombs;

    /**
     * The amount of steps from the center of the explosion a bomb blows tiles up.
     */
    //private final int bombRadius;

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

    public StaticGameStats staticGameStats;

    private int currentPlayer;

    private GamePhase gamePhase;

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
                    new Player(Tile.getAllPlayerTiles()[i], initialOverwriteStones, initialBombs);
        }

        // Create static game stats
        staticGameStats = new StaticGameStats(this, initialPlayers,
                                            initialOverwriteStones, initialBombs, bombRadius);

        gameStats = new GameStats(this);

        currentPlayer = 1;

        gamePhase = GamePhase.PHASE_1;
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
    }

    public void nextPlayer() {
        if (gamePhase == GamePhase.END) {
            logger.error("Cannot calculate next player in end phase");
            return;
        }

        int oldPlayer = currentPlayer;

        do {
            rotateCurrentPlayer();

            if (oldPlayer == currentPlayer &&
                    MoveCalculator.getValidMovesForPlayer(this, currentPlayer).isEmpty()) {
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

        } while (MoveCalculator.getValidMovesForPlayer(this, getCurrentPlayerNumber())
                .isEmpty() || getCurrentPlayer().isDisqualified());

        logger.verbose("Current player is now " + currentPlayer);
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayer);
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int playerNumber) {
        try {
            return players[playerNumber - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.fatal(
                    "Could not get player with number " + playerNumber + ": " + e.getMessage());
            return null;
        }
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

    public Map<TransitionPart, TransitionPart> getTransitions() {
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
        StringBuilder result = new StringBuilder();

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

        // Indent
        String[] lines = result.toString().split("\n");
        return "Game\n\u001B[0m" +
                Arrays.stream(lines).map(line -> "    " + line).collect(Collectors.joining("\n"));
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
