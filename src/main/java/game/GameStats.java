package game;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import util.Collection;

import java.util.*;

public class GameStats implements Cloneable {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    private Map<Tile, Set<Coordinates>> coordinatesGroupedByTile;

    private List<Community> communities;

    private boolean enableCommunities;

    public GameStats(Game game) {
        coordinatesGroupedByTile = new HashMap<>();
        communities = new ArrayList<>();
        for (Tile tile : Tile.values()) {
            coordinatesGroupedByTile.put(tile,
                    new HashSet<>(game.getAllCoordinatesWhereTileIs(tile)));
        }

        initializeCommunities(game);
    }

    private void initializeCommunities(Game game) {
        Set<Coordinates> allOccupiedCoordinates = new HashSet<>();
        for (Tile tile : Tile.values()) {
            if (tile.isUnoccupied() || tile.equals(Tile.WALL)) {
                continue;
            }
            // all players
            allOccupiedCoordinates.addAll(getAllCoordinatesWhereTileIs(tile));
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
        checkEnableCommunities(game);
    }

    private void checkEnableCommunities(Game game) {
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
        enableCommunities = relevantCommunities.size() >= 2;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Set<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return coordinatesGroupedByTile.get(tile);
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public boolean isEnableCommunities() {
        return enableCommunities;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Other Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    // TODO: Performance?
    public void replaceTileAtCoordinates(Coordinates coordinates, Tile tile) {
        coordinatesGroupedByTile.get(
                        Collection.findKeyByValue(coordinatesGroupedByTile, coordinates))
                .remove(coordinates);
        coordinatesGroupedByTile.get(tile).add(coordinates);
    }

    public void updateCommunities(Set<Coordinates> positions, Tile value, Game game) {
        if (!enableCommunities) {
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
            Set<Community> communitiesToRemove = new HashSet<>();
            // If new tile is added on a new field where no community is, check for merge
            if (searchCommunity == null) {

                Set<Coordinates> neighbourCoordinates =
                        CoordinatesExpander.expandCoordinates(game, Set.of(position), 1);
                neighbourCoordinates.removeIf(
                        coordinate -> game.getTile(coordinate).isUnoccupied());

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
        for (Community community : communities) {
            community.setUpdatedCommunity(false);
        }
        // There have to be at least one community
        assert searchCommunity != null;
        searchCommunity.setUpdatedCommunity(true);

        checkEnableCommunities(game);
    }

    @Override
    public GameStats clone() {
        try {
            GameStats clone = (GameStats) super.clone();
            clone.coordinatesGroupedByTile = new HashMap<>();
            for (Map.Entry<Tile, Set<Coordinates>> entry : coordinatesGroupedByTile.entrySet()) {
                clone.coordinatesGroupedByTile.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }

            clone.communities = new ArrayList<>();
            for (Community community : communities) {
                clone.communities.add(community.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
