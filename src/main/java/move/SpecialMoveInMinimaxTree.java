package move;

import board.Coordinates;

public abstract class SpecialMoveInMinimaxTree extends Move {
    SpecialMoveInMinimaxTree(int player, Coordinates coordinates){
        super(player, coordinates);
    }
}
