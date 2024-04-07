package game;

import board.*;
import player.Player;
import player.move.Move;
import util.File;
import util.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    /*
    |--------------------------------------------------------------------------
    | Member variables
    |--------------------------------------------------------------------------
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

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    /**
     * Create a new game.
     */
    public Game(int initialPlayers, int initialOverwriteStones, int initialBombs, int bombRadius, Board board) {

        Logger.log("Creating game");

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
            players[i] = new Player(TileValue.getAllPlayerValues()[i], initialOverwriteStones, initialBombs, board.getAllTilesWithValue(TileValue.getAllPlayerValues()[i]));
        }

        // Set first player
        this.currentPlayer = 0;
    }

    /*
    |--------------------------------------------------------------------------
    | Moves
    |--------------------------------------------------------------------------
    */

    public Set<Move> getValidMovesForCurrentPlayer() {
        return getCurrentPlayer().getValidMoves();
    }

    /**
     * Sets the current player to the next player.
     */
    public void nextPlayer(){
        currentPlayer = (currentPlayer + 1) % players.length;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters and Setters
    |--------------------------------------------------------------------------
    */

    public Board getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /*
    |--------------------------------------------------------------------------
    | Util
    |--------------------------------------------------------------------------
    */

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("Initial players: ").append(initialPlayers).append("\n");
        result.append("Initial overwrite stones: ").append(initialOverwriteStones).append("\n");
        result.append("Initial bombs: ").append(initialBombs).append("\n");
        result.append("Bomb radius: ").append(bombRadius).append("\n");

        result.append("Players (Overwrite Stones / Bombs)").append("\n");
        for (Player player : players) {
            result.append("- ").append(player.getPlayerValue().toString()).append(" (").append(player.getOverwriteStones()).append(" / ").append(player.getBombs()).append(")\n");
        }

        result.append(board.toString());

        // Indent
        String[] lines = result.toString().split("\n");
        return "Game\n\u001B[0m" + Arrays.stream(lines).map(line -> "    " + line).collect(Collectors.joining("\n"));
    }
}
