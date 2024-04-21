package util;

import java.util.HashMap;
import java.util.Map;

public class CommandLineArguments {

    public static Map<String, String> parse(String[] args) {
        Map<String, String> result = new HashMap<>();

        for(int i = 0; i < args.length; i+=2) {
            result.put(args[i], args[i+1]);
        }

        Logger.get().log("Arguments: " + result.toString());

        return result;
    }

}
