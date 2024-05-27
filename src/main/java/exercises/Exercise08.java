package exercises;

import board.Coordinates;
import game.Game;
import game.GameFactory;
import move.BombMove;
import util.Logger;

public class Exercise08 {

    public static void main(String[] args) {
        abnahme();
    }

    public static void abnahme() {

        // Load map
        Game game = GameFactory.createFromFile("maps/ue08-bomben/bomben01.map");
        game.nextPlayer();

        // Print board
        Logger.get().log(game.toString());

        game.executeMove(new BombMove(2, new Coordinates(4, 2)));

        // Print board
        Logger.get().log(game.toString());

    }

}
