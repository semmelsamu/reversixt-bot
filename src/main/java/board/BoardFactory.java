package board;

import util.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BoardFactory {

    static Logger logger = new Logger(BoardFactory.class.getName());

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Factories
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Creates a board from lines.
     *
     * @return The board.
     */
    public static Board createFromLines(LinkedList<String> lines) {

        logger.log("Creating board from lines");

        // Parsing dimensions
        String[] dimensions = lines.remove(0).split(" ");
        int height = Integer.parseInt(dimensions[0]);
        int width = Integer.parseInt(dimensions[1]);
        logger.verbose("Dimensions: Height " + height + "; Width " + width);

        // Parsing
        char[][] rawTiles = parseMap(lines, height, width);
        int[][] rawTransitions = parseTransitions(lines);

        // Converting to concrete data structures
        var tiles = arrayToTiles(rawTiles);
        var transitions = arrayToTransitions(rawTransitions);

        // Creating board
        return new Board(tiles, transitions);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Util lines to array
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static char[][] parseMap(List<String> lines, int height, int width) {

        logger.log("Parsing map");

        char[][] map = new char[height][width];

        for (int y = 0; y < height; y++) {
            String currentLine = lines.remove(0);
            logger.verbose(currentLine);
            String[] currentRows = currentLine.split((" "));
            for (int x = 0; x < width; x++) {
                map[y][x] = currentRows[x].charAt(0);
            }
        }

        return map;
    }

    private static int[][] parseTransitions(LinkedList<String> lines) {

        logger.log("Parsing transitions");

        int[][] transitions = new int[lines.size()][6];

        for (int i = 0; i < lines.size(); i++) {

            String currentLine = lines.get(i);

            logger.verbose(currentLine);
            String[] transitionParts = currentLine.split(" ");
            transitions[i] = new int[]{
                    Integer.parseInt(transitionParts[0]), Integer.parseInt(transitionParts[1]),
                    Integer.parseInt(transitionParts[2]), Integer.parseInt(transitionParts[4]),
                    Integer.parseInt(transitionParts[5]), Integer.parseInt(transitionParts[6]),
            };
        }

        return transitions;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Util array to data structure
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static Tile[][] arrayToTiles(char[][] array) {

        // Get dimensions
        int height = array.length;
        int width = array[0].length;

        // Create buffer
        Tile[][] result = new Tile[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = Tile.fromChar(array[y][x]);
            }
        }

        return result;
    }

    private static Map<TransitionPart, TransitionPart> arrayToTransitions(int[][] array) {

        // Create buffer
        Map<TransitionPart, TransitionPart> result = new HashMap<>();

        // Register transitions
        for (int[] transition : array) {

            int x1 = transition[0];
            int y1 = transition[1];
            Coordinates coordinates1 = new Coordinates(x1, y1);
            Direction d1Out = Direction.fromValue(transition[2]);
            Direction d1In = Direction.fromValue((d1Out.getValue() + 4) % 8);

            int x2 = transition[3];
            int y2 = transition[4];
            Coordinates coordinates2 = new Coordinates(x2, y2);
            Direction d2Out = Direction.fromValue(transition[5]);
            Direction d2In = Direction.fromValue((d2Out.getValue() + 4) % 8);

            // Transitions must be registered in both ways

            result.put(new TransitionPart(coordinates1, d1Out),
                    new TransitionPart(coordinates2, d2In));

            result.putIfAbsent(new TransitionPart(coordinates2, d2Out),
                    new TransitionPart(coordinates1, d1In));
        }

        return result;
    }

}
