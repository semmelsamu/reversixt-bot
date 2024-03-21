package map;

import util.Logger;

import java.util.Arrays;

import static util.File.readFile;

public class Map {

    private final int initialPlayers;
    private final int initialOverwriteStones;
    private final int initialBombs, bombRadius;
    private final int width, height;
    private TileType[][] map;

    public Map(int initialPlayers,
               int initialOverwriteStones,
               int initialBombs, int bombRadius,
               int width, int height,
               char[][] charMap
    ) {
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        this.width = width;
        this.height = height;

        this.map = new TileType[charMap.length][charMap[0].length];

        for (int i = 0; i < charMap.length; i++) {
            for (int j = 0; j < charMap[i].length; j++) {
                this.map[i][j] = TileType.fromChar(charMap[i][j]);
            }
        }

        Logger.log("Constructed map");
        Logger.debug(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Map\n{" +
                "initialPlayers=" + initialPlayers +
                ", initialOverwriteStones=" + initialOverwriteStones +
                ", initialBombs=" + initialBombs +
                ", bombRadius=" + bombRadius +
                ", width=" + width +
                ", height=" + height +
                "}\n");

        for(TileType[] row : map) {
            for(TileType column: row) {
                result.append(column).append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }

    public static Map constructFromString(String string) {
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
            int width = Integer.parseInt(line4Split[0]);
            int height = Integer.parseInt(line4Split[1]);

            char[][] map = new char[height][width];
            for (int y = 0; y < height; y++) {
                // Lines 0-3 are initialPlayer, initialOverwriteStones..., so we start at line 4
                Logger.debug(lines[y + 4]);
                String[] currentRow = lines[y + 4].split((" "));
                for (int x = 0; x < width; x++) {
                    map[y][x] = currentRow[x].charAt(0);
                }
            }

            return new Map(initialPlayers,
                    initialOverwriteStones,
                    initialBombs,
                    bombRadius,
                    width,
                    height,
                    map);

        } catch (Exception e) {
            Logger.fatal("Error parsing map string: " + e.getMessage());
            return null;
        }
    }

    public static Map constructFromFile(String filename) {
        return constructFromString(readFile(filename));
    }
}
