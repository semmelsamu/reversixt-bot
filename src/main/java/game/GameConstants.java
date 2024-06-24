package game;

public record GameConstants(
        int initialPlayers,
        int initialOverwriteStones,
        int initialBombs,
        int bombRadius
) {
}
