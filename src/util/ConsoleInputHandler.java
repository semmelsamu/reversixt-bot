package util;

import player.Player;
import player.move.Bonus;
import player.move.Move;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input actions
 */
public class ConsoleInputHandler {

    static Scanner s = new Scanner(System.in);

    /**
     * Create a Move out of x and y coordinates from the console
     *
     * @param player {@link Player}
     * @return new {@link Move}
     */
    public static Move selectMove(Player player) {
        System.out.print(player.getPlayerValue() + " - Gib eine Aktion an (x): ");
        int xCor = s.nextInt();
        System.out.print(player.getPlayerValue() + " -  Gib eine Aktion an (y): ");
        int yCor = s.nextInt();

        return player.getValidMoves().stream().filter(move -> move.getTile().getPosition().x == xCor && move.getTile().getPosition().y == yCor).findFirst().orElse(null);
    }

    public static Bonus handleBonus(Player player) {
        char bonus = 'a';

        while (!(bonus == 'b' || bonus == 'u')) {
            System.out.print(player.getPlayerValue() + " - Wähle zwischen einer Bombe und einem Ueberschreibstein (b/u): ");
            bonus = s.next().charAt(0);
        }
        return bonus == 'b' ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
    }

    public static int handleChoice(Player[] players, Player currentPlayer) {

        System.out.print(currentPlayer.getPlayerValue() + " - Wähle zwischen einen Spieler (");
        Arrays.stream(players).forEach(e -> System.out.print(e.getPlayerValue().character + "/"));
        System.out.print("): ");

        return s.nextInt() - 1;
    }
}
