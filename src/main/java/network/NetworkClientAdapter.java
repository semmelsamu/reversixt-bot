package network;

import board.Coordinates;
import board.Tile;
import clients.Client;
import player.move.Bonus;
import player.move.BonusMove;
import player.move.ChoiceMove;
import player.move.Move;

public class NetworkClientAdapter implements NetworkClient {

    private final Client client;

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
            type = (byte) ((ChoiceMove) result).getPlayerToSwapWith().toPlayerIndex();
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

        if (type == 0) {
            client.receiveMove(new Move(playerTile, coordinates));
        } else if (type == 20 || type == 21) {
            Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
            client.receiveMove(new BonusMove(playerTile, coordinates, bonus));
        } else {
            Tile playerToSwapWith = uint8ToTile(type);
            client.receiveMove(new ChoiceMove(playerTile, coordinates, playerToSwapWith));
        }
    }

    @Override
    public void receiveDisqualification(byte player) {

    }

    @Override
    public void receiveEndingPhase1() {

    }

    @Override
    public void receiveEndingPhase2() {

    }

    public static Tile uint8ToTile(byte uint8) {
        return Tile.fromChar((char) (uint8 + '0'));
    }
}
