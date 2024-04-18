package clients.local;

import player.move.Move;

public interface LocalClient {

    public void receiveMap(String map);

    public void receivePlayerNumber(byte player);

    public Move sendMove();

    public void receiveMove(Move move);

}
