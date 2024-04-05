package util;

import board.Coordinates;
import game.Game;
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
     * @param game {@link Game}
     * @return new {@link Move}
     */
    public static Move createMove(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        System.out.print(currentPlayer.getPlayerValue() + " - Gib eine Aktion an (x): ");
        int x = s.nextInt();
        System.out.print(currentPlayer.getPlayerValue() + " -  Gib eine Aktion an (y): ");
        int y = s.nextInt();

        return new Move(currentPlayer, game.getBoard().getTile(new Coordinates(x, y)));
    }

    public static Bonus handleBonus(Player player) {
        char bonus = 'a';

        while(!(bonus == 'b' || bonus == 'u')) {
            System.out.print(player.getPlayerValue() + " - Wähle zwischen einer Bombe und einem Ueberschreibstein (b/u): ");
            bonus = s.next().charAt(0);
        }
        return bonus == 'b' ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
    }

    public static int handleChoice(Game game) {
        Player[] players = game.getPlayers();

        List<Player> list = Arrays.stream(players).filter(p -> p != players[game.getCurrentPlayerIndex()]).toList();
        System.out.print(game.getCurrentPlayer().getPlayerValue() + " - Wähle zwischen einen Spieler (");
        list.forEach(e -> System.out.print(e.getPlayerValue().character + "/"));
        System.out.print("):");

        return s.nextInt();
    }
}
