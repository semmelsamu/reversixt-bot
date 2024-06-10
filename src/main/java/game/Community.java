package game;

import board.Coordinates;
import board.Tile;

import java.util.*;
import java.util.stream.Collectors;

public class Community implements Cloneable{

    private Map<Tile, Set<Coordinates>> tilesPlayerPair;
    private boolean relevant;

    public Community() {
        this.tilesPlayerPair = new HashMap<>();
        this.relevant = false;
    }

    public Community(Tile player, Set<Coordinates> tilesPlayerPair) {
        this();
        this.tilesPlayerPair.put(player, new HashSet<>(tilesPlayerPair));
    }

    public void addCoordinate(Tile playerId, Coordinates coordinate) {
        Set<Coordinates> coordinatesSet =
                tilesPlayerPair.computeIfAbsent(playerId, k -> new HashSet<>());
        coordinatesSet.add(coordinate);
    }

    public Tile findKeyByValue(Coordinates coordinates) {
        for (Map.Entry<Tile, Set<Coordinates>> integerSetEntry : tilesPlayerPair.entrySet()) {
            for (Coordinates coordinate : integerSetEntry.getValue()) {
                if (coordinate.equals(coordinates)) {
                    return integerSetEntry.getKey();
                }
            }
        }
        return null;
    }

    public void removeCoordinate(Coordinates coordinate) {
        Tile playerId = findKeyByValue(coordinate);
        Set<Coordinates> coordinatesSet = tilesPlayerPair.get(playerId);

        if (coordinatesSet != null && coordinatesSet.remove(coordinate)) {
            if (coordinatesSet.isEmpty()) {
                tilesPlayerPair.remove(playerId);
            }
        }
    }


    public void addAllCoordinates(Community other) {
        for (Map.Entry<Tile, Set<Coordinates>> entry : other.tilesPlayerPair.entrySet()) {
            tilesPlayerPair.computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                    .addAll(entry.getValue());
        }
    }

    public Set<Coordinates> getAllCoordinates() {
        return tilesPlayerPair.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<Tile> getAllKeys(){
        return tilesPlayerPair.keySet();
    }

    public boolean foundKey(Tile playerId) {
        return tilesPlayerPair.get(playerId) != null;
    }

    public boolean isRelevant() {
        return relevant;
    }

    public void setRelevant(boolean relevant) {
        this.relevant = relevant;
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
        return Objects.equals(tilesPlayerPair, community.tilesPlayerPair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tilesPlayerPair);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Community{")
                .append("relevant=").append(relevant)
                .append(", tilesPlayerPair=");

        tilesPlayerPair.forEach((player, coordinatesSet) -> {
            sb.append("\n    Player: ").append(player)
                    .append(", Coordinates: ").append(coordinatesSet);
        });

        sb.append("\n}");
        return sb.toString();
    }


    @Override
    public Community clone() {
        try {
            Community clone = (Community) super.clone();
            clone.tilesPlayerPair = new HashMap<>();
            for (Map.Entry<Tile, Set<Coordinates>> entry : this.tilesPlayerPair.entrySet()) {
                clone.tilesPlayerPair.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
