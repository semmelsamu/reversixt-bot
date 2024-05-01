package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandLineArguments {

    public enum Arguments {

        IP("i", "127.0.0.1"),
        PORT("p", 7777),
        DEPTH("d", 3),

        EXERCISE("e", 0),
        LOGGER_DEFAULT_PRIORITY("l", 2),
        LOGGER_USE_COLORS("c", false);


        public final String name;
        public final Object defaultValue;

        Arguments(String name, Object fallback) {
            this.name = name;
            this.defaultValue = fallback;
        }

        public static String[] getAllNames() {
            return Arrays.stream(values()).map(argument -> argument.name).toArray(String[]::new);
        }
    }

    Map<String, String> values;

    public CommandLineArguments(String[] args) {

        values = new HashMap<>();

        if (args.length == 0) {
            return;
        }

        if (args[0].equals("-h")) {
            System.out.println("Usage:");
            for (var argument : Arguments.values()) {
                System.out.println("-" + argument.name + "\t" + argument + "\t(Default: " +
                        argument.defaultValue + ")");
            }

            System.exit(0);
        }

        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be of type <argument> <value> ...");
        }

        for (int i = 0; i < args.length; i += 2) {

            String name = args[i];
            String value = args[i + 1];

            if (name.charAt(0) != '-') {
                throw new IllegalArgumentException("Argument names must start with a dash '-'");
            }

            name = name.substring(1);

            // Check if given argument exists in defined arguments
            if (!Arrays.asList(Arguments.getAllNames()).contains(name)) {
                throw new IllegalArgumentException("Argument name " + name + " is unknown");
            }

            values.put(name, value);

        }
    }

    public Object get(Arguments argument) {
        if (values.containsKey(argument.name)) {
            return values.get(argument.name);
        } else {
            return argument.defaultValue;
        }
    }

    public String getString(Arguments argument) {
        return get(argument).toString();
    }

    public int getInt(Arguments argument) {
        return Integer.parseInt(getString(argument));
    }

    public boolean getBoolean(Arguments argument) {
        return Boolean.parseBoolean(getString(argument));
    }

}
