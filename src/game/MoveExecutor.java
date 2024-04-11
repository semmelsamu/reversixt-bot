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
        Player currentPlayer = game.getCurrentPlayer();
        Player playerToSwapWith = choiceMove.getPlayerToSwapWith();

        // List<Tile> oldTilesPlayerFromCurrentPlayer = currentPlayer.getOccupiedTiles();
        List<Tile> oldTilesPlayerFromCurrentPlayer = game.getGameStats().getAllTilesWithValue(currentPlayer.getPlayerValue());

        // for(Tile tile : playerToSwapWith.getOccupiedTiles()){
        for (Tile tile : game.getGameStats().getAllTilesWithValue(playerToSwapWith.getPlayerValue())) {
            game.setTile(tile.getPosition(), currentPlayer.getPlayerValue());
        }

        for (Tile tile : oldTilesPlayerFromCurrentPlayer) {
            game.setTile(tile.getPosition(), playerToSwapWith.getPlayerValue());
        }
    }

    private void executeInversionLogic() {
        // Overwriting Tiles in Board
        Player[] players = game.getPlayers();

        // Updating OccupiedTiles of all Players
        // List<Tile> oldTilesfromPred = players[players.length - 1].getOccupiedTiles();
        List<Tile> oldTilesfromPred = game.getGameStats().getAllTilesWithValue(players[players.length - 1].getPlayerValue());
        List<Tile> oldOwnTiles = null;
        TileValue[] playerValues = TileValue.getAllPlayerValues();
        for (int i = 0; i < players.length; i++) {
            if (i != 0) {
                oldTilesfromPred = oldOwnTiles;
            }
            // oldOwnTiles = players[i].getOccupiedTiles();
            oldOwnTiles = game.getGameStats().getAllTilesWithValue(players[i].getPlayerValue());
            for (Tile tile : oldTilesfromPred) {
                game.setTile(tile.getPosition(), playerValues[i]);
            }
        }
    }

}
