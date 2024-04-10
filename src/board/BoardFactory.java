package board;

import util.Logger;

import java.util.LinkedList;
import java.util.List;

public class BoardFactory {

    /**
     * Creates a board from lines.
     *
     * @return The board.
     */
    public static Board createFromLines(LinkedList<String> lines) {

        Logger.get().log("Creating board from lines");

        // Parsing dimensions
        String[] dimensions = lines.remove(0).split(" ");
        int height = Integer.parseInt(dimensions[0]);
        int width = Integer.parseInt(dimensions[1]);
        Logger.get().verbose("Dimensions: Height " + height + "; Width " + width);

        // Parsing map
        char[][] map = parseMap(lines, height, width);

        // Parsing transitions
        int[][] transitions = parseTransitions(lines);

        // Creating board
        return new Board(map, transitions);
    }

    /*
    |--------------------------------------------------------------------------
    | Factory utils
    |--------------------------------------------------------------------------
    */

    private static char[][] parseMap(List<String> lines, int height, int width) {

        Logger.get().log("Parsing map");

        char[][] map = new char[height][width];

        for (int y = 0; y < height; y++) {
            String currentLine = lines.remove(0);
            Logger.get().verbose(currentLine);
            String[] currentRows = currentLine.split((" "));
            for (int x = 0; x < width; x++) {
                map[y][x] = currentRows[x].charAt(0);
            }
        }

        return map;
    }

    private static int[][] parseTransitions(LinkedList<String> lines) {

        Logger.get().log("Parsing transitions");

        int[][] transitions = new int[lines.size()][6];

        for (int i = 0; i < lines.size(); i++) {

            String currentLine = lines.get(i);

            Logger.get().verbose(currentLine);
            String[] transitionParts = currentLine.split(" ");
            transitions[i] = new int[]{
                    Integer.parseInt(transitionParts[0]),
                    Integer.parseInt(transitionParts[1]),
                    Integer.parseInt(transitionParts[2]),
                    Integer.parseInt(transitionParts[4]),
                    Integer.parseInt(transitionParts[5]),
                    Integer.parseInt(transitionParts[6]),
            };
        }

        return transitions;
    }

}
