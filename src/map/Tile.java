package map;

import util.Logger;

import java.util.ArrayList;
import java.util.List;

public class MapTile {

    private TileType type;

    private final Coordinates position;

    // TODO: refactor to set
    private List<Transition> transitions;

    public MapTile(TileType type, Coordinates position) {
        this.type = type;
        this.position = position;
        transitions = new ArrayList<>();
    }

    public void addTransition(Transition newTransition) {

        for (Transition transition : transitions) {
            if (transition.outgoingDirection().getValue() == newTransition.outgoingDirection().getValue()) {
                Logger.warning("A tile should only have one transition in every direction.");
                return;
            }
        }

        // TODO: fix swapped coordinates on transition positions
        this.transitions.add(newTransition);

        Logger.verbose("Registered transition: " + newTransition);
    }

    public TileType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MapTile[position=" + position + ", type=" + type + "]";
    }
}