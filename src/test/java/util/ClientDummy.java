package util;

import board.Tile;
import clients.Client;
import game.Game;
import player.move.Move;

public class ClientDummy implements Client {

    @Override
    public Move sendMove(Game game, Tile player) {
        return null;
    }
}
