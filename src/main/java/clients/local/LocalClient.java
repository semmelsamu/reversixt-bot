package clients.local;

import board.Tile;
import player.move.Move;

/**
 * A client that operates with local Data structures and Types, such as Tile, Move, ...
 */
public interface LocalClient {

    void receiveMap(String map);

    void receivePlayerNumber(Tile player);

    Move sendMove();

    void receiveMove(Move move);

}
