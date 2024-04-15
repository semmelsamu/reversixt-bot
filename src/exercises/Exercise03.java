package exercises;

import game.Game;
import game.GameFactory;
import util.Logger;

public class Exercise03 {

    public static void aufgabe3() {
        Game game = GameFactory.createFromFile("maps/initialMaps/heartExercise3.map");
        game.getPlayers()[0].incrementOverwriteStone();
        game.getPlayers()[1].decrementOverwriteStones();
        game.getPlayers()[0].incrementBombs();


        Logger.get().log(game.toString());
        game.evaluateCurrentPlayer();

        game.nextPlayer();

        game.evaluateCurrentPlayer();
    }
}
