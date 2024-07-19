package game;

import board.Board;
import board.BoardFactory;
import util.File;
import util.Logger;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Responsible for creating a {@link Game}
 */
public class GameFactory {

    static Logger logger = new Logger(GameFactory.class.getName());

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Factories
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Creates a new game from input lines.
     *
     * @param lines A list of lines, where each line is trimmed and all lowercase (normalized).
     * @return The new game.
     */
    private static Game createFromLines(LinkedList<String> lines) {

        logger.verbose("Creating game from lines");

        // We unshift line for line and parse it

        // Parsing initial player data
        int initialPlayers = Integer.parseInt(lines.remove(0));
        logger.debug("Initial players: " + initialPlayers);
        int initialOverwriteStones = Integer.parseInt(lines.remove(0));
        logger.debug("Initial overwrite stones: " + initialOverwriteStones);

        // Parsing initial bomb data
        String[] bombs = lines.remove(0).split((" "));
        int initialBombs = Integer.parseInt(bombs[0]);
        logger.debug("Initial bombs: " + initialBombs);
        int bombRadius = Integer.parseInt(bombs[1]);
        logger.debug("Bomb radius: " + bombRadius);

        // Create board from remaining lines
        Board board = BoardFactory.createFromLines(lines);

        // Creating game
        return new Game(initialPlayers, initialOverwriteStones, initialBombs, bombRadius, board);
    }

    public static Game createFromString(String string) {

        logger.verbose("Creating game from string");

        // Mind lines can be separated by nl or cr+nl
        String[] lines = string.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            // Normalise each line
            lines[i] = lines[i].trim().toLowerCase();
        }

        return createFromLines(new LinkedList<>(Arrays.asList(lines)));
    }

    public static Game createFromFile(String filename) {

        logger.verbose("Creating game from file " + filename);
        return createFromString(File.readFile(filename));
    }

}
