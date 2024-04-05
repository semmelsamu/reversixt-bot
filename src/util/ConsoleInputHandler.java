package util;

import board.Coordinates;
import game.Game;
import player.move.Move;

import java.util.Scanner;

/**
 * Handles all console input actions
 */
public class ConsoleInputHandler {

    /**
     * Create a Move out of x and y coordinates from the console
     *
     * @param game {@link Game}
     * @return new {@link Move}
     */
    public static Move createMove(Game game) {
        Scanner s = new Scanner(System.in);
        System.out.print("Gib eine Aktion an (x): ");
        int x = s.nextInt();
        System.out.print("Gib eine Aktion an (y): ");
        int y = s.nextInt();
        s.close();
        return new Move(game.getCurrentPlayer(), game.getBoard().getTile(new Coordinates(x, y)));
    }
}
