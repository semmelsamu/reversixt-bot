package map;

import util.Logger;

import static util.File.readFile;

public class Board {

    /**
     * The "game board". First dimension is the lines, second one is columns/rows.
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

    public static Board constructFromString(String string) {
        Logger.log("Attempting to parse map from string");

        // Convert to lines
        String[] lines = string.split("\\r?\\n"); // Mind lines can be separated by nl or cr+nl
        for (int i = 0; i < lines.length; i++) {
            // Format string
            lines[i] = lines[i].trim().toLowerCase();
        }

        try {
            Logger.debug(lines[0]);
            int initialPlayers = Integer.parseInt(lines[0]);

            Logger.debug(lines[1]);
            int initialOverwriteStones = Integer.parseInt(lines[1]);

            Logger.debug(lines[2]);
            String[] line3Split = lines[2].split((" "));
            int initialBombs = Integer.parseInt(line3Split[0]);
            int bombRadius = Integer.parseInt(line3Split[1]);

            Logger.debug(lines[3]);
            String[] line4Split = lines[3].split(" ");
            int height = Integer.parseInt(line4Split[0]);
            int width = Integer.parseInt(line4Split[1]);

            char[][] map = new char[height][width];
            for (int y = 0; y < height; y++) {
                // Lines 0-3 are initialPlayer, initialOverwriteStones..., so we start at line 4
                Logger.debug(lines[y + 4]);
                String[] currentRow = lines[y + 4].split((" "));
                for (int x = 0; x < width; x++) {
                    map[y][x] = currentRow[x].charAt(0);
                }
            }

            int[][] transitions = new int[lines.length - 4 - height][6];
            int currentTransition = 0;

            for (int i = 4 + height; i < lines.length; i++) {
                String transitionString = lines[i];
                Logger.verbose(transitionString);
                String[] transitionParts = transitionString.split(" ");
                transitions[currentTransition] = new int[]{
                        Integer.parseInt(transitionParts[0]),
                        Integer.parseInt(transitionParts[1]),
                        Integer.parseInt(transitionParts[2]),
                        Integer.parseInt(transitionParts[4]),
                        Integer.parseInt(transitionParts[5]),
                        Integer.parseInt(transitionParts[6]),
                };
                currentTransition++;
            }

            return new Board(map, transitions);

        } catch (Exception e) {
            Logger.fatal("Error parsing map string: " + e.getMessage());
            return null;
        }
    }

    public static Board constructFromFile(String filename) {
        Logger.log("Constructing Map from file " + filename);
        return constructFromString(readFile(filename));
    }
}
