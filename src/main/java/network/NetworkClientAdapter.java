package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import game.Game;
import game.GameFactory;
import game.GamePhase;
import move.*;
import util.Logger;
import util.Triple;

/**
 * Adapts the primitive types of the server to internal data structures and function calls.
 */
public class NetworkClientAdapter {

    private final Logger logger = new Logger(this.getClass().getName());

    private Client client;

    /**
     * The time in milliseconds by which we want to respond earlier to avoid disqualification due to
     * network latency.
     */
    private static final int TIME_BUFFER = 1000;

    private Game game;
    private int playerNumber;

    public NetworkClientAdapter() {
    }

    public byte sendGroupNumber() {
        return 4;
    }

    public void receiveMap(String map) {
        this.game = GameFactory.createFromString(map);
        startClient();
    }

    public void receivePlayerNumber(byte playerNumber) {
        this.playerNumber = playerNumber;
        startClient();
    }

    /**
     * Check if the game and player number have been sent and if so, construct the client
     */
    private void startClient() {
        if (game != null && playerNumber != 0) {
            this.client = new Client(game, playerNumber);
        }
    }

    public Triple<Short, Short, Byte> sendMoveAnswer(int timeLimit, byte depthLimit) {

        // For performance’s sake we ignore depth limit and always use a time limit
        // If no time limit is given we default to 2 seconds as we are probably debugging
        int limit = timeLimit > 0 ? timeLimit - TIME_BUFFER : 2000;

        Move result = client.search(limit);

        logger.log("Sending " + result.getClass().getSimpleName() + result.getCoordinates());

        short x = (short) result.getCoordinates().x;
        short y = (short) result.getCoordinates().y;

        byte type = 0;

        if (result instanceof ChoiceMove) {
            type = (byte) (((ChoiceMove) result).getPlayerToSwapWith());
        }

        if (result instanceof BonusMove) {
            type = (byte) (((BonusMove) result).getBonus() == Bonus.BOMB ? 20 : 21);
        }

        return new Triple<>(x, y, type);
    }

    public void receiveMove(short x, short y, byte type, byte playerNumber) {

        Coordinates coordinates = new Coordinates(x, y);

        Move move;

        if (game.getPhase() == GamePhase.BUILD) {
            if (type == 0) {
                if (game.getTile(coordinates) == Tile.INVERSION) {
                    move = new InversionMove(playerNumber, coordinates);
                } else if (game.getTile(coordinates) == Tile.EMPTY) {
                    move = new NormalMove(playerNumber, coordinates);
                } else {
                    move = new OverwriteMove(playerNumber, coordinates);
                }
            } else if (type == 20 || type == 21) {
                Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
                move = new BonusMove(playerNumber, coordinates, bonus);
            } else {
                move = new ChoiceMove(playerNumber, coordinates, type);
            }
        } else {
            move = new BombMove(playerNumber, coordinates);
        }

        try {
            client.executeMove(move);
        }
        catch (Exception e) {
            // Only log but let it run. Maybe we'll get lucky and manage to sneak through.
            logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            client.logStats();
        }
    }

    public void receiveDisqualification(byte playerNumber) {
        client.disqualify(playerNumber);
        if (this.playerNumber == playerNumber) {
            logger.error("Client got disqualified");
        }
        client.logStats();
    }

    public void receiveEndingPhase1() {
        if (!game.getPhase().equals(GamePhase.BOMB)) {
            logger.warn("Server and client game phase are out of sync");
        }
        client.logStats();
    }

    public void receiveEndingPhase2() {
        if (!game.getPhase().equals(GamePhase.END)) {
            logger.warn("Server and client game phase are out of sync");
        }
        client.logStats();
    }
}
