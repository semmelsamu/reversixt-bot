package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;

public class MoveExecutor {

    Game game;

    public MoveExecutor(Game game) {
        this.game = game;
    }

    public void executeMove(Move move) {

        Logger.get().log("Executing move " + move);
        if (!(move instanceof BombMove)) {
            executeMovePhase1(move);
        } else {
            executeBombMove(move);
        }

        Logger.get().debug("Game after move execution: " + game);
    }

    /*
    |--------------------------------------------------------------------------
    | Main coloring logic
    |--------------------------------------------------------------------------
    */
    private void executeMovePhase1(Move move) {
        Tile playerValue = move.getPlayer();
        Set<Coordinates> tilesToColor = new HashSet<>();
        // Check every direction
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, move.getCoordinates(), direction);

            // Check if tile has a neighbour in this direction
            if (!(tileReader.hasNext())) {
                continue;
            }
            tileReader.next();
            Tile firstNeighnbourTile = tileReader.getTile();
            // Check if first neighbour tile is unoccupied
            if (firstNeighnbourTile.isUnoccupied()) {
                continue;
            }
            // Check if first neighbour tile is an own tile
            if (firstNeighnbourTile == playerValue) {
                continue;
            }
            // Check if first neighbour tile has the same coordinates
            if (tileReader.getCoordinates() == move.getCoordinates()) {
                continue;
            }
            tilesToColor.addAll(getTilesToColorInDirection(tileReader, move));
        }
        // Check if an overwrite stone has to be used
        if (!(game.getTile(move.getCoordinates()).isUnoccupied())) {
            game.getPlayers()[move.getPlayer().toPlayerIndex()].decrementOverwriteStones();
        }

        // Color all tiles
        for (Coordinates coordinates : tilesToColor) {
            game.setTile(coordinates, playerValue);
        }
        game.setTile(move.getCoordinates(), playerValue);

        if (move instanceof BonusMove) {
            executeBonusLogic((BonusMove) move);
        }

        if (move instanceof ChoiceMove) {
            executeChoiceLogic((ChoiceMove) move);
        }

        if (move instanceof InversionMove) {
            executeInversionLogic();
        }
    }

    private static Set<Coordinates> getTilesToColorInDirection(TileReader tileReader, Move move) {
        Tile currentTile = tileReader.getTile();
        Tile playerValue = move.getPlayer();
        Set<Coordinates> tilesToColorInDirection = new HashSet<>();
        // As long as the current tile is not an own piece
        while (currentTile != playerValue) {

            tilesToColorInDirection.add(tileReader.getCoordinates());

            // Check if there is a dead end
            if (!(tileReader.hasNext())) {
                return new HashSet<>();
            }

            tileReader.next();
            currentTile = tileReader.getTile();

            // Check if the coordinates are the same as of the new Tile
            if (move.getCoordinates().equals(tileReader.getCoordinates())) {
                return new HashSet<>();
            }
            // Check if there is an unoccupied tile
            if (currentTile.isUnoccupied()) {
                return new HashSet<>();
            }
        }
        return tilesToColorInDirection;

    }

    /*
    |--------------------------------------------------------------------------
    | Special move logic
    |--------------------------------------------------------------------------
    */

    private void executeBonusLogic(BonusMove bonusMove) {
        if (bonusMove.getBonus() == Bonus.BOMB) {
            game.getCurrentPlayer().incrementBombs();
        } else if (bonusMove.getBonus() == Bonus.OVERWRITE_STONE) {
            game.getCurrentPlayer().incrementOverwriteStone();
        } else {
            Logger.get().fatal("Tried to execute bonus move without bonus action");
        }
    }

    private void executeChoiceLogic(ChoiceMove choiceMove) {
        Tile currentPlayer = game.getTile(choiceMove.getCoordinates());
        Tile playerToSwapWith = choiceMove.getPlayerToSwapWith();

        // Collect all occupied tiles of current player
        var oldTilesPlayerFromCurrentPlayer =
                new HashSet<>(game.getGameStats().getAllCoordinatesWhereTileIs(currentPlayer));

        // Iterate through all old tiles of player to swap with
        for (Coordinates coordinates : new HashSet<>(
                game.getGameStats().getAllCoordinatesWhereTileIs(playerToSwapWith))) {
            game.setTile(coordinates, currentPlayer);
        }

        // Iterate through all old tiles of current player
        for (Coordinates coordinates : oldTilesPlayerFromCurrentPlayer) {
            game.setTile(coordinates, playerToSwapWith);
        }
    }

    private void executeInversionLogic() {

        // Overwriting Tiles in Board
        Tile[] participatingPlayerTiles = game.getAllParticipatingPlayers();
        // TODO: Tile[] participatingPlayerTiles = (Tile[]) Arrays.stream(game.getPlayers()).map
        //  (Player::getPlayerValue).toArray();

        // Updating OccupiedTiles of all Players

        var previousPlayer = participatingPlayerTiles[participatingPlayerTiles.length - 1];

        // We need to create a copy of the coordinate set as we are altering it in the loop
        // Else we get a ConcurrentModificationException.
        Set<Coordinates> oldTilesFromPred =
                new HashSet<>(game.getGameStats().getAllCoordinatesWhereTileIs(previousPlayer));

        Set<Coordinates> oldOwnTiles = null;

        for (int i = 0; i < participatingPlayerTiles.length; i++) {

            if (i != 0) {
                oldTilesFromPred = oldOwnTiles;
            }

            oldOwnTiles = new HashSet<>(
                    game.getGameStats().getAllCoordinatesWhereTileIs(participatingPlayerTiles[i]));
            Logger.get().debug(oldTilesFromPred.toString());

            for (Coordinates coordinates : oldTilesFromPred) {
                game.setTile(coordinates, participatingPlayerTiles[i]);
            }

        }
    }

    public void executeBombMove(Move move) {
        if (game.getCurrentPlayer().getBombs() == 0) {
            Logger.get().error("No bombs available");
            return;
        }

        if (!(move instanceof BombMove)) {
            Logger.get().log("Not a bomb move");
            return;
        }

        for (Coordinates bombedTile : getAllTilesToBeBombed(move.getCoordinates())) {
            game.setTile(bombedTile, Tile.WALL);
        }
    }

    // TODO: refactor
    private Set<Coordinates> getAllTilesToBeBombed(Coordinates coordinates) {
        int radius = game.getBombRadius();
        Set<Coordinates> allDestroyedTiles = new HashSet<>();
        Set<Coordinates> allDestroyedTilesTemp = new HashSet<>();

        allDestroyedTiles.add(coordinates);
        for (int i = 0; i < radius; i++) {
            for (Coordinates allDestroyedTile : allDestroyedTiles) {
                for (Direction direction : Direction.values()) {
                    TileReader reader = new TileReader(game, allDestroyedTile, direction);
                    if (reader.hasNext()) {
                        reader.next();
                        allDestroyedTilesTemp.add(new Coordinates(reader.getCoordinates().x,
                                reader.getCoordinates().y));
                    }
                }
            }
            allDestroyedTiles.addAll(allDestroyedTilesTemp);
            allDestroyedTilesTemp.clear();
        }

        return allDestroyedTiles;
    }
}
