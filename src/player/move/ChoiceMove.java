package player.move;

import board.Board;
import board.Tile;
import game.Game;
import player.Player;
import util.ConsoleInputHandler;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends Move {

    private Game game;

    public ChoiceMove(Player player, Tile tile, Game game) {
        super(player, tile);
        this.game = game;
    }

    @Override
    public void execute(Board board) {
        super.execute(board);
        int playerToSwapWithIndex = getPlayerToSwapWithIndex();

        Player[] players = game.getPlayers();
        Player temp = game.getCurrentPlayer();
        players[game.getCurrentPlayerIndex()] = players[playerToSwapWithIndex];
        players[playerToSwapWithIndex] = temp;
        game.setPlayers(players);
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public int getPlayerToSwapWithIndex() {
        return ConsoleInputHandler.handleChoice(game) - 1;
    }
}
