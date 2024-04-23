package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import game.GamePhase;
import player.move.*;

public class NetworkClientAdapter implements NetworkClient {

    private final Client client;
    private GamePhase currentPhase;

    /**
     * Adapt a Client to work with the NetworkClient aka the NetworkEventHandler, which
     * communicates with the Server.
     */
    public NetworkClientAdapter(Client client) {
        this.client = client;
        currentPhase = GamePhase.PHASE_1;
    }

    @Override
    public int sendGroupNumber() {
        return 4;
    }

    @Override
    public void receiveMap(String map) {
        client.receiveMap(map);
    }

    @Override
    public void receivePlayerNumber(byte player) {
        client.receivePlayerNumber(uint8ToTile(player));
    }

    @Override
    public MoveAnswer sendMoveAnswer() {
        Move result = client.sendMove();

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

        if (currentPhase == GamePhase.PHASE_1) {
            if (type == 0) {
                client.receiveMove(new Move(playerTile, coordinates));
            } else if (type == 20 || type == 21) {
                Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
                client.receiveMove(new BonusMove(playerTile, coordinates, bonus));
            } else {
                Tile playerToSwapWith = uint8ToTile(type);
                client.receiveMove(new ChoiceMove(playerTile, coordinates, playerToSwapWith));
            }
        } else {
            client.receiveMove(new BombMove(playerTile, coordinates));
        }
    }

    @Override
    public void receiveDisqualification(byte player) {

    }

    @Override
    public void receiveEndingPhase1() {
        client.updateGamePhase(GamePhase.PHASE_2);
        currentPhase = GamePhase.PHASE_2;
    }

    @Override
    public void receiveEndingPhase2() {
        client.updateGamePhase(GamePhase.END);
    }

    public static Tile uint8ToTile(byte uint8) {
        return Tile.fromChar((char) (uint8 + '0'));
    }
}
