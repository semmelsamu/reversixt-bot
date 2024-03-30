package game;

import board.*;
import player.Player;
import util.File;
import util.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    private Board board;

    /**
     * Array containing all players with their information
     */
    private Player[] players;

    /**
     * The player whose turn it is
     */
    private Player currentPlayer;
    private TileValue[] playerValues;

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
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        this.board = board;

        // Add players
        players = new Player[initialPlayers];
        TileValue[] playerValues = TileValue.getAllPlayerValues();
        for (int i = 0; i < initialPlayers; i++) {
            players[i] = new Player(playerValues[i], initialOverwriteStones, initialBombs,
                                    board.getAllTilesWithValue(playerValues[i]));
        }
        currentPlayer = players[0];
    }

    /*
    |--------------------------------------------------------------------------
    | Factories
    |--------------------------------------------------------------------------
    */

    /**
     * Creates a new game from input lines.
     * @param lines A list of lines, where each line is trimmed and all lowercase (normalized).
     * @return The new game.
     */
    private static Game createFromLines(LinkedList<String> lines) {

        Logger.log("Creating game from lines");

        // We unshift line for line and parse it

        // Parsing initial player data
        int initialPlayers = Integer.parseInt(lines.remove(0));
        Logger.verbose("Initial players: " + initialPlayers);
        int initialOverwriteStones = Integer.parseInt(lines.remove(0));
        Logger.verbose("Initial overwrite stones: " + initialOverwriteStones);

        // Parsing initial bomb data
        String[] bombs = lines.remove(0).split((" "));
        int initialBombs = Integer.parseInt(bombs[0]);
        Logger.verbose("Initial bombs: " + initialBombs);
        int bombRadius = Integer.parseInt(bombs[1]);
        Logger.verbose("Bomb radius: " + bombRadius);

        // Create board from remaining lines
        Board board = Board.createFromLines(lines);


        // Creating game
        return new Game(initialPlayers, initialOverwriteStones, initialBombs, bombRadius, board);
    }

    private static Game createFromString(String string) {

        Logger.log("Creating game from string");

        // Mind lines can be separated by nl or cr+nl
        String[] lines = string.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            // Normalise each line
            lines[i] = lines[i].trim().toLowerCase();
        }

        return createFromLines(new LinkedList<>(Arrays.asList(lines)));
    }

    public static Game createFromFile(String filename) {

        Logger.log("Creating game from file " + filename);
        return createFromString(File.readFile(filename));
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public List<Coordinates> getValidMovesForCurrentPlayer(){
        return currentPlayer.getValidMoves();
    }

    public Board getBoard() {
        return board;
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
        for(Player player: players) {
            result.append("-").
                    append(player.getPlayerValue().toString()).
                    append("(").append(player.getOverwriteStones()).
                    append(" / ").append(player.getBombs()).
                    append(")\n");
        }

        result.append(board.toString());

        // Indent
        String[] lines = result.toString().split("\n");
        return "Game\n\u001B[0m" + Arrays.stream(lines)
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));
    }
}
