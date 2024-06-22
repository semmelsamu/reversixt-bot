package stats;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import game.Game;
import game.Player;

import java.util.HashSet;
import java.util.Set;

public class Communities implements Cloneable {

    private Set<Community> communities;

    private Community lastUpdatedCommunity;

    private boolean communitiesDisabled;

    public Communities(Game game) {

        lastUpdatedCommunity = null;

        Set<Coordinates> allOccupiedCoordinates = new HashSet<>();

        for (Player player : game.getPlayers()) {
            allOccupiedCoordinates.addAll(
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                            player.getPlayerValue()));
        }

        communities = new HashSet<>();

        for (Coordinates coordinate : allOccupiedCoordinates) {

            // Check if Coordinate is already present in a Community
            boolean coordinateIsPresentInCommunity = false;
            for (Community community : communities) {
                if (community.getCoordinates().contains(coordinate)) {
                    coordinateIsPresentInCommunity = true;
                    break;
                }
            }
            if (coordinateIsPresentInCommunity) {
                continue;
            }

            // Coordinate is not yet in a community, so a new one has to be created
            Community community = new Community(game);
            community.addAllCoordinates(expandCoordinateToCommunity(coordinate, game), game);

            communities.add(community);

        }

        checkDisableCommunities(game);
    }

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

    private void checkDisableCommunities(Game game) {
        Set<Community> relevantCommunities = new HashSet<>(communities);
        for (Community community : communities) {
            int sumPlayers = 0;
            for (Player player : game.getPlayers()) {
                sumPlayers += community.getTileCount(player.getPlayerValue());
            }
            if (sumPlayers == 0) {
                // Should be only these communities, which only contains expansion stones
                relevantCommunities.remove(community);
            }
        }
        communitiesDisabled = relevantCommunities.size() < 2;
    }

    public void setLastUpdatedCommunity(Community lastUpdatedCommunity) {
        this.lastUpdatedCommunity = lastUpdatedCommunity;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Set<Community> getCommunities() {
        return communities;
    }

    public boolean isCommunitiesDisabled() {
        return communitiesDisabled;
    }

    public Community getLastUpdatedCommunity() {
        return lastUpdatedCommunity;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Other Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void updateCommunities(Set<Coordinates> positions, Tile value, Game game) {
        if (communitiesDisabled) {
            return;
        }

        Community searchCommunity = null;
        for (Coordinates position : positions) {
            for (Community community : communities) {
                if (community.getCoordinates().contains(position)) {
                    searchCommunity = community;
                    // Found community where tile can just easily be replaced
                    break;
                }
            }
            // If new tile is added on a new field where no community is, check for merge
            if (searchCommunity == null) {
                Set<Coordinates> neighbourCoordinates =
                        CoordinatesExpander.expandCoordinates(game, Set.of(position), 1);
                neighbourCoordinates.removeIf(
                        coordinate -> game.getTile(coordinate).isUnoccupied());

                Set<Community> communitiesToRemove = new HashSet<>();
                for (Coordinates neighbourCoordinate : neighbourCoordinates) {
                    // check the community of the neighbour
                    for (Community community : communities) {
                        if (community.getCoordinates().contains(neighbourCoordinate)) {
                            communitiesToRemove.add(community);
                        }
                    }
                }
                // If no communities are around, it can be added to only one be found
                if (communitiesToRemove.size() == 1) {
                    searchCommunity = communitiesToRemove.iterator().next();
                } else {
                    // Merge the communities to a single one
                    Community newCommunity = new Community(game);
                    for (Community community : communitiesToRemove) {
                        newCommunity.addAllCoordinatesFromCommunity(community, game);
                    }
                    communities.removeAll(communitiesToRemove);
                    communities.add(newCommunity);
                    searchCommunity = newCommunity;
                }
            } else {
                // Old position need to be removed
                searchCommunity.removeCoordinate(position, game);
            }
            // Add new position
            searchCommunity.addCoordinate(position, game);
        }

        lastUpdatedCommunity = searchCommunity;
        checkDisableCommunities(game);
    }

    @Override
    public Communities clone() {
        try {

            Communities clone = (Communities) super.clone();

            clone.communities = new HashSet<>();
            for (Community community : communities) {
                clone.communities.add(community.clone());
            }
            clone.lastUpdatedCommunity =
                    this.lastUpdatedCommunity != null ? this.lastUpdatedCommunity.clone() : null;

            return clone;

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }
}
