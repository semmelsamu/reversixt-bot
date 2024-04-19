package util;

public class LoggerUtils {

    public static String getTextForMessageType(int messageType) {

        StringBuilder result = new StringBuilder();

        switch (messageType) {
            case 1 -> result.append("Client sends group name");
            case 2 -> result.append("Server sends map");
            case 3 -> result.append("Server assigns client a player number");
            case 4 -> result.append("Server requests move from client");
            case 5 -> result.append("Client sends move");
            case 6 -> result.append("Server sends move from other player");
            case 7 -> result.append("Server disqualifies");
            case 8 -> result.append("End of phase 1");
            case 9 -> result.append("End of phase 2 (The end)");
            default -> result.append("Unknown message type");
        }

        result.append(" (Type ").append(messageType).append(")");

        return result.toString();
    }

}
