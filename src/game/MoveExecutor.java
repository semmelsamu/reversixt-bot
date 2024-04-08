package game;

import board.*;
import player.Player;
import player.move.*;
import util.Logger;

import java.util.*;

public class MoveExecutor {

    Game game;

    public MoveExecutor(Game game){
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

        tilesToColour.add(move.getTile());

        // Color all tiles
        for (Tile tile : tilesToColour) {
            Optional<Player> otherPlayer = Arrays.stream(game.getPlayers()).filter(p -> p.getPlayerValue() == tile.getValue()).findFirst();

            // Remove old stone from opponent player's occupied list
            otherPlayer.ifPresent(value -> value.getOccupiedTiles().remove(tile));
            // Add this stone to this player
            move.getPlayer().getOccupiedTiles().add(tile);

            // Color actual board
            game.getBoard().setTileValue(tile.getPosition(), move.getPlayer().getPlayerValue());
        }

        if(move instanceof BonusMove)
            executeBonusLogic((BonusMove) move);

        if(move instanceof ChoiceMove)
            executeChoiceLogic((ChoiceMove) move);

        if(move instanceof InversionMove)
            executeInversionLogic();
    }

    private static Set<Tile> getTilesToColourInDirection(Player player, Tile currentTile, Direction currentDirection) {
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

            // Check if there is a dead end
            if (currentNeighbour == null) {
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
        if(bonusMove.getBonus() == Bonus.BOMB) {
            game.getCurrentPlayer().incrementBombs();
        }
        else if(bonusMove.getBonus() == Bonus.OVERWRITE_STONE) {
            game.getCurrentPlayer().incrementOverwriteStone();
        }
        else {
            Logger.get().fatal("Tried to execute bonus move without bonus action");
        }
    }

    private void executeChoiceLogic(ChoiceMove choiceMove) {
        Player currentPlayer = game.getCurrentPlayer();
        Player playerToSwapWith = choiceMove.getPlayerToSwapWith();

        List<Tile> oldTilesPlayerFromCurrentPlayer = currentPlayer.getOccupiedTiles();

        overwritingPiecesFromTo(currentPlayer, playerToSwapWith);
        overwritingPiecesFromTo(playerToSwapWith, currentPlayer);


        currentPlayer.setOccupiedTiles(playerToSwapWith.getOccupiedTiles());
        playerToSwapWith.setOccupiedTiles(oldTilesPlayerFromCurrentPlayer);
        /*TileValue playerA = game.getCurrentPlayer().getPlayerValue();
        TileValue playerB = choiceMove.getPlayerToSwapWith().getPlayerValue();

        ArrayList<Tile> tilesPlayerA = (ArrayList<Tile>) game.getBoard().getAllTilesWithValue(playerA);
        ArrayList<Tile> tilesPlayerB = (ArrayList<Tile>) game.getBoard().getAllTilesWithValue(playerB);

        for(var tile : tilesPlayerA) {
            game.getBoard().setTileValue(tile.getPosition(), playerB);
        }

        for(var tile : tilesPlayerB) {
            game.getBoard().setTileValue(tile.getPosition(), playerA);
        }
        */
    }

    private void executeInversionLogic() {
        // Overwriting Tiles in Board
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; i++) {
            overwritingPiecesFromTo(players[i], players[(i+1)% players.length]);
        }

        // Updating OccupiedTiles of all Players
        List<Tile> oldTilesfromPred = players[players.length - 1].getOccupiedTiles();
        List<Tile> oldOwnTiles = null;
        for (int i = 0; i < players.length; i++){
            if(i != 0){
                oldTilesfromPred = oldOwnTiles;
            }
            oldOwnTiles = players[i].getOccupiedTiles();
            players[i].setOccupiedTiles(oldTilesfromPred);
        }
    }


    /**
     * Overwriting tiles from fromPlayer to toPlayer. Not updating occupiedTiles!!!
     */
    private void overwritingPiecesFromTo(Player fromPlayer, Player toPlayer){
        Board board = game.getBoard();
        TileValue toPlayerValue = toPlayer.getPlayerValue();
        List<Tile> tilesToOverwrite = fromPlayer.getOccupiedTiles();
        for(Tile tile : tilesToOverwrite){
            board.setTileValue(tile.getPosition(), toPlayerValue);
        }
    }

}
