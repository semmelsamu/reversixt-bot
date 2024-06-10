package game;

import board.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

public class Community {

    private Map<Integer, Set<Coordinates>> tilesPlayerPair;
    private boolean relevant;

    public Community() {}

    public Community(int player, Set<Coordinates> tilesPlayerPair) {
        this.tilesPlayerPair = new HashMap<>(Map.of(player, tilesPlayerPair));
        this.relevant = false;
    }

    public void addCoordinate(int playerId, Coordinates coordinate) {
        Set<Coordinates> coordinatesSet =
                tilesPlayerPair.computeIfAbsent(playerId, k -> new HashSet<>());
        coordinatesSet.add(coordinate);
    }

    public int findKeyByValue(Coordinates coordinates) {
        for (Map.Entry<Integer, Set<Coordinates>> integerSetEntry : tilesPlayerPair.entrySet()) {
            for (Coordinates coordinate : integerSetEntry.getValue()) {
                if (coordinate.equals(coordinates)) {
                    return integerSetEntry.getKey();
                }
            }
        }
        return -1;
    }

    public void removeCoordinate(Coordinates coordinate) {
        int playerId = findKeyByValue(coordinate);
        Set<Coordinates> coordinatesSet = tilesPlayerPair.get(playerId);

        if (coordinatesSet != null && coordinatesSet.remove(coordinate)) {
            if (coordinatesSet.isEmpty()) {
                tilesPlayerPair.remove(playerId);
            }
        }
    }


    public void addAllCoordinates(Community other) {
        for (Map.Entry<Integer, Set<Coordinates>> entry : other.tilesPlayerPair.entrySet()) {
            tilesPlayerPair.computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                    .addAll(entry.getValue());
        }
    }

    public Set<Coordinates> getAllCoordinates() {
        return tilesPlayerPair.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<Integer> getAllKeys(){
        return tilesPlayerPair.keySet();
    }

    public boolean foundKey(int playerId) {
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
}
