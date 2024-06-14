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

    public GameStats(Game game) {
        coordinatesGroupedByTile = new HashMap<>();
        communities = new ArrayList<>();
        for (Tile tile : Tile.values()) {
            coordinatesGroupedByTile.put(tile,
                    new HashSet<>(game.getAllCoordinatesWhereTileIs(tile)));
        }

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

    public void updateCommunities(Coordinates position, Tile value, Game game) {
        Community searchCommunity = null;
        for (Community community : communities) {
            if (community.getCoordinates().contains(position)) {
                searchCommunity = community;
                // Found community where tile can just easily be replaced
                break;
            }
        }
        Set<Community> communitiesToRemove = new HashSet<>();
        // if new tile is added on a new field where no community is, check for merge
        if (searchCommunity == null) {

            Set<Coordinates> neighbourCoordinates =
                    CoordinatesExpander.expandCoordinates(game, Set.of(position), 1);
            neighbourCoordinates.removeIf(coordinate -> game.getTile(coordinate).isUnoccupied());

            for (Coordinates neighbourCoordinate : neighbourCoordinates) {
                // check the community of the neighbour
                for (Community community : communities) {
                    if (community.getCoordinates().contains(neighbourCoordinate)) {
                        communitiesToRemove.add(community);
                    }
                }
            }
            // if no communities are around, it can be added to only one be found
            if (communitiesToRemove.size() == 1) {
                searchCommunity = communitiesToRemove.iterator().next();
            } else {
                // merge the communities to a single one
                Community newCommunity = new Community(game);
                for (Community community : communitiesToRemove) {
                    newCommunity.addAllCoordinatesFromCommunity(community, game);
                }
                communities.removeAll(communitiesToRemove);
                communities.add(newCommunity);
                searchCommunity = newCommunity;
            }
        } else {
            // old position need to be removed
            searchCommunity.removeCoordinate(position, game);
        }
        //add new position
        searchCommunity.addCoordinate(position, value);

        for (Community community : communities) {
            community.setUpdatedCommunity(false);
        }
        searchCommunity.setUpdatedCommunity(true);
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
