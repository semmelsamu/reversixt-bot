package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import game.*;
import player.move.*;

public class NetworkClientAdapter implements NetworkClient {

    private final Client client;

    private Game game;
    private MoveExecutor moveExecutor;
    private Tile player;

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
        this.player = uint8ToTile(player);
    }

    @Override
    public MoveAnswer sendMoveAnswer() {
        Move result = client.sendMove(game, player);

        short x = (short) result.getCoordinates().x;
        short y = (short) result.getCoordinates().y;

        byte type = 0;

        if (result instanceof ChoiceMove) {
            type = (byte) (((ChoiceMove) result).getPlayerToSwapWith().toPlayerIndex() + 1);
        }

        if (result instanceof BonusMove) {
            type = (byte) (((BonusMove) result).getBonus() == Bonus.BOMB ? 20 : 21);
        }

        return new MoveAnswer(x, y, type);
    }

    @Override
    public void receiveMove(short x, short y, byte type, byte player) {

        Tile playerTile = uint8ToTile(player);
        Coordinates coordinates = new Coordinates(x, y);

        if (game.getGamePhase() == GamePhase.PHASE_1) {
            if (type == 0) {
                if(game.getTile(coordinates) != Tile.INVERSION) {
                    moveExecutor.executeMove(new Move(playerTile, coordinates));
                }
                else {
                    moveExecutor.executeMove(new InversionMove(playerTile, coordinates));
                }
            } else if (type == 20 || type == 21) {
                Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
                moveExecutor.executeMove(new BonusMove(playerTile, coordinates, bonus));
            } else {
                Tile playerToSwapWith = uint8ToTile(type);
                moveExecutor.executeMove(new ChoiceMove(playerTile, coordinates, playerToSwapWith));
            }
        } else {
            moveExecutor.executeMove(new BombMove(playerTile, coordinates));
        }
    }

    @Override
    public void receiveDisqualification(byte player) {

    }

    @Override
    public void receiveEndingPhase1() {
        game.setGamePhase(GamePhase.PHASE_2);
    }

    @Override
    public void receiveEndingPhase2() {
        game.setGamePhase(GamePhase.END);
    }

    public static Tile uint8ToTile(byte uint8) {
        return Tile.fromChar((char) (uint8 + '0'));
    }
}
