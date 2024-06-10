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

    Map<Tile, Set<Coordinates>> coordinatesGroupedByTile;

    List<Community> communities;

    public GameStats(Game game) {
        coordinatesGroupedByTile = new HashMap<>();
        communities = new ArrayList<>();
        for (Tile tile : Tile.values()) {
            coordinatesGroupedByTile.put(tile,
                    new HashSet<>(game.getAllCoordinatesWhereTileIs(tile)));
        }

        Set<Tuple<Tile, Coordinates>> tileCoordinatesPair = new HashSet<>();
        for (Tile tile : Tile.values()) {
            if (!tile.isUnoccupied()) {
                // all players
                for (Coordinates cor : getAllCoordinatesWhereTileIs(tile)) {
                    tileCoordinatesPair.add(new Tuple<>(tile, cor));
                }
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

            Community community = new Community(tileCoordinatesTuple.first().character,
                    Set.of(tileCoordinatesTuple.second()));
            int newCoordinatesCounter;
            do {
                newCoordinatesCounter = 0;
                for (Direction dir : Direction.values()) {
                    TileReader reader = new TileReader(game, tileCoordinatesTuple.second(), dir);
                    if (reader.hasNext()) {
                        reader.next();
                        if (reader.getTile().isUnoccupied() ||
                                community.getAllCoordinates().contains(reader.getCoordinates())) {
                            continue;
                        }
                        community.addCoordinate(reader.getTile().character,
                                reader.getCoordinates());
                        newCoordinatesCounter++;
                    }
                }
            } while (newCoordinatesCounter > 0);

        }

        mergeIdenticalCommunities();
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

    public void updateCommunities(Coordinates position, Tile value) {
        Community searchCommunity = null;
        for (Community community : communities) {
            if (community.getAllCoordinates().contains(position)) {
                searchCommunity = community;
                break;
            }
        }
        if (searchCommunity == null) {
            mergeIdenticalCommunities();
        }
        searchCommunity.removeCoordinate(position);
        searchCommunity.addCoordinate(value.character, position);

    }

    @Override
    public GameStats clone() {
        try {
            GameStats clone = (GameStats) super.clone();
            clone.coordinatesGroupedByTile = new HashMap<>();
            for (Map.Entry<Tile, Set<Coordinates>> entry : coordinatesGroupedByTile.entrySet()) {
                clone.coordinatesGroupedByTile.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
