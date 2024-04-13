package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import board.TransitionPart;
import player.Player;
import player.move.Move;
import util.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game {

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
    private final Board board;

    /**
     * Array containing all players with their information
     */
    private Player[] players;

    /**
     * The number of the player whose turn it is.
     * You are probably looking for {@link #getCurrentPlayer()}
     */
    private int currentPlayer;

    /**
     * The container for all stats about the game and the logic
     */
    public GameStats gameStats;

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

        Logger.get().log("Creating game");

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

        // Set first player
        this.currentPlayer = 0;

        gameStats = new GameStats(this);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Moves
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Set<Move> getValidMovesForCurrentPlayer() {
        return new MoveCalculator(this).getValidMoves();
    }

    /**
     * Sets the current player to the next player.
     */
    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.length;
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

    public Tile[] getAllParticipatingPlayers(){
        return Arrays.copyOfRange(Tile.getAllPlayerTiles(),0, initialPlayers - 1);
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }

    public Tile getTile(Coordinates position) {
        return board.getTile(position);
    }

    public Map<TransitionPart, TransitionPart> getTransitions() {
        return board.getTransitions();
    }

    public GameStats getGameStats() {
        return gameStats;
    }

    public void setTile(Coordinates position, Tile value) {
        gameStats.replaceTileAtCoordinates(position, value);
        board.setTile(position, value);
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return board.getAllCoordinatesWhereTileIs(tile);
    }

    public boolean hasPlayerOverwriteStones(Tile playerTile){
        Player player = players[playerTile.toPlayerIndex()];
        return player.getOverwriteStones() > 0;
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
}
