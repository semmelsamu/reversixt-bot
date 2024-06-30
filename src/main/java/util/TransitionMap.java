package util;

public class TransitionMap {

    private final short[] map;

    public TransitionMap() {
        map = new short[32767];
    }

    public void put(short key, short value) {
        map[key] = value;
    }

    public short get(short key) {
        return map[key];
    }
}
