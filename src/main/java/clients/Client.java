package clients;

import game.Game;
import move.Move;

/**
 * A client that operates with local Data structures and Types, such as Tile, Move, ...
 */
public abstract class Client {

    protected Game game;

    protected int ME;

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(int player) {
        this.ME = player;
    }

    public abstract Move sendMove(int timeLimit, int depthLimit);

}
