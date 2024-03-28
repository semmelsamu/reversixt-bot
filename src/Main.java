import board.Direction;
import game.Game;
import util.Logger;

public class Main {

    private static final String VERSION = "0.1";

    public static void main(String[] args) {
        Logger.LOGGER_NAME = "revxt-ss24-g04-v" + VERSION;
        Logger.DEBUG = true;
        Logger.log("Starting");

        Game game = Game.createFromFile("maps/initialMaps/window.map");
    }
}
