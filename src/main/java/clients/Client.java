package clients;

import board.Tile;
import game.Game;
import game.GamePhase;
import player.move.Move;

/**
 * A client that operates with local Data structures and Types, such as Tile, Move, ...
 */
public interface Client {

    Move sendMove(Game game, Tile player);

}
