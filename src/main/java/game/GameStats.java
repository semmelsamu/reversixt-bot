package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import util.Collection;
import util.Tuple;

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

        Set<Tuple<Tile, Coordinates>> tileCoordinatesPair = new HashSet<>();
        for (Tile tile : Tile.values()) {
            if (tile.isUnoccupied() || tile.equals(Tile.WALL)) {
                continue;
            }
            // all players
            for (Coordinates cor : getAllCoordinatesWhereTileIs(tile)) {
                tileCoordinatesPair.add(new Tuple<>(tile, cor));
            }
        }

        for (Tuple<Tile, Coordinates> tileCoordinatesTuple : tileCoordinatesPair) {
            boolean nextTuple = false;
            for (Community community : communities) {
                if (community.getAllCoordinates().contains(tileCoordinatesTuple.second())) {
                    nextTuple = true;
                    break;
                }
            }

            if (nextTuple) {
                continue;
            }

            Community community = new Community(tileCoordinatesTuple.first(),
                    new HashSet<>(Set.of(tileCoordinatesTuple.second())));
            int newCoordinatesCounter;
            do {
                newCoordinatesCounter = 0;
                Set<Coordinates> allCoordinates = community.getAllCoordinates();
                for (Coordinates cor : allCoordinates) {
                    for (Direction dir : Direction.values()) {
                        TileReader reader = new TileReader(game, cor, dir);
                        if (reader.hasNext()) {
                            reader.next();
                            if (reader.getTile().isUnoccupied() ||
                                    allCoordinates.contains(reader.getCoordinates())) {
                                continue;
                            }
                            community.addCoordinate(reader.getTile(),
                                    reader.getCoordinates());
                            newCoordinatesCounter++;
                        }
                    }
                }

            } while (newCoordinatesCounter > 0);
            communities.add(community);
        }
        mergeIdenticalCommunities();
        System.out.println(communities);
    }

    private void mergeIdenticalCommunities() {
        List<Community> mergedCommunities = new ArrayList<>();
        for (Community community : communities) {
            boolean merged = false;
            for (Community other : mergedCommunities) {
                if (community.equals(other)) {
                    other.addAllCoordinates(community);
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                mergedCommunities.add(community);
            }
        }
        communities = mergedCommunities;
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
            if (community.getAllCoordinates().contains(position)) {
                searchCommunity = community;
                // Found community where tile can just easily be replaced
                break;
            }
        }
        Set<Community> communitiesToRemove = new HashSet<>();
        // if new tile is added on a new field where no community is, check for merge
        if (searchCommunity == null) {
            for (Direction dir : Direction.values()) {
                TileReader reader = new TileReader(game, position, dir);
                if (reader.hasNext()) {
                    reader.next();
                    // skip if no player is detected
                    if (reader.getTile().isUnoccupied()) {
                        continue;
                    }
                    // check the community of the neighbour
                    for (Community community : communities) {
                        if (community.getAllCoordinates().contains(reader.getCoordinates())) {
                            communitiesToRemove.add(community);
                        }
                    }
                }
            }
            // if no communities are around, it can be added to only one be found
            if (communitiesToRemove.size() == 1) {
                searchCommunity = communitiesToRemove.iterator().next();
            } else {
                // merge the communities to a single one
                Community newCommunity = new Community();
                for (Community community : communitiesToRemove) {
                    newCommunity.addAllCoordinates(community);
                }
                communities.removeAll(communitiesToRemove);
                searchCommunity = newCommunity;
            }
        } else {
            // old position need to be removed
            searchCommunity.removeCoordinate(position);
        }
        //add new position
        searchCommunity.addCoordinate(value, position);
        if (searchCommunity.foundKey(Tile.fromChar((char) (game.getClientPlayer() + '0')))) {
            searchCommunity.setRelevant(true);
        }
        communities.add(searchCommunity);
    }

    @Override
    public GameStats clone() {
        try {
            GameStats clone = (GameStats) super.clone();
            clone.coordinatesGroupedByTile = new HashMap<>();
            for (Map.Entry<Tile, Set<Coordinates>> entry : coordinatesGroupedByTile.entrySet()) {
                clone.coordinatesGroupedByTile.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            clone.communities = new ArrayList<>(this.communities);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void updateCommunityRelevance(int clientPlayer) {
        for (Community community : communities) {
            if (community.foundKey(Tile.fromChar((char) (clientPlayer + '0')))) {
                community.setRelevant(true);
            }
        }
        System.out.println(communities);
    }
}
