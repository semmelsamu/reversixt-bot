package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import game.*;
import player.move.*;
import util.Logger;

public class NetworkClientAdapter implements NetworkClient {

    private final Logger logger = new Logger(this.getClass().getName());

    private final Client client;

    private Game game;
    private MoveExecutor moveExecutor;
    private int player;

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
        moveExecutor = new MoveExecutor(game);
    }

    @Override
    public void receivePlayerNumber(byte player) {
        this.player = player;
    }

    @Override
    public MoveAnswer sendMoveAnswer() {
        Move result = client.sendMove(game, player);

        short x = (short) result.getCoordinates().x;
        short y = (short) result.getCoordinates().y;

        byte type = 0;

        if (result instanceof ChoiceMove) {
            type = (byte) (((ChoiceMove) result).getPlayerToSwapWith() + 1);
        }

        if (result instanceof BonusMove) {
            type = (byte) (((BonusMove) result).getBonus() == Bonus.BOMB ? 20 : 21);
        }

        return new MoveAnswer(x, y, type);
    }

    @Override
    public void receiveMove(short x, short y, byte type, byte playerNumber) {

        Coordinates coordinates = new Coordinates(x, y);

        if (game.getPhase() == GamePhase.PHASE_1) {
            if (type == 0) {
                if (game.getTile(coordinates) != Tile.INVERSION) {
                    moveExecutor.executeMove(new NormalMove(playerNumber, coordinates));
                } else {
                    moveExecutor.executeMove(new InversionMove(playerNumber, coordinates));
                }
            } else if (type == 20 || type == 21) {
                Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
                moveExecutor.executeMove(new BonusMove(playerNumber, coordinates, bonus));
            } else {
                moveExecutor.executeMove(new ChoiceMove(playerNumber, coordinates, type));
            }
        } else {
            moveExecutor.executeMove(new BombMove(playerNumber, coordinates));
        }

        logger.verbose(game.toString());
    }

    @Override
    public void receiveDisqualification(byte player) {
        game.getPlayer(player).disqualify();
    }

    @Override
    public void receiveEndingPhase1() {
        if(game.getPhase() != GamePhase.PHASE_2) {
            logger.warn("Server and client game phase do not match");
        }
    }

    @Override
    public void receiveEndingPhase2() {
        if(game.getPhase() != GamePhase.END) {
            logger.warn("Server and client game phase do not match");
        }
    }

    public Game getGame(){
        return this.game;
    }
}
