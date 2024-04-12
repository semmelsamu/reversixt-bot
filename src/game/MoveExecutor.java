package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import player.Player;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.List;
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
        Set<Coordinates> tilesToColour = new HashSet<>();
        // Check every direction
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, move.getCoordinates(), direction);

            // Check if there is a dead end
            if(!(tileReader.hasNext())){
                continue;
            }
            tileReader.next();
            Tile firstNeighnbourTile = tileReader.getTile();
            // Check if first neighbour tile is neutral
            if(firstNeighnbourTile.isUnoccupied()){
                continue;
            }
            //Check if first neighbour tile is an own tile
            if(firstNeighnbourTile == playerValue) {
                continue;
            }
            //Check if first neighbour has the same Coordinates
            if(tileReader.getCoordinates() == move.getCoordinates())
            tilesToColour.addAll(getTilesToColourInDirection(tileReader, direction, move));
        }

        if (!(game.getTile(move.getCoordinates()).isUnoccupied())) {
            game.getPlayers()[move.getPlayer().toPlayerIndex()].decrementOverwriteStones();
        }

        // Color all tiles
        for (Coordinates coordinates : tilesToColour) {
            game.setTile(coordinates, playerValue);
        }
        game.setTile(move.getCoordinates(), playerValue);


        if (move instanceof BonusMove)
            executeBonusLogic((BonusMove) move);

        if (move instanceof ChoiceMove)
            executeChoiceLogic((ChoiceMove) move);

        if (move instanceof InversionMove)
            executeInversionLogic();
    }

    private static Set<Coordinates> getTilesToColourInDirection(TileReader tileReader,
                                                         Direction currentDirection, Move move) {
        Tile currentTile = tileReader.getTile();
        Tile playerValue = move.getPlayer();
        Set<Coordinates> positionsToColourInDirection = new HashSet<>();
        // As long as the current tile is not an own piece
        while (currentTile != playerValue) {

            positionsToColourInDirection.add(tileReader.getCoordinates());

            // Check if there is a dead end
            if (!(tileReader.hasNext())) {
                return new HashSet<>();
            }
            tileReader.next();
            currentTile = tileReader.getTile();

            // Check if the coordinates are the same as of the new Tile
            if (move.getCoordinates() == tileReader.getCoordinates()){
                return new HashSet<>();
            }
            // Check if there is a neutral tile
            if(currentTile.isUnoccupied()) {
                return new HashSet<>();
            }

        }
        return positionsToColourInDirection;

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
        List<Coordinates> oldTilesPlayerFromCurrentPlayer = game.getGameStats().getAllCoordinatesWhereTileIs(currentPlayer);

        // Iterate through all old tiles of player to swap with
        for (Coordinates coordinates : game.getGameStats().getAllCoordinatesWhereTileIs(playerToSwapWith)) {
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
        List<Coordinates> oldTilesfromPred =
                game.getGameStats()
                .getAllCoordinatesWhereTileIs
                (participatingPlayerTiles[participatingPlayerTiles.length - 1]);
        List<Coordinates> oldOwnTiles = null;
        for (int i = 0; i < participatingPlayerTiles.length; i++) {
            if (i != 0) {
                oldTilesfromPred = oldOwnTiles;
            }
            oldOwnTiles = game.getGameStats()
                    .getAllCoordinatesWhereTileIs(participatingPlayerTiles[i]);
            for (Coordinates coordinates : oldTilesfromPred) {
                game.setTile(coordinates, participatingPlayerTiles[i]);
            }
        }
    }

}
