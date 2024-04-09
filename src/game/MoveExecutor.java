package game;

import board.Direction;
import board.Neighbour;
import board.Tile;
import board.TileValue;
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

        Set<Tile> tilesToColour = new HashSet<>();
        // Check every direction
        for (Direction d : Direction.values()) {
            Neighbour neighbour = move.getTile().getNeighbour(d);
            // Check if there is a dead end
            if (neighbour == null) {
                continue;
            }
            TileValue neighbourValue = neighbour.tile().getValue();
            // Check if tile value is seen as an enemy or if it's the same color
            if (TileValue.getAllFriendlyValues().contains(neighbourValue) || neighbourValue == move.getPlayer().getPlayerValue()) {
                continue;
            }
            tilesToColour.addAll(getTilesToColourInDirection(move.getPlayer(), move.getTile(), d));
        }

        if (!game.getTile(move.getTile().getPosition()).getValue().isEmpty()) {
            game.getPlayers()[move.getPlayer().getPlayerValue().toPlayerIndex()].decreaseOverwriteStones();
        }

        // Color all tiles
        for (Tile tile : tilesToColour) {
            game.setTile(tile.getPosition(), move.getPlayer().getPlayerValue());
        }
        game.setTile(move.getTile().getPosition(), move.getPlayer().getPlayerValue());


        if (move instanceof BonusMove)
            executeBonusLogic((BonusMove) move);

        if (move instanceof ChoiceMove)
            executeChoiceLogic((ChoiceMove) move);

        if (move instanceof InversionMove)
            executeInversionLogic();
    }

    private static Set<Tile> getTilesToColourInDirection(Player player, Tile currentTile, Direction currentDirection) {
        Tile firstTile = currentTile;
        Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);
        Set<Tile> tilesToColourInDirection = new HashSet<>();
        // As long the neighbour has the same color as itself
        while (currentNeighbour.tile().getValue() != player.getPlayerValue()) {
            currentTile = currentNeighbour.tile();

            tilesToColourInDirection.add(currentTile);
            if (currentNeighbour.directionChange() != null) {
                currentDirection = currentNeighbour.directionChange();
            }
            currentNeighbour = currentTile.getNeighbour(currentDirection);

            // Check if there is a dead end or if it's the same as the first one
            if (currentNeighbour == null || firstTile == currentNeighbour.tile()) {
                return new HashSet<>();
            }

            // Check if there are values which are not enemies
            if (TileValue.getAllFriendlyValues().contains(currentNeighbour.tile().getValue())) {
                return new HashSet<>();
            }
        }
        return tilesToColourInDirection;

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
        List<Tile> oldTilesPlayerFromCurrentPlayer = game.getAllTilesWithValue(currentPlayer.getPlayerValue());

        // for(Tile tile : playerToSwapWith.getOccupiedTiles()){
        for (Tile tile : game.getAllTilesWithValue(playerToSwapWith.getPlayerValue())) {
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
        List<Tile> oldTilesfromPred = game.getAllTilesWithValue(players[players.length - 1].getPlayerValue());
        List<Tile> oldOwnTiles = null;
        TileValue[] playerValues = TileValue.getAllPlayerValues();
        for (int i = 0; i < players.length; i++) {
            if (i != 0) {
                oldTilesfromPred = oldOwnTiles;
            }
            // oldOwnTiles = players[i].getOccupiedTiles();
            oldOwnTiles = game.getAllTilesWithValue(players[i].getPlayerValue());
            for (Tile tile : oldTilesfromPred) {
                game.setTile(tile.getPosition(), playerValues[i]);
            }
        }
    }

}
