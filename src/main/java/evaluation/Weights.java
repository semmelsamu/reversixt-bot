package evaluation;

import board.Coordinates;
import board.CoordinatesExpander;
import board.Tile;
import game.Game;
import util.Logger;

import java.util.Set;

public class Weights {

    int[][] weights;

    public Weights(Game game) {

        weights = new int[game.getHeight()][game.getWidth()];

        for (int y = 0; y < game.getHeight(); y++) {

            for (int x = 0; x < game.getWidth(); x++) {

                int score = 0;

                if (!game.getTile(new Coordinates(x, y)).equals(Tile.WALL)) {
                    score = 9;
                }

                weights[y][x] = score;

            }

        }

        for (int y = 0; y < weights.length; y++) {
            for (int x = 0; x < weights[0].length; x++) {

                int neighbours =
                        CoordinatesExpander.expandCoordinates(game, Set.of(new Coordinates(x, y)),
                                1).size();

                weights[y][x] -= neighbours;

                if (weights[y][x] < 0) {
                    weights[y][x] = 0;
                }
            }
        }

        Logger.get().log(toString());

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("Weights");

        for (int[] weightRow : weights) {
            result.append("\n");
            for (int i : weightRow) {
                if (i == 0) {
                    result.append("  ");
                } else {
                    result.append(i).append(" ");
                }
            }
        }

        return result.toString();
    }

}
