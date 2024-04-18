package clients.local;

import board.Coordinates;
import board.Tile;
import clients.network.MoveAnswer;
import clients.network.NetworkClient;
import player.move.Bonus;
import player.move.BonusMove;
import player.move.ChoiceMove;
import player.move.Move;

public class LocalClientToNetworkClientAdapter implements NetworkClient {

    private final LocalClient localClient;

    public LocalClientToNetworkClientAdapter(LocalClient localClient) {
        this.localClient = localClient;
    }

    @Override
    public int sendGroupNumber() {
        return 4;
    }

    @Override
    public void receiveMap(String map) {
        localClient.receiveMap(map);
    }

    @Override
    public void receivePlayerNumber(byte player) {
        localClient.receivePlayerNumber(Tile.fromChar((char) ((int) player)));
    }

    @Override
    public MoveAnswer sendMoveAnswer() {
        Move result = localClient.sendMove();

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

        Tile playerTile = Tile.fromChar((char) ((int) player));
        Coordinates coordinates = new Coordinates(x, y);

        if (type == 0) {
            localClient.receiveMove(new Move(playerTile, coordinates));
        }
        else if (type == 20 || type == 21) {
            Bonus bonus = type == 20 ? Bonus.BOMB : Bonus.OVERWRITE_STONE;
            localClient.receiveMove(new BonusMove(playerTile, coordinates, bonus));
        }
        else {
            Tile playerToSwapWith = Tile.fromChar((char) ((int) type));
            localClient.receiveMove(new ChoiceMove(playerTile, coordinates, playerToSwapWith));
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
}
