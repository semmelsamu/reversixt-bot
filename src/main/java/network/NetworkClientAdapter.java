package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import game.Game;
import game.GameFactory;
import game.GamePhase;
import game.MoveExecutor;
import move.*;
import util.Logger;

public class NetworkClientAdapter implements NetworkClient {

    private final Logger logger = new Logger(this.getClass().getName());

    private final Client client;

    private Game game;
    private int player;

    private MoveExecutor moveExecutor;

    /**
     * Adapt a Client to work with the NetworkClient aka the NetworkEventHandler, which
     * communicates with the Server.
     */
    public NetworkClientAdapter(Client client) {
        this.client = client;
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
    }

    @Override
    public void receivePlayerNumber(byte player) {
        this.player = player;
        client.setPlayer(player);
    }

    @Override
    public MoveAnswer sendMoveAnswer(int timeLimit, byte depthLimit) {
        checkPhase();

        Move result = client.sendMove(timeLimit, depthLimit);

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
    }

    @Override
    public void receiveMove(short x, short y, byte type, byte playerNumber) {
        checkPhase();

        Coordinates coordinates = new Coordinates(x, y);

        Move move = null;

        if (game.getPhase() == GamePhase.PHASE_1) {
            if (type == 0) {
                if (game.getTile(coordinates) == Tile.INVERSION) {
                    move = new InversionMove(playerNumber, coordinates);
                } else if(game.getTile(coordinates) == Tile.EMPTY) {
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

        logger.verbose(game.toString());
    }

    @Override
    public void receiveDisqualification(byte player) {
        game.disqualifyPlayer(player);
        logger.log(game.toString());
    }

    @Override
    public void receiveEndingPhase1() {
        if (game.getPhase() != GamePhase.PHASE_2) {
            logger.log("Server and client game phase may not match. " +
                    "Waiting for next server message.");
            checkPhase = true;
        }
        logger.log(game.toString());
    }

    /**
     * Used to get rid of the Warning that appears in the edge case where there are no possible
     * actions in the bomb phase, and it gets skipped. The server will then send 2 messages, first
     * entering the bomb phase and then the end. On every message we check if the client game phase
     * matches the server phase.
     */
    private boolean checkPhase = false;
    private void checkPhase() {
        if(checkPhase) {
            logger.warn("Server and client game phase do not match.");
        }
    }

    @Override
    public void receiveEndingPhase2() {
        checkPhase = false;
        if (game.getPhase() != GamePhase.END) {
            logger.warn("Server and client game phase do not match");
        }
        logger.log(game.toString());
    }

    public Game getGame() {
        return this.game;
    }
}
