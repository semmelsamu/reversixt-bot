package util;

import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {

    public record Parameter(
            String name,
            Object defaultValue
    ) {
    }

    private final Map<String, Parameter> parameters = new HashMap<>();

    public void setParameter(String key, Parameter value) {
        parameters.put(key, value);
    }

    public class ParsedArguments {

        private final Map<String, Object> arguments;

        public ParsedArguments() {
            this.arguments = new HashMap<>();
        }

        public void set(String argument, Object value) {
            arguments.put(argument, value);
        }

        public Object get(String argument) {
            return arguments.get(argument);
        }

        @Override
        public String toString() {
            return "ParsedArguments{" + "arguments=" + arguments + '}';
        }
    }

    public ParsedArguments parse(String[] args) {

        ParsedArguments result = new ParsedArguments();

        // Setting default values
        for (var parameter : parameters.entrySet()) {
            result.set(parameter.getKey(), parameter.getValue().defaultValue);
        }

        if (args.length == 0) {
            return result;
        }

        int i = 0;

        while (i < args.length) {
            if (args[i].charAt(0) != '-') {
                throw new IllegalArgumentException("Argument names must start with a dash '-'");
            }

            String name = args[i].substring(1);
            i++;

            // Check if given argument exists in defined arguments
            if (!parameters.containsKey(name)) {
                throw new IllegalArgumentException("Argument name " + name + " is unknown");
            }

            Object defaultValue = parameters.get(name).defaultValue;
            Object parsedValue = null;

            if (defaultValue instanceof Boolean) {
                parsedValue = !(Boolean) defaultValue;
            } else {
                String value = args[i];
                i++;

                if (defaultValue instanceof Integer) {
                    parsedValue = Integer.parseInt(value);
                } else if (defaultValue instanceof String) {
                    parsedValue = value;
                }
            }

            if (parsedValue == null) {
                throw new RuntimeException("Could not parse argument");
            }

            result.set(name, parsedValue);
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder result =
                new StringBuilder("\n").append(StringUtil.fillString("Arguments:", 29))
                        .append("Default Values:\n\n");

        for (var parameter : parameters.entrySet()) {
            result.append(StringUtil.fillString("-" + parameter.getKey(), 5)).append("  ");
            result.append(StringUtil.fillString(parameter.getValue().name, 20)).append("  ");
            result.append(parameter.getValue().defaultValue).append("\n");
        }

        return result.toString();
    }
}
