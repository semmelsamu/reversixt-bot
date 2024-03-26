package map;

import util.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * This class only stores information about what is currently on the game board, not the state of the game.
 */
public class Board {

    /**
     * The game board.
     * First dimension is the lines (y-direction), second one is columns/rows (x-direction).
     * Starts at the top left with (0/0).
     */
    private Tile[][] tiles;

    public Board(char[][] map, int[][] transitions) {

        Logger.log("Creating board");

        this.tiles = new Tile[map.length][map[0].length];

        // Build map

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                this.tiles[y][x] = new Tile(
                        TileType.fromChar(map[y][x]),
                        new Coordinates(x, y)
                );
            }
        }

        // Register transitions

        for (int[] transition : transitions) {
            // Transitions must be registered in both ways.
            int x1 = transition[0];
            int y1 = transition[1];
            Direction d1 = Direction.fromValue(transition[2]);
            int x2 = transition[3];
            int y2 = transition[4];
            Direction d2 = Direction.fromValue(transition[5]);

            Tile tile1 = this.tiles[y1][x1];
            Tile tile2 = this.tiles[y2][x2];

            tile1.addTransition(new Transition(d1, tile2, d2));
            tile2.addTransition(new Transition(d2, tile1, d1));

        }

        this.print();
    }

    public void print() {
        for (Tile[] row : tiles) {
            for (Tile column : row) {
                System.out.print(column.getType().print());
            }
            System.out.println();
        }
    }

    /*
    |--------------------------------------------------------------------------
    | Factories
    |--------------------------------------------------------------------------
    */

    /**
     * Creates a board lines
     * @return The board.
     */
    public static Board createFromLines(LinkedList<String> lines) {

        Logger.log("Creating board from lines");

        // Parsing dimensions
        String[] dimensions = lines.remove(0).split(" ");
        int height = Integer.parseInt(dimensions[0]);
        int width = Integer.parseInt(dimensions[1]);
        Logger.debug("Dimensions: Height " + height + "; Width " + width);

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

        Logger.log("Parsing map");

        char[][] map = new char[height][width];

        for (int y = 0; y < height; y++) {
            String currentLine = lines.remove(0);
            Logger.debug(currentLine);
            String[] currentRows = currentLine.split((" "));
            for (int x = 0; x < width; x++) {
                map[y][x] = currentRows[x].charAt(0);
            }
        }

        return map;
    }

    private static int[][] parseTransitions(LinkedList<String> lines) {

        Logger.log("Parsing transitions");

        int[][] transitions = new int[lines.size()][6];

        for(int i = 0; i < lines.size(); i++) {

            String currentLine = lines.remove(0);

            Logger.debug(currentLine);
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
