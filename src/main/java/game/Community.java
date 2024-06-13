package game;

import board.Coordinates;
import board.Tile;

import java.util.*;

public class Community implements Cloneable {

    private final Game game;
    private Set<Coordinates> coordinates;
    private PlayerTileAmountContainer[] playerTileAmountContainers;

    public Community(Game game) {
        this.game = game;
        this.coordinates = new HashSet<>();

        int playersLength = game.getPlayers().length;
        this.playerTileAmountContainers = new PlayerTileAmountContainer[playersLength + 1];
        for (int i = 0; i < playersLength; i++) {
            for (Player player : game.getPlayers()) {
                playerTileAmountContainers[i] =
                        new PlayerTileAmountContainer(player.getPlayerValue(), 0);
            }
        }
        playerTileAmountContainers[playersLength] =
                new PlayerTileAmountContainer(Tile.EXPANSION, 0);
    }

    public void addCoordinate(Coordinates coordinate) {
        coordinates.add(coordinate);
        for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
            if (playerTileAmountContainer.getPlayer() == game.getTile(coordinate)) {
                playerTileAmountContainer.incrementTileAmount();
            }
        }
    }

    public void addAllCoordinates(Set<Coordinates> coordinates) {
        this.coordinates.addAll(coordinates);
        for (Coordinates coordinate : coordinates) {
            for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
                if (playerTileAmountContainer.getPlayer() == game.getTile(coordinate)) {
                    playerTileAmountContainer.incrementTileAmount();
                }
            }
        }
    }

    public void removeCoordinate(Coordinates coordinate) {
        coordinates.remove(coordinate);
        for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
            if (playerTileAmountContainer.getPlayer() == game.getTile(coordinate)) {
                playerTileAmountContainer.decrementTileAmount();
            }
        }
    }

    public void addAllCoordinatesFromCommunity(Community other) {
        coordinates.addAll(other.coordinates);
        for (PlayerTileAmountContainer ourPlayerTileAmountContainer : playerTileAmountContainers) {
            for (PlayerTileAmountContainer otherPlayerTileAmountContainer :
                    other.playerTileAmountContainers) {
                if (ourPlayerTileAmountContainer.getPlayer() ==
                        otherPlayerTileAmountContainer.getPlayer()) {
                    ourPlayerTileAmountContainer.incrementTileAmountByValue(
                            otherPlayerTileAmountContainer.getTileAmount());
                }
            }
        }
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public int getTileAmountByPlayer(Tile playerValue) {
        for (PlayerTileAmountContainer playerTileAmountContainer : playerTileAmountContainers) {
            if (playerTileAmountContainer.player == playerValue) {
                return playerTileAmountContainer.getTileAmount();
            }
        }
        return -1;
    }

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
                Objects.equals(playerTileAmountContainers, community.playerTileAmountContainers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, playerTileAmountContainers);
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

    static class PlayerTileAmountContainer implements Cloneable {
        private final Tile player;
        private int tileAmount;

        public PlayerTileAmountContainer(Tile player, int tileAmount) {
            this.player = player;
            this.tileAmount = tileAmount;
        }

        public Tile getPlayer() {
            return player;
        }

        public int getTileAmount() {
            return tileAmount;
        }

        public void incrementTileAmountByValue(int value) {
            tileAmount += value;
        }

        public void incrementTileAmount() {
            tileAmount++;
        }

        public void decrementTileAmount() {
            tileAmount--;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PlayerTileAmountContainer that = (PlayerTileAmountContainer) o;
            return tileAmount == that.tileAmount && Objects.equals(player, that.player);
        }

        @Override
        public int hashCode() {
            return Objects.hash(player, tileAmount);
        }

        @Override
        protected PlayerTileAmountContainer clone() throws CloneNotSupportedException {
            try {
                return (PlayerTileAmountContainer) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
