package game;

import map.Board;
import util.Logger;

public class Game {
    private final int initialPlayers;
    private final int initialOverwriteStones;
    private final int initialBombs, bombRadius;
    private final Board map;

    public Game(Board map) {
        this.map = map;
        String[] lines = map.getPath().split("\\r?\\n"); // Mind lines can be separated by nl or cr+nl
        Logger.debug(lines[0]);
        initialPlayers = Integer.parseInt(lines[0]);

        Logger.debug(lines[1]);
        initialOverwriteStones = Integer.parseInt(lines[1]);

        Logger.debug(lines[2]);
        String[] line3Split = lines[2].split((" "));
        initialBombs = Integer.parseInt(line3Split[0]);
        bombRadius = Integer.parseInt(line3Split[1]);
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
}
