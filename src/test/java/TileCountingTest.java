import game.Game;
import game.GameFactory;
import game.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileCountingTest {

    @Test
    public void countWindowMapPlayersTiles_test(){
        Game game = GameFactory.createFromFile("maps/initialMaps/window.map");

        for (Player allParticipatingPlayer : game.getPlayers()) {
            int calculatedTileCount =
                    game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                                    allParticipatingPlayer.getPlayerValue())
                            .size();
            assertEquals(4, calculatedTileCount, "Tile count for each player at 4");
        }
    }

    @Test
    public void countCheckerboardMapPlayersTiles_test(){
        Game game = GameFactory.createFromFile("maps/initialMaps/checkerboard.map");

        for (Player allParticipatingPlayer : game.getPlayers()) {
            int calculatedTileCount = game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                            allParticipatingPlayer.getPlayerValue())
                            .size();
            assertEquals(8, calculatedTileCount, "Tile count for each player at 8");
        }
    }
}
