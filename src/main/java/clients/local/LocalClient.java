package clients.local;

import board.Tile;
import player.move.Move;

public interface LocalClient {

    void receiveMap(String map);

    void receivePlayerNumber(Tile player);

    Move sendMove();

    void receiveMove(Move move);

}
