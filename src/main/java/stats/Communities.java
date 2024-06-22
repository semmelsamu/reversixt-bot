package stats;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.Player;
import util.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Communities implements Cloneable {

    Logger logger = new Logger(this.getClass().getName());

    private Set<Community> communities;

    private Community lastUpdatedCommunity;

    private boolean communitiesDisabled;

    public Communities(Game game) {

        lastUpdatedCommunity = null;

        Set<Coordinates> allOccupiedCoordinates = new HashSet<>(
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(Tile.EXPANSION));

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
            communities.add(new Community(game, coordinate));

            // TODO: Initial reachability maps can potentially be equal on different communities.
            //       Then we may only need to store it in one community and the others can reference
            //       it.
        }

        this.logger.log("Initial Communities: " + this);
        this.logger.log(this.toString(game));

        // checkDisableCommunities(game);
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

    /* public void updateCommunities(Set<Coordinates> positions, Tile value, Game game) {
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
                        newCommunity.mergeCommunity(community, game);
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
    }*/

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void setLastUpdatedCommunity(Community lastUpdatedCommunity) {
        this.lastUpdatedCommunity = lastUpdatedCommunity;
    }

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
            clone.lastUpdatedCommunity =
                    this.lastUpdatedCommunity != null ? this.lastUpdatedCommunity.clone() : null;

            return clone;

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Communities");
        for (var community : communities) {
            result.append("\n").append(community.toString());
        }
        return result.toString();
    }

    public String toString(Game game) {
        List<Community> orderedCommunities = new ArrayList<>(communities);
        StringBuilder result = new StringBuilder();
        result.append("Communities visualized");
        for (int y = 0; y < game.getHeight(); y++) {
            result.append("\n");
            for (int x = 0; x < game.getWidth(); x++) {
                Coordinates currentPosition = new Coordinates(x, y);

                if (game.getTile(currentPosition).equals(Tile.WALL)) {
                    result.append("# ");
                    continue;
                }

                char community = '-';
                for (int i = 0; i < orderedCommunities.size(); i++) {
                    if (orderedCommunities.get(i).getCoordinates().contains(currentPosition)) {
                        community = (char) (i + '0');
                    }
                }
                result.append(community).append(" ");
            }
        }
        return result.toString();
    }
}
