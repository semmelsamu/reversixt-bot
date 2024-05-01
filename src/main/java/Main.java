import exercises.Exercise01;
import exercises.Exercise02;
import exercises.Exercise04;
import exercises.Exercise05;
import game.Game;
import util.CommandLineArguments;
import util.CommandLineArguments.Argument;
import util.Logger;

public class Main {
    public static void main(String[] args) {

        CommandLineArguments cla = new CommandLineArguments(args);

        Logger.useColors = cla.getBoolean(Argument.LOGGER_USE_COLORS);
        Logger.defaultPriority = cla.getInt(Argument.LOGGER_DEFAULT_PRIORITY);
        Logger.setPriority(Game.class.getName(), 3);

        // Launch whatever

        switch (cla.getInt(Argument.EXERCISE)) {
            case 1 -> Exercise01.aufgabe3();
            case 2 -> Exercise02.aufgabe3();
            case 3 -> Exercise04.abnahme(cla.getString(Argument.IP), cla.getInt(Argument.PORT));
            default -> Exercise05.abnahme(cla.getString(Argument.IP), cla.getInt(Argument.PORT),
                    cla.getInt(Argument.DEPTH));
        }

    }
}