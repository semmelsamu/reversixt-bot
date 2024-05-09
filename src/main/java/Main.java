import clients.OptimizedParanoidClient;
import clients.ParanoidClient;
import exercises.*;
import game.Game;
import util.CommandLineArguments;
import util.CommandLineArguments.Argument;
import util.Logger;

public class Main {
    public static void main(String[] args) {

        assert 1 == 1;

        CommandLineArguments cla = new CommandLineArguments(args);

        Logger.useColors = cla.getBoolean(Argument.LOGGER_USE_COLORS);
        Logger.defaultPriority = cla.getInt(Argument.LOGGER_DEFAULT_PRIORITY);
        Logger.setPriority(Game.class.getName(), 3);
        Logger.setPriority(ParanoidClient.class.getName(), 1);
        Logger.setPriority(OptimizedParanoidClient.class.getName(), 1);

        Logger.get().log("Arguments: \"" + String.join(" ", args) + "\"");

        // Launch whatever

        switch (cla.getInt(Argument.EXERCISE)) {
            case 1 -> Exercise01.aufgabe3();
            case 2 -> Exercise02.aufgabe3();
            case 3 -> Exercise04.abnahme(cla.getString(Argument.IP), cla.getInt(Argument.PORT));
            case 5 -> Exercise05.abnahme(cla.getString(Argument.IP), cla.getInt(Argument.PORT));
            default -> Exercise06.abnahme(cla.getString(Argument.IP), cla.getInt(Argument.PORT));
        }

    }
}