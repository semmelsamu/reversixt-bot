package exercises;

import game.Game;
import game.GameFactory;
import util.Logger;

public class Exercise03 {

    public static void aufgabe3() {
        Game game = GameFactory.createFromFile("maps/initialMaps/heartExercise3.map");
        Logger.get().log(game.toString());

        game.evaluateCurrentPlayer();

        game.nextPlayer();

        game.evaluateCurrentPlayer();
    }
}
