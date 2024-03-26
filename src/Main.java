import map.Board;
import util.Logger;

public class Main {

    private static final String VERSION = "0.1";

    public static void main(String[] args) {
        Logger.LOGGER_NAME = "revxt-ss24-g04v" + VERSION;
        Logger.DEBUG = true;
        Logger.log("Starting...");

        Board map = Board.constructFromFile("maps/example.map");
    }
}
