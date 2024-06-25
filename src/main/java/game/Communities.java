package game;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import util.Tuple;

import java.util.*;

public class Communities implements Cloneable {

    // TODO: Sort by most relevant Community?
    // TODO: Total auslöschung not good in Community!
    private Set<Community> communities;

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

    public Set<Tuple<Game, Community>> getRelevantCommunities(int player) {
        Set<Tuple<Game, Community>> result = new HashSet<>();
        Game game = this.game;

        communityLoop:
        for (var community : communities) {

            // Reachability calculation as specified in the report
            if (!community.isReachable(player)) {
                continue;
            }

            // If there are no valid moves in the Community, it sure cannot be simulated
            if (!community.anyPlayerHasValidMoves()) {
                continue;
            }

            // The following checks are destructive for the game, so a copy has to be made.
            game = game.clone();
            community = game.communities.findCommunityByCoordinates(
                    community.getCoordinates().iterator().next());

            if (community.getRelevantMovesForCurrentPlayer().isEmpty()) {
                community.nextPlayer();
            }

            // Search if we have valid moves in the Community
            int oldPlayer = game.getCurrentPlayerNumber();
            while (game.getCurrentPlayerNumber() != player) {
                community.nextPlayer();
                if (game.getCurrentPlayerNumber() == oldPlayer) {
                    continue communityLoop;
                }
            }

            result.add(new Tuple<>(game, community));

        }

        return result;
    }

    public Community findCommunityByCoordinates(Coordinates coordinates) {
        for (Community community : communities) {
            if (community.coordinates.contains(coordinates)) {
                return community;
            }
        }
        return null;
    }

    public void updateCommunities(Coordinates coordinates) {

        // TODO: Maybe merge Communities already when they are close together? ~3 Tiles?

        List<Community> communitiesToBeUpdated = new LinkedList<>();

        for (var community : communities) {
            // Check if Coordinates border the community
            if (CoordinatesExpander.expandCoordinates(game, community.getCoordinates(), 1)
                    .contains(coordinates)) {
                communitiesToBeUpdated.add(community);
            }
        }

        // Get the Community the others get merged into
        // TODO: Better heuristic? Maybe the largest one?
        Community resultCommunity = communitiesToBeUpdated.remove(0);

        // Add the new Coordinate to the Community
        resultCommunity.addCoordinate(coordinates);

        // Merge with the other Communities
        for (var communityToBeMerged : communitiesToBeUpdated) {

            // Merge community
            resultCommunity.mergeCommunity(communityToBeMerged);

            // Remove merged community as it is now contained in the resultCommunity.
            // We can't use communities.remove(communityToBeMerged) because of some Java edge-case,
            // this doesn't remove it. Instead, we need to do it with an iterator. Ugly but works.
            // These 8 lines of code cost me 6 hours, I hope that they are appreciated.
            Iterator<Community> iterator = communities.iterator();
            while (iterator.hasNext()) {
                Community community = iterator.next();
                if (community.equals(communityToBeMerged)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    public void setGame(Game game) {
        this.game = game;
        for (var community : communities) {
            community.setGame(game);
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
