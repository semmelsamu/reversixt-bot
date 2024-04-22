import board.Tile;
import game.Game;
import game.GameFactory;
import org.junit.jupiter.api.Test;
import util.TestLogger;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileCountingTest {

    @Test
    public void countWindowMapPlayersTiles_test(){
        Game game = GameFactory.createFromFile("maps/initialMaps/window.map");

        for (Tile allParticipatingPlayer : game.getAllParticipatingPlayers()) {
            int calculatedTileCount =
                    game.getAllCoordinatesWhereTileIs(allParticipatingPlayer).size();
            assertEquals(4, calculatedTileCount, "Tile count for each player at 4");
        }
    }

    @Test
    public void countCheckerboardMapPlayersTiles_test(){
        Game game = GameFactory.createFromFile("maps/initialMaps/checkerboard.map");

        for (Tile allParticipatingPlayer : game.getAllParticipatingPlayers()) {
            int calculatedTileCount =
                    game.getAllCoordinatesWhereTileIs(allParticipatingPlayer).size();
            assertEquals(8, calculatedTileCount, "Tile count for each player at 8");
        }
    }
}
