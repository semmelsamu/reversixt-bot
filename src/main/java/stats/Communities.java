package stats;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Communities implements Cloneable {

    private Set<Community> communities;

    public Communities(Game game) {

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

    public Set<Community> getAllReachableCommunities(Game game, int player) {
        Set<Community> result = new HashSet<>();

        for (var community : communities) {
            if (community.isReachable(game, player)) {
                result.add(community);
            }
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
        result.append("Communities");
        for (var community : communities) {
            result.append("\n").append(community.toString());
        }
        return result.toString();
    }

    public String toString(Game game) {
        List<Community> orderedCommunities = new ArrayList<>(communities);
        StringBuilder result = new StringBuilder();
        result.append("All Communities");
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
