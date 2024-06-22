package stats;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import game.Game;
import util.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Community implements Cloneable {

    Logger logger = new Logger(this.getClass().getName());

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

    private Set<Coordinates> reachableCoordinates;

    /**
     * Initialize a new Community.
     * @param game       The game this Community is part of.
     * @param coordinate A Coordinate in the community.
     */
    public Community(Game game, Coordinates coordinate) {

        coordinates = new HashSet<>();

        // Initialize tile counts with 0
        tileCounts = new int[game.getPlayers().length + 1];
        Arrays.fill(tileCounts, 0);

        // Calculate community coordinates
        for (Coordinates coordinateToBeAdded : expandCoordinateToCommunity(coordinate, game)) {
            addCoordinate(coordinateToBeAdded, game);
        }

        // Calculate reachability map
        reachableCoordinates = new HashSet<>(coordinates);
        // Expand the coordinates and add them to the reachable
        // coordinates until no new ones get added
        while (reachableCoordinates.addAll(
                CoordinatesExpander.expandCoordinates(game, reachableCoordinates, 1)))
            ;

        logger.debug(this.toString(game));
    }

    public void addCoordinate(Coordinates coordinate, Game game) {
        if (coordinates.add(coordinate)) {
            // Only update if the coordinate was not already present
            tileCounts[game.getTile(coordinate).toPlayerIndex() + 1]++;
        }
    }

    public void removeCoordinate(Coordinates coordinate, Game game) {
        if (coordinates.remove(coordinate)) {
            // Only update if the coordinate was actually removed
            tileCounts[game.getTile(coordinate).toPlayerIndex() + 1]--;
        }
    }

    /**
     * Merge a Community into this Community.
     * @param community The other Community.
     * @param game      The game the Communities are part of.
     */
    public void mergeCommunity(Community community, Game game) {
        // Merge the coordinates
        for (Coordinates coordinate : community.coordinates) {
            // TileCounts get merged internally
            addCoordinate(coordinate, game);
        }

        // Merge the reachability map
        reachableCoordinates = new HashSet<>(reachableCoordinates);
        reachableCoordinates.addAll(community.reachableCoordinates);
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
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Calculate all coordinates of the community that contains the coordinate
     */
    private static Set<Coordinates> expandCoordinateToCommunity(Coordinates coordinate, Game game) {
        Set<Coordinates> result = new HashSet<>();

        Set<Coordinates> coordinatesToBeAdded = new HashSet<>();
        coordinatesToBeAdded.add(coordinate);

        // addAll returns true if new elements were added
        while (result.addAll(coordinatesToBeAdded)) {

            coordinatesToBeAdded = CoordinatesExpander.expandCoordinates(game, result, 1);
            coordinatesToBeAdded.removeIf(coordinates -> game.getTile(coordinates).isUnoccupied());

        }

        return result;
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
        return Objects.hashCode(reachableCoordinates) * 10_000 + Objects.hashCode(coordinates);
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
        result.append("Community ").append(hashCode()).append("\n");
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

    public String toString(Game game) {
        StringBuilder result = new StringBuilder();
        result.append("Community visualized. WALL=. CURRENT=# REACHABLE=* NOT_REACHABLE=-");
        for (int y = 0; y < game.getHeight(); y++) {
            result.append("\n");
            for (int x = 0; x < game.getWidth(); x++) {
                Coordinates currentPosition = new Coordinates(x, y);

                if (game.getTile(currentPosition).equals(Tile.WALL)) {
                    result.append(". ");
                } else if (coordinates.contains(currentPosition)) {
                    result.append("# ");
                } else if (reachableCoordinates.contains(currentPosition)) {
                    result.append("* ");
                } else {
                    result.append("- ");
                }
            }
        }
        return result.toString();
    }

}
