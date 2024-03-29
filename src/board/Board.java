package board;

import player.Player;
import util.Logger;

import java.util.Arrays;
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

    /**
     * The dimensions of the game board. Used for quick access.
     */
    public final int width;
    public final int height;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    /**
     * Creates a game board from a map array and a transition array.
     */
    public Board(char[][] map, int[][] transitions) {

        Logger.log("Creating board");

        // Get dimensions
        height = map.length;
        width = map[0].length;
        this.tiles = new Tile[height][width];

        // Build map
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.tiles[y][x] = new Tile(
                        TileValue.fromChar(map[y][x]),
                        new Coordinates(x, y)
                );
            }
        }

        // Set neighbours
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Tile currentTile = tiles[y][x];
                if(y > 0) currentTile.setNeighbour(Direction.NORTH, new Neighbour(tiles[y-1][x]));
                if(y > 0 && x < width-1) currentTile.setNeighbour(Direction.NORTHEAST, new Neighbour(tiles[y-1][x+1]));
                if(x < width-1) currentTile.setNeighbour(Direction.EAST, new Neighbour(tiles[y][x+1]));
                if(y < height-1 && x < width-1) currentTile.setNeighbour(Direction.SOUTHEAST, new Neighbour(tiles[y+1][x+1]));
                if(y < height-1) currentTile.setNeighbour(Direction.SOUTH, new Neighbour(tiles[y+1][x]));
                if(y < height-1 && x > 0) currentTile.setNeighbour(Direction.SOUTHWEST, new Neighbour(tiles[y+1][x-1]));
                if(x > 0) currentTile.setNeighbour(Direction.WEST, new Neighbour(tiles[y][x-1]));
                if(y > 0 && x > 0) currentTile.setNeighbour(Direction.NORTHWEST, new Neighbour(tiles[y-1][x-1]));
            }
        }

        // Register transitions
        for (int[] transition : transitions) {

            // Transitions must be registered in both ways

            int x1 = transition[0];
            int y1 = transition[1];
            Direction d1Out = Direction.fromValue(transition[2]);
            Direction d1In = Direction.fromValue((d1Out.getValue() + 4) % 8);

            int x2 = transition[3];
            int y2 = transition[4];
            Direction d2Out = Direction.fromValue(transition[5]);
            Direction d2In = Direction.fromValue((d2Out.getValue() + 4) % 8);

            Tile tile1 = this.tiles[y1][x1];
            Tile tile2 = this.tiles[y2][x2];

            tile1.setNeighbour(d1Out, new Neighbour(tile2, d2In));
            tile2.setNeighbour(d2Out, new Neighbour(tile1, d1In));
        }

    }

    /*
    |--------------------------------------------------------------------------
    | Getters and setters
    |--------------------------------------------------------------------------
    */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(Coordinates position) {
        return this.tiles[position.y][position.x];
    }

    public List<Tile> getAllTiles() {
        List<Tile> result = new LinkedList<>();

        for(Tile[] tileRow : tiles) {
            // Iteration could be replaced with bulk 'Collection.addAll()' call
            result.addAll(Arrays.asList(tileRow));
        }

        return result;
    }

    public List<Tile> getAllOccupiedTilesForPlayer(Player player) {

        List<Tile> allTiles = getAllTiles();
        List<Tile> result = new LinkedList<>();

        for(Tile tile : allTiles) {
            if(tile.getValue() == player.getPlayerValue())
                result.add(tile);
        }

        return result;
    }

    /*
    |--------------------------------------------------------------------------
    | Utility
    |--------------------------------------------------------------------------
    */

    /**
     * Prints the color map to the system out.
     */
    public void print() {
        for (Tile[] row : tiles) {
            for (Tile column : row) {
                System.out.print(column.getValue().print());
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
     * Creates a board from lines.
     * @return The board.
     */
    public static Board createFromLines(LinkedList<String> lines) {

        Logger.log("Creating board from lines");

        // Parsing dimensions
        String[] dimensions = lines.remove(0).split(" ");
        int height = Integer.parseInt(dimensions[0]);
        int width = Integer.parseInt(dimensions[1]);
        Logger.verbose("Dimensions: Height " + height + "; Width " + width);

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
            Logger.verbose(currentLine);
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

            String currentLine = lines.get(i);

            Logger.verbose(currentLine);
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
