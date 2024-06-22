package stats;

import board.Coordinates;
import board.Tile;
import game.Game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Community implements Cloneable {

    /**
     * The Coordinates this Community includes.
     */
    private Set<Coordinates> coordinates;

    /**
     * Stores the number of tiles each player in this community occupies. The first entry [0] is
     * reserved for expansion stones, each following entry stores the amount of tiles the
     * corresponding player has.
     */
    private int[] tileCounts;

    public Community(Game game) {

        coordinates = new HashSet<>();

        // Initialize tile counts with 0
        tileCounts = new int[game.getPlayers().length + 1];
        Arrays.fill(tileCounts, 0);
    }

    public void addCoordinate(Coordinates coordinate, Game game) {
        if (coordinates.add(coordinate)) {
            // Only update if the coordinate was not already present
            tileCounts[game.getTile(coordinate).toPlayerIndex() + 1]++;
        }
    }

    public void addAllCoordinates(Set<Coordinates> coordinates, Game game) {
        for (Coordinates coordinate : coordinates) {
            addCoordinate(coordinate, game);
        }
    }

    public void removeCoordinate(Coordinates coordinate, Game game) {
        if (coordinates.remove(coordinate)) {
            // Only update if the coordinate was actually removed
            tileCounts[game.getTile(coordinate).toPlayerIndex() + 1]--;
        }
    }

    public void addAllCoordinatesFromCommunity(Community other, Game game) {
        for (Coordinates coordinate : other.coordinates) {
            addCoordinate(coordinate, game);
        }
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public int getTileCount(Tile tile) {
        if (tile.equals(Tile.EXPANSION)) {
            return tileCounts[0];
        } else if (tile.isPlayer()) {
            return tileCounts[tile.toPlayerIndex() + 1];
        } else {
            return -1;
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Overrides
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Community community = (Community) o;
        return Objects.equals(coordinates, community.coordinates) &&
                Arrays.equals(tileCounts, community.tileCounts);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(coordinates);
        result = 31 * result + Arrays.hashCode(tileCounts);
        return result;
    }

    @Override
    public Community clone() {
        try {

            Community clone = (Community) super.clone();

            clone.coordinates = new HashSet<>(this.coordinates);
            clone.tileCounts = this.tileCounts.clone();

            return clone;

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Community\n");
        result.append("- Coordinates: ");
        for (var coordinate : coordinates) {
            result.append(coordinate).append(", ");
        }
        result.append("\n- Tile counts: ");
        result.append("EXPANSION: ").append(tileCounts[0]);
        for (int i = 1; i < tileCounts.length; i++) {
            result.append(", PLAYER").append(i).append(": ").append(tileCounts[i]);
        }
        return result.toString();
    }

}
