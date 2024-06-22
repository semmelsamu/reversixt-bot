package stats;

import board.Coordinates;
import board.Tile;
import game.Game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Community implements Cloneable {

    private Set<Coordinates> coordinates;
    private PlayerTileAmountContainer[] playerTileAmountContainers;

    public Community(Game game) {
        this.coordinates = new HashSet<>();

        int playersLength = game.getPlayers().length;
        this.playerTileAmountContainers = new PlayerTileAmountContainer[playersLength + 1];
        for (int i = 0; i < playersLength; i++) {
            playerTileAmountContainers[i] =
                    new PlayerTileAmountContainer(game.getPlayers()[i].getPlayerValue(), 0);
        }
        playerTileAmountContainers[playersLength] =
                new PlayerTileAmountContainer(Tile.EXPANSION, 0);
    }

    public void addCoordinateOfExistingField(Coordinates coordinate, Game game) {
        addCoordinate(coordinate, game.getTile(coordinate));
    }

    public void addCoordinate(Coordinates coordinate, Tile tile) {
        if (coordinates.add(coordinate)) { // Only update if the coordinate was not already present
            for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
                if (playerTileAmountContainer.getPlayer().equals(tile)) {
                    playerTileAmountContainer.incrementTileAmount();
                }
            }
        }

        if (coordinates.size() != Arrays.stream(playerTileAmountContainers)
                .mapToInt(PlayerTileAmountContainer::getTileAmount).sum()) {
            System.out.println(
                    this.coordinates.size() + " " + Arrays.toString(playerTileAmountContainers));
        }
    }

    public void addAllCoordinates(Set<Coordinates> newCoordinates, Game game) {
        Set<Coordinates> addedCoordinates = new HashSet<>();
        for (Coordinates coordinate : newCoordinates) {
            if (coordinates.add(coordinate)) { // Only add if it wasn't already present
                addedCoordinates.add(coordinate);
            }
        }
        // Update statistics only for newly added coordinates
        for (Coordinates coordinate : addedCoordinates) {
            for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
                if (playerTileAmountContainer.getPlayer().equals(game.getTile(coordinate))) {
                    playerTileAmountContainer.incrementTileAmount();
                }
            }
        }
    }

    public void removeCoordinate(Coordinates coordinate, Game game) {
        coordinates.remove(coordinate); // Only update if the coordinate was actually removed
        for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
            if (playerTileAmountContainer.getPlayer().equals(game.getTile(coordinate))) {
                playerTileAmountContainer.decrementTileAmount();
            }
        }
    }

    public void addAllCoordinatesFromCommunity(Community other, Game game) {
        Set<Coordinates> addedCoordinates = new HashSet<>();
        for (Coordinates coordinate : other.coordinates) {
            if (coordinates.add(coordinate)) { // Only add if it wasn't already present
                addedCoordinates.add(coordinate);
            }
        }
        // Update statistics only for newly added coordinates
        for (Coordinates coordinate : addedCoordinates) {
            for (PlayerTileAmountContainer ourPlayerTileAmountContainer :
                    playerTileAmountContainers) {
                if (ourPlayerTileAmountContainer.getPlayer().equals(game.getTile(coordinate))) {
                    ourPlayerTileAmountContainer.incrementTileAmount();
                }
            }
        }
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public int getTileAmountByPlayer(Tile playerValue) {
        for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
            if (playerTileAmountContainer.player.equals(playerValue)) {
                return playerTileAmountContainer.getTileAmount();
            }
        }
        return -1;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Overrides
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Community community = (Community) o;
        return Objects.equals(coordinates, community.coordinates) &&
                Arrays.equals(playerTileAmountContainers, community.playerTileAmountContainers);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(coordinates);
        result = 31 * result + Arrays.hashCode(playerTileAmountContainers);
        return result;
    }

    @Override
    public Community clone() {
        try {
            Community clone = (Community) super.clone();
            // Deep clone the mutable state
            clone.coordinates = new HashSet<>(this.coordinates);
            clone.playerTileAmountContainers =
                    new PlayerTileAmountContainer[this.playerTileAmountContainers.length];
            for (int i = 0; i < this.playerTileAmountContainers.length; i++) {
                clone.playerTileAmountContainers[i] = this.playerTileAmountContainers[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        String coordinatesString = coordinates.stream().map(Coordinates::toString)
                .collect(Collectors.joining(",\n    ", "[\n    ", "\n]"));

        String playerTileAmountsString =
                Arrays.stream(playerTileAmountContainers).map(PlayerTileAmountContainer::toString)
                        .collect(Collectors.joining(",\n    ", "[\n    ", "\n]"));

        return "Community {\n" + "  coordinates=" + coordinatesString + ",\n" +
                "  playerTileAmountContainers=" + playerTileAmountsString + "\n" + '}';
    }

}
