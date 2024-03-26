package game;

import map.Board;
import util.File;
import util.Logger;

import java.util.List;

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

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    /**
     * Create a new game.
     */
    public Game(int initialPlayers, int initialOverwriteStones, int initialBombs, int bombRadius, Board board) {
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        this.board = board;
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
    private static Game createFromLines(List<String> lines) {

        Logger.log("Creating game from lines");

        // Splitting game and board lines
        List<String> gameLines = lines.subList(0, 3);
        List<String> boardLines = lines.subList(3, lines.size());

        // Parsing initial player data
        int initialPlayers = Integer.parseInt(gameLines.get(0));
        Logger.debug("Initial players: " + initialPlayers);
        int initialOverwriteStones = Integer.parseInt(gameLines.get(1));
        Logger.debug("Initial overwrite stones: " + initialOverwriteStones);

        // Parsing initial bomb data
        String[] bombs = gameLines.get(2).split((" "));
        int initialBombs = Integer.parseInt(bombs[0]);
        Logger.debug("Initial bombs: " + initialBombs);
        int bombRadius = Integer.parseInt(bombs[1]);
        Logger.debug("Bomb radius: " + bombRadius);

        // Creating board
        Board board = Board.createFromLines(boardLines);

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

        return createFromLines(List.of(lines));
    }

    public static Game createFromFile(String filename) {

        Logger.log("Creating game from file " + filename);
        return createFromString(File.readFile(filename));
    }
}
