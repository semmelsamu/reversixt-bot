package util;

import board.Coordinates;
import board.Tile;
import game.Game;
import player.Player;
import player.move.*;
import java.util.Scanner;

/**
 * Handles all console input actions
 */
public class ConsoleInputHandler {

    static String color = "\u001B[34m"; // Blue
    static String neutral = "\u001B[0m"; // Neutral

    static Scanner scanner = new Scanner(System.in);

    public static String selectMap() {
        Logger.get().log("Selecting map");
        return (String) selectOption("Which map do you want to load? Enter the number.", File.getAllMaps().toArray());
    }

    public static Move selectMove(Game game) {
        Logger.get().log("Selecting move");

        Player player = selectOption("Which player should execute this move?", game.getPlayers());

        System.out.print(color + "\nEnter the x coordinate.\n> ");
        int x = scanner.nextInt();
        System.out.print(color + "\nEnter the y coordinate.\n> ");
        int y = scanner.nextInt();
        Tile tile = game.getTile(new Coordinates(x, y));

        enum MoveType { MOVE, BONUS_MOVE, CHOICE_MOVE, INVERSION_MOVE };
        switch (selectOption("Which kind of move should it be?", MoveType.values())) {
            case BONUS_MOVE -> {
                Bonus bonus = selectOption("Which bonus do you wish?", Bonus.values());
                return new BonusMove(player, tile, bonus);
            }
            case CHOICE_MOVE -> {
                Player playerToSwapWith = selectOption("Which player do you wish to swap stones with?", game.getPlayers());
                return new ChoiceMove(player, tile, playerToSwapWith);
            }
            case INVERSION_MOVE -> {
                return new InversionMove(player, tile);
            }
            default -> {
                return new Move(player, tile);
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | Util
    |--------------------------------------------------------------------------
    */

    private static <T> T selectOption(String prompt, T[] options) {
        System.out.println("\n" + color + prompt);

        for(int i = 0; i < options.length; i++) {
            System.out.println(color + i + " - " + options[i]);
        }

        try {
            System.out.print(neutral + "> ");
            int option = scanner.nextInt();

            T result = options[option];
            System.out.println(color + "Selected option " + option + " (" + result.toString() + ")");

            return result;
        }
        catch (Exception e) {
            Logger.get().fatal("Failed to select option");
            throw e;
        }
    }
}
