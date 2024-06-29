package game;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import util.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Communities implements Cloneable {

    // TODO: Sort by most relevant Community?
    // TODO: Total auslöschung not good in Community!
    Set<Community> communities;

    Community simulating;

    Game game;

    public Communities(Game game) {

        this.game = game;

        Set<Coordinates> allOccupiedCoordinates = new HashSet<>(
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(Tile.EXPANSION));

        for (Player player : game.getPlayers()) {
            allOccupiedCoordinates.addAll(
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                            player.getPlayerValue()));
        }

        communities = new HashSet<>();

        coordinatesLoop:
        for (Coordinates coordinate : allOccupiedCoordinates) {

            // Check if Coordinate is already present in a Community
            for (Community community : communities) {
                if (community.coordinates.contains(coordinate)) {
                    continue coordinatesLoop;
                }
            }

            // Coordinate is not yet in a community, so a new one has to be created
            communities.add(new Community(game, coordinate));

            // TODO: Initial reachability maps can potentially be equal on different communities.
            //  Then we may only need to store it in one community and the others can reference it.
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Get all Communities
     */
    public Set<Community> get() {
        return communities;
    }

    /**
     * Get all Communities that are relevant. Uses Community::isRelevant to determine if a Community
     * is relevant.
     */
    public Set<Community> getRelevant() {
        return communities.stream().filter(Community::isRelevant).collect(Collectors.toSet());
    }

    /**
     * Get the Community on which the Coordinates are located.
     */
    public Community get(Coordinates coordinates) {
        for (Community community : communities) {
            if (community.coordinates.contains(coordinates)) {
                return community;
            }
        }
        return null;
    }

    /**
     * @param community A Community
     * @return The same Community from the attached Game
     */
    public Community get(Community community) {
        return get(community.coordinates.iterator().next());
    }

    /**
     * Set the simulating Community and find a valid Player, i.e. a Player that has valid Moves in
     * this Community.
     */
    public void simulate(Community community) {
        simulating = community;
        game.findValidPlayer();
    }

    /**
     * Return the Community that is currently being simulated or null if no Community is simulated.
     */
    public Community getSimulating() {
        return simulating;
    }

    /**
     * Merge two Communities.
     */
    public Community merge(Community community1, Community community2) {

        // The mergeCommunity will merge into the resultCommunity
        Community resultCommunity, mergeCommunity;

        // Merging the smaller Community into the bigger one -> faster
        if (community1.coordinates.size() > community2.coordinates.size()) {
            resultCommunity = community1;
            mergeCommunity = community2;
        } else {
            resultCommunity = community2;
            mergeCommunity = community1;
        }

        // Merge the coordinates
        for (Coordinates coordinate : mergeCommunity.coordinates) {
            // TileCounts get merged internally
            resultCommunity.addCoordinate(coordinate);
        }

        // Merge the reachability map
        resultCommunity.reachableCoordinates.addAll(mergeCommunity.reachableCoordinates);

        // Remove mergeCommunity as it is now contained in the resultCommunity. We can't use
        // communities.remove(mergeCommunity) because of some Java edge-case, this doesn't work.
        // Instead, we need to do it with an iterator. Ugly but works. These 8 lines of code cost
        // me 6 hours, I hope that they are appreciated.
        Iterator<Community> iterator = communities.iterator();
        while (iterator.hasNext()) {
            Community community = iterator.next();
            if (community.equals(mergeCommunity)) {
                iterator.remove();
                break;
            }
        }

        return resultCommunity;
    }

    /**
     * Check if Communities have to add the Coordinate because the Tile on the Coordinates changed,
     * and merge Communities that got close to each other.
     */
    public void update(Coordinates coordinates) {

        // The Community the other Communities get merged into.
        Community resultCommunity = null;

        Set<Community> mergeCommunities = new HashSet<>();

        for (var community : communities) {

            // Check if Coordinates border the community
            if (CoordinatesExpander.expandCoordinates(game, community.coordinates,
                    Constants.COMMUNITY_MERGE_RADIUS).contains(coordinates)) {

                mergeCommunities.add(community);

                // The resultCommunity should be the largest one, as then only the smallest ones
                // get merged -> Performance
                if (resultCommunity == null ||
                        resultCommunity.coordinates.size() > community.coordinates.size()) {
                    resultCommunity = community;
                }

            }
        }

        if (mergeCommunities.isEmpty()) {
            return;
        }

        mergeCommunities.remove(resultCommunity);

        resultCommunity.addCoordinate(coordinates);

        for (Community mergeCommunity : mergeCommunities) {
            resultCommunity = merge(resultCommunity, mergeCommunity);
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
    public Communities clone() {
        try {

            Communities clone = (Communities) super.clone();

            clone.communities = new HashSet<>();

            for (Community community : communities) {
                clone.communities.add(community.clone());
            }

            return clone;

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(communities.size()).append(" Communities");
        for (var community : communities) {
            result.append("\n").append(community.toString());
        }
        return result.toString();
    }
}
