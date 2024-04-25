package boeseMaps;

import board.Tile;
import game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.move.Move;
import util.NetworkClientHelper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static util.MoveCalculatorHelper.*;
import static util.MoveExecutorHelper.*;

public class boeseMap01Test {

    private Game gameFromClient1;
    NetworkClientHelper helper1;

    @BeforeEach
    public void setup() {
        helper1 = new NetworkClientHelper("maps/boeseMaps/boeseMap01.map");
        gameFromClient1 = helper1.getGame();

    }

    @Test
    public void moveCalculator_test() {
        Set<Move> allValidMovesForPlayer1 = getAllValidMovesForPlayer(gameFromClient1, Tile.PLAYER1);

        assertEquals(1, allValidMovesForPlayer1.size(), "Only one valid move");
        assertEquals(allValidMovesForPlayer1.iterator().next(), createDummyMove(Tile.PLAYER1, 7, 1),
                "Move valid");
    }

    @Test
    public void moveExecutor_test() {
        helper1.receiveMove(7, 1, 0, Tile.PLAYER1);

        assertEquals(Tile.PLAYER1, getTile(gameFromClient1, 7, 1), "Tile colored");
        assertEquals(Tile.PLAYER1, getTile(gameFromClient1, 6, 1), "Tile colored");
        assertEquals(Tile.PLAYER1, getTile(gameFromClient1, 5, 1), "Tile colored");
        assertEquals(Tile.PLAYER2, getTile(gameFromClient1, 4, 0), "Tile not colored");
        assertEquals(Tile.EMPTY, getTile(gameFromClient1, 3, 0), "Tile not colored");
    }
}
