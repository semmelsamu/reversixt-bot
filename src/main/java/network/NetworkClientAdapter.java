package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import exceptions.GamePhaseNotValidException;
import exceptions.MoveNotValidException;
import game.Game;
import game.GameFactory;
import game.GamePhase;
import move.*;
import util.Logger;

import java.util.LinkedList;
import java.util.List;

public class NetworkClientAdapter implements NetworkClient {

    private final Logger logger = new Logger(this.getClass().getName());

    private final Client client;

    private Game game;

    private List<Game> pastGameStates;

    /**
     * Adapt a Client to work with the NetworkClient aka the NetworkEventHandler, which
     * communicates with the Server.
     */
    public NetworkClientAdapter(Client client) {
        this.client = client;
        logger.log("Starting " + client.getClass().getSimpleName());
    }

    @Override
    public int sendGroupNumber() {
        return 4;
    }

    @Override
    public void receiveMap(String map) {
        this.game = GameFactory.createFromString(map);
        logger.log(game.toString());
        client.setGame(game);
        pastGameStates = new LinkedList<>();
        storeGame();
    }

    @Override
    public void receivePlayerNumber(byte player) {
        client.setPlayer(player);
    }

    @Override
    public MoveAnswer sendMoveAnswer(int timeLimit, byte depthLimit) {
        try {
            Limit limitType;
            int limit;

            if (timeLimit > 0) {
                limitType = Limit.TIME;
                limit = timeLimit;
            } else {
                limitType = Limit.DEPTH;
                limit = depthLimit;
            }

            if (game.getValidMovesForCurrentPlayer().isEmpty()) {
                throw new MoveNotValidException("No valid moves!");
            }

            if (game.getPhase() == GamePhase.END) {
                throw new GamePhaseNotValidException(
                        "Move was requested but we think the game already ended");
            }

            logger.log("Calculating new move with " + limitType + " limit " + limit);

            Move result = client.sendMove(limitType, limit);

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

            return new MoveAnswer(x, y, type);

        } catch (Exception e) {
            logPastGames();
            throw e;
        }
    }

    @Override
    public void receiveMove(short x, short y, byte type, byte playerNumber) {
        storeGame();
        try {
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

            game.executeMove(move);

        } catch (Exception e) {
            logPastGames();
            throw e;
        }
    }

    @Override
    public void receiveDisqualification(byte player) {
        game.disqualifyPlayer(player);
        if (client.getME() == player) {
            logPastGames();
            throw new RuntimeException("Client got disqualified");
        }
    }

    @Override
    public void receiveEndingPhase1() {
        logPastGames();
        if (game.getPhase() != GamePhase.BOMB) {
            logger.warn("Server and client game phase may not match.");
        }
    }

    @Override
    public void receiveEndingPhase2() {
        if (game.getPhase() != GamePhase.END) {
            logger.warn("Server and client game phase do not match.");
        }
    }

    /**
     * Gets called if either the game is finished or the client got disconnected. This is the last
     * time a client may execute any logic.
     */
    @Override
    public void exit() {
        logger.log("Exiting " + client.getClass().getSimpleName());
        client.exit();
    }

    /**
     * Hold the last x game states in memory. Used for logging.
     */
    private void storeGame() {
        pastGameStates.add(game.clone());
        if (pastGameStates.size() > 10) {
            pastGameStates.remove(0);
        }
    }

    private void logPastGames() {
        StringBuilder stringBuilder = new StringBuilder("Past Game states:");
        for (Game game : pastGameStates) {
            stringBuilder.append("\n").append(game.toString());
        }
        stringBuilder.append("\nCurrent Game:").append(game);
        logger.debug(stringBuilder.toString());
    }
}
