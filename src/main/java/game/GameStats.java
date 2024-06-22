package game;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;

import java.util.HashSet;
import java.util.Set;

public class GameStats implements Cloneable {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */


    private Set<Community> communities;

    private Community lastUpdatedCommunity;

    private boolean communitiesDisabled;

    public GameStats(Game game) {

        lastUpdatedCommunity = null;
        communities = new HashSet<>();

        initializeCommunities(game);
    }

    private void initializeCommunities(Game game) {
        Set<Coordinates> allOccupiedCoordinates = new HashSet<>();
        for (Tile tile : Tile.values()) {
            if (tile.isUnoccupied() || tile.equals(Tile.WALL)) {
                continue;
            }
            // all players
            allOccupiedCoordinates.addAll(
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(tile));
        }

        for (Coordinates coordinate : allOccupiedCoordinates) {
            boolean nextV = false;
            for (Community community : communities) {
                if (community.getCoordinates().contains(coordinate)) {
                    nextV = true;
                    break;
                }
            }

            if (nextV) {
                continue;
            }

            Community community = new Community(game);
            community.addCoordinateOfExistingField(coordinate, game);
            int oldCoordinatesCounter;
            do {
                oldCoordinatesCounter = community.getCoordinates().size();
                Set<Coordinates> coordinatesInCommunity =
                        CoordinatesExpander.expandCoordinates(game, community.getCoordinates(), 1);
                coordinatesInCommunity.removeIf(cor -> game.getTile(cor).isUnoccupied());
                community.addAllCoordinates(coordinatesInCommunity, game);
            } while (oldCoordinatesCounter != community.getCoordinates().size());
            communities.add(community);
        }
        checkDisableCommunities(game);
    }

    private void checkDisableCommunities(Game game) {
        Set<Community> relevantCommunities = new HashSet<>(communities);
        for (Community community : communities) {
            int sumPlayers = 0;
            for (Player player : game.getPlayers()) {
                sumPlayers += community.getTileAmountByPlayer(player.getPlayerValue());
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
            searchCommunity.addCoordinate(position, value);
        }

        lastUpdatedCommunity = searchCommunity;
        checkDisableCommunities(game);
    }

    @Override
    public GameStats clone() {
        try {
            GameStats clone = (GameStats) super.clone();

            clone.communities = new HashSet<>();
            for (Community community : communities) {
                clone.communities.add(community.clone());
            }
            clone.lastUpdatedCommunity =
                    this.lastUpdatedCommunity != null ? this.lastUpdatedCommunity.clone() : null;

            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
