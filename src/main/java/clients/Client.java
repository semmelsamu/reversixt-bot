package clients;

import game.Game;
import move.Move;

/**
 * A client that operates with local Data structures and Types, such as Tile, Move, ...
 */
public interface Client {

    Move sendMove(Game game, int player);

}
