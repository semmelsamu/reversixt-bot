package game;

import map.Board;
import util.Logger;

import java.util.Arrays;

import static util.File.readFile;

public class Game {
    private static int initialPlayers;
    private static int initialOverwriteStones;
    private static int initialBombs;
    private static int bombRadius;
    private Board map;

    public static Game constructFromFile(String path) {
        Logger.log("Constructing Map from file " + path);

        String[] lines = readFile(path).split("\\r?\\n"); // Mind lines can be separated by nl or cr+nl

        String[] gameLines = new String[]{lines[0], lines[1], lines[2]};
        String[] boardArray = (String[]) Arrays.stream(lines, 3, lines.length).toArray();
        Game game = constructGameFromLines(gameLines);
        game.setBoard(Board.constructBoardFromLines(boardArray));
        return game;
    }

    private static Game constructGameFromLines(String[] gameLines) {
        Logger.debug(gameLines[0]);
        initialPlayers = Integer.parseInt(gameLines[0]);

        Logger.debug(gameLines[1]);
        initialOverwriteStones = Integer.parseInt(gameLines[1]);

        Logger.debug(gameLines[2]);
        String[] line3Split = gameLines[2].split((" "));
        initialBombs = Integer.parseInt(line3Split[0]);
        bombRadius = Integer.parseInt(line3Split[1]);
        return new Game();
    }

    public int getInitialPlayers() {
        return initialPlayers;
    }

    public int getInitialOverwriteStones() {
        return initialOverwriteStones;
    }

    public int getInitialBombs() {
        return initialBombs;
    }

    public int getBombRadius() {
        return bombRadius;
    }

    public Board getMap() {
        return map;
    }

    public void setBoard(Board map){
        this.map = map;
    }
}
