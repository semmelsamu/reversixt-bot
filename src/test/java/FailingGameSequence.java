import board.Coordinates;
import board.Tile;
import game.Game;
import game.GameFactory;
import game.MoveCalculator;
import game.MoveExecutor;
import player.move.Move;

public class FailingGameSequence {
    public static void main(String[] args) {

        Game game = GameFactory.createFromFile("maps/initialMaps/infinity.map");
        MoveExecutor moveExecutor = new MoveExecutor(game);
        MoveCalculator moveCalculator = new MoveCalculator(game);
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(5, 1)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(6, 1)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(5, 1)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(4, 1)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(3, 4)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(6, 4)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(5, 4)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(5, 3)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(6, 3)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(2, 5)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(5, 0)));
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(6, 2)));
        moveExecutor.executeMove(new Move(Tile.PLAYER1, new Coordinates(7, 3)));
        moveCalculator.getValidMovesForPlayer(Tile.PLAYER2);
        // invalid move
        moveExecutor.executeMove(new Move(Tile.PLAYER2, new Coordinates(7, 1)));
    }
}
