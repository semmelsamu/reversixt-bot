package exercises;

import game.Game;
import game.GameFactory;
import util.Logger;

public class Exercise08 {

    public static void abnahme() {

        // Load map
        Game game = GameFactory.createFromFile("maps/ue08-bomben/bomben01.map");

        // Print board
        Logger.get().log(game.toString());

    }

}
