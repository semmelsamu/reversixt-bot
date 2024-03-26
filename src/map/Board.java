package map;

import util.Logger;

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

    public Board(char[][] tiles, int[][] transitions) {

        this.tiles = new Tile[tiles.length][tiles[0].length];

        // Build map

        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                this.tiles[y][x] = new Tile(
                        TileType.fromChar(tiles[y][x]),
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

        Logger.log("Constructed map");
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

    /**
     * Constructs a game board from a string.
     * @return The game board.
     */
    public static Board constructBoardFromLines(String[] boardLines) {
        Logger.log("Attempting to parse map from string");

        for (int i = 0; i < boardLines.length; i++) {
            // Format string
            boardLines[i] = boardLines[i].trim().toLowerCase();
        }

        try {;

            Logger.debug(boardLines[0]);
            String[] line4Split = boardLines[0].split(" ");
            int height = Integer.parseInt(line4Split[0]);
            int width = Integer.parseInt(line4Split[1]);

            char[][] map = new char[height][width];
            for (int y = 1; y < height; y++) {
                Logger.debug(boardLines[y]);
                String[] currentRow = boardLines[y].split((" "));
                for (int x = 0; x < width; x++) {
                    map[y][x] = currentRow[x].charAt(0);
                }
            }

//            int[][] transitions = new int[boardLines.length - height - 1][6];
//            int currentTransition = 0;
//
//            // TODO: ugly
//            for (int i = 4 + height; i < boardLines.length; i++) {
//                String transitionString = boardLines[i];
//                Logger.verbose(transitionString);
//                String[] transitionParts = transitionString.split(" ");
//                transitions[currentTransition] = new int[]{
//                        Integer.parseInt(transitionParts[0]),
//                        Integer.parseInt(transitionParts[1]),
//                        Integer.parseInt(transitionParts[2]),
//                        Integer.parseInt(transitionParts[4]),
//                        Integer.parseInt(transitionParts[5]),
//                        Integer.parseInt(transitionParts[6]),
//                };
//                currentTransition++;
//            }

            return null;

        } catch (Exception e) {
            Logger.fatal("Error parsing map string: " + e.getMessage());
            return null;
        }
    }
}
