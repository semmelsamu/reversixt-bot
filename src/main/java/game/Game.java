package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import board.TransitionPart;
import network.MoveAnswer;
import player.Player;
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
    private final int initialPlayers;

    /**
     * The number of overwrite stones each player has in the beginning.
     */
    private final int initialOverwriteStones;

    /**
     * The number of bombs each player has in the beginning.
     */
    private final int initialBombs;

    /**
     * The amount of steps from the center of the explosion a bomb blows tiles up.
     */
    private final int bombRadius;

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

    private int currentPlayer;

    public GamePhase gamePhase;

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

        logger.log("Creating game");

        // Store initial information
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;

        // Set board
        this.board = board;

        // Initialize players
        players = new Player[initialPlayers];
        for (int i = 0; i < initialPlayers; i++) {
            players[i] =
                    new Player(Tile.getAllPlayerTiles()[i], initialOverwriteStones, initialBombs);
        }

        gameStats = new GameStats(this);

        currentPlayer = 1;

        gamePhase = GamePhase.PHASE_1;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Current player logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void nextPlayer() {
        int oldPlayer = currentPlayer;
        do {
            currentPlayer = (currentPlayer + 1) % players.length;
            if (oldPlayer == currentPlayer) {
                logger.log("No more player has any moves");
            }
        } while (!(new MoveCalculator(this)).getValidMovesForPlayer(getCurrentPlayer()).isEmpty() ||
                oldPlayer == currentPlayer);
    }

    public Player getCurrentPlayer() {
        return getPlayer(currentPlayer);
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
        return players[playerNumber - 1];
    }

    public int getBombRadius() {
        return bombRadius;
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

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
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

        result.append("Initial players: ").append(initialPlayers).append("\n");
        result.append("Initial overwrite stones: ").append(initialOverwriteStones).append("\n");
        result.append("Initial bombs: ").append(initialBombs).append("\n");
        result.append("Bomb radius: ").append(bombRadius).append("\n");

        result.append("Players (Overwrite Stones / Bombs)").append("\n");
        for (Player player : players) {
            result.append("- ").append(player.getPlayerValue().toString()).append(" (")
                    .append(player.getOverwriteStones()).append(" / ").append(player.getBombs())
                    .append(")\n");
        }

        result.append(board.toString()).append("\n");
        result.append(gameStats);

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
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
