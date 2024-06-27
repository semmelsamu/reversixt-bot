package game;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import game.logic.MoveCalculator;
import move.Move;
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
    Set<Coordinates> coordinates;

    /**
     * Stores the number of tiles each player in this community occupies. The first entry [0] is
     * reserved for expansion stones, each following entry stores the amount of tiles the
     * corresponding player has.
     */
    int[] tileCounts;

    Set<Coordinates> reachableCoordinates;

    Game game;

    /**
     * Initialize a new Community.
     * @param game       The game this Community is part of.
     * @param coordinate A Coordinate in the community.
     */
    public Community(Game game, Coordinates coordinate) {

        this.game = game;

        coordinates = new HashSet<>();

        // Initialize tile counts with 0
        tileCounts = new int[game.getPlayers().length + 1];
        Arrays.fill(tileCounts, 0);

        // Calculate community coordinates
        for (Coordinates coordinateToBeAdded : expandCoordinateToCommunity(game, coordinate)) {
            addCoordinate(coordinateToBeAdded);
        }

        // Calculate reachability map
        reachableCoordinates = calculateReachabilityMap(game, coordinates.iterator().next());
    }

    public void addCoordinate(Coordinates coordinate) {
        if (coordinates.add(coordinate)) {
            // Only update if the coordinate was not already present
            tileCounts[game.getTile(coordinate).toPlayerIndex() + 1]++;
        }
    }

    /**
     * Calculate if this community is reachable for a Player. // TODO: Better heuristic!
     */
    public boolean isReachable(int player) {

        /*
        if (game.getPlayer(player).getOverwriteStones() > 0 && getTileCount(Tile.EXPANSION) > 0) {
            return true;
        }

        if (!MoveCalculator.getValidMovesForPlayer(game, player,
                CoordinatesExpander.expandCoordinates(game, coordinates, 1)).isEmpty()) {
            return true;
        }

        return false;
        */

        // This is the old code with logic from the article.

        // "A Community is considered reachable when..."

        // OPTION A
        // ... there are at least two Players with stones in the community
        int playersWithStones = 0;
        for (int i = 1; i < tileCounts.length; i++) {
            if (tileCounts[i] > 0) {
                playersWithStones++;
            }
            if (playersWithStones >= 2) {
                return true;
            }
        }

        // OPTION B
        // ... one player owns stones, ...
        if (playersWithStones == 0) {
            return false;
        }
        // ... there is at least one expansion stone in the Community, ...
        if (tileCounts[0] == 0) {
            return false;
        }
        // ... and the Player has a move in the community
        return !MoveCalculator.getValidMovesForPlayer(game, player, coordinates).isEmpty();

    }

    /**
     * Merge a Community into this Community.
     * @param community The other Community.
     */
    public void mergeCommunity(Community community) {
        // Merge the coordinates
        for (Coordinates coordinate : community.coordinates) {
            // TileCounts get merged internally
            addCoordinate(coordinate);
        }

        // Merge the reachability map
        reachableCoordinates = new HashSet<>(reachableCoordinates);
        reachableCoordinates.addAll(community.reachableCoordinates);
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

    boolean moveAffectsCommunity(Move move) {
        Set<Coordinates> validCoordinates =
                CoordinatesExpander.expandCoordinates(game, coordinates, 1);
        return validCoordinates.contains(move.getCoordinates());
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
    private static Set<Coordinates> expandCoordinateToCommunity(Game game, Coordinates coordinate) {
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

    private static Set<Coordinates> calculateReachabilityMap(Game game, Coordinates coordinates) {

        Set<Coordinates> result = new HashSet<>();
        result.add(coordinates);

        // Expand the coordinates and add them to the reachable
        // coordinates until no new ones get added
        while (result.addAll(CoordinatesExpander.expandCoordinates(game, result, 1)))
            ;

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
        return Objects.equals(coordinates, community.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates);
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

        result.append("Community #").append(hashCode());

        for (int y = 0; y < game.getHeight(); y++) {

            result.append("\n");

            for (int x = 0; x < game.getWidth(); x++) {

                Coordinates currentPosition = new Coordinates(x, y);

                if (game.getTile(currentPosition).equals(Tile.WALL)) {
                    result.append(". ");
                } else if (coordinates.contains(currentPosition)) {
                    result.append("# ");
                } else if (reachableCoordinates.contains(currentPosition)) {
                    result.append("+ ");
                } else {
                    result.append("- ");
                }

            }
        }

        return result.toString();
    }

}
