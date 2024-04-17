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

    /*
    |--------------------------------------------------------------------------
    | Main coloring logic
    |--------------------------------------------------------------------------
    */

    public void executeMove(Move move) {

        Logger.get().log("Executing move " + move);
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
            if (move.getCoordinates() == tileReader.getCoordinates()) {
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
                game.getGameStats().getAllCoordinatesWhereTileIs(currentPlayer);

        // Iterate through all old tiles of player to swap with
        for (Coordinates coordinates : game.getGameStats()
                .getAllCoordinatesWhereTileIs(playerToSwapWith)) {
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
        // Updating OccupiedTiles of all Players
        // List<Tile> oldTilesfromPred = players[players.length - 1].getOccupiedTiles();
        var oldTilesfromPred = game.getGameStats().getAllCoordinatesWhereTileIs(
                participatingPlayerTiles[participatingPlayerTiles.length - 1]);
        Set<Coordinates> oldOwnTiles = null;
        for (int i = 0; i < participatingPlayerTiles.length; i++) {
            if (i != 0) {
                oldTilesfromPred = oldOwnTiles;
            }
            oldOwnTiles =
                    game.getGameStats().getAllCoordinatesWhereTileIs(participatingPlayerTiles[i]);
            for (Coordinates coordinates : oldTilesfromPred) {
                game.setTile(coordinates, participatingPlayerTiles[i]);
            }
        }
    }

}
