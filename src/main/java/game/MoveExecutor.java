package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import player.Player;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;

public class MoveExecutor {

    Logger logger = new Logger(this.getClass().getName());

    Game game;

    public MoveExecutor(Game game) {
        this.game = game;
    }

    public void executeMove(Move move) {

        logger.log("Executing move " + move);

        if(move.getPlayerNumber() != game.getCurrentPlayerNumber()) {
            logger.warn("Executing move of player who is currently not their turn");
        }

        if (!(move instanceof BombMove)) {
            executeMovePhase1(move);
        } else {
            executeBombMove((BombMove) move);
        }

        if(move.getPlayerNumber() == game.getCurrentPlayerNumber()) {
            game.nextPlayer();
        }

        logger.debug("Game after move execution: " + game);
    }

    /*
    |--------------------------------------------------------------------------
    | Main coloring logic
    |--------------------------------------------------------------------------
    */
    private void executeMovePhase1(Move move) {
        Tile playerValue = game.getPlayer(move.getPlayerNumber()).getPlayerValue();
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
            game.getPlayer(move.getPlayerNumber()).decrementOverwriteStones();
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

    private Set<Coordinates> getTilesToColorInDirection(TileReader tileReader, Move move) {
        Tile currentTile = tileReader.getTile();
        Tile playerValue = game.getPlayer(move.getPlayerNumber()).getPlayerValue();
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
        Player player = game.getPlayer(bonusMove.getPlayerNumber());
        if (bonusMove.getBonus() == Bonus.BOMB) {
            player.incrementBombs();
        } else if (bonusMove.getBonus() == Bonus.OVERWRITE_STONE) {
            player.incrementOverwriteStone();
        } else {
            logger.fatal("Tried to execute bonus move without bonus action");
        }
    }

    private void executeChoiceLogic(ChoiceMove choiceMove) {
        Player player = game.getPlayer(choiceMove.getPlayerNumber());
        Player playerToSwapWith = game.getPlayer(choiceMove.getPlayerToSwapWith());

        // Collect all occupied tiles of current player
        var oldTilesPlayerFromCurrentPlayer = new HashSet<>(game.getGameStats()
                .getAllCoordinatesWhereTileIs(player.getPlayerValue()));

        // Iterate through all old tiles of player to swap with
        for (Coordinates coordinates : new HashSet<>(game.getGameStats()
                .getAllCoordinatesWhereTileIs(playerToSwapWith.getPlayerValue()))) {
            game.setTile(coordinates, player.getPlayerValue());
        }

        // Iterate through all old tiles of current player
        for (Coordinates coordinates : oldTilesPlayerFromCurrentPlayer) {
            game.setTile(coordinates, playerToSwapWith.getPlayerValue());
        }
    }

    private void executeInversionLogic() {

        // Overwriting Tiles in Board
        Player[] players = game.getPlayers();
        // TODO: Tile[] participatingPlayerTiles = (Tile[]) Arrays.stream(game.getPlayers()).map
        //  (Player::getPlayerValue).toArray();

        // Updating OccupiedTiles of all Players

        var previousPlayer = players[players.length - 1];

        // We need to create a copy of the coordinate set as we are altering it in the loop
        // Else we get a ConcurrentModificationException.
        Set<Coordinates> oldTilesFromPred = new HashSet<>(
                game.getGameStats().getAllCoordinatesWhereTileIs(previousPlayer.getPlayerValue()));

        Set<Coordinates> oldOwnTiles = null;

        for (int i = 0; i < players.length; i++) {

            if (i != 0) {
                oldTilesFromPred = oldOwnTiles;
            }

            oldOwnTiles = new HashSet<>(
                    game.getGameStats().getAllCoordinatesWhereTileIs(players[i].getPlayerValue()));
            logger.debug(oldTilesFromPred.toString());

            for (Coordinates coordinates : oldTilesFromPred) {
                game.setTile(coordinates, players[i].getPlayerValue());
            }

        }
    }

    public void executeBombMove(BombMove move) {
        if (game.getPlayer(move.getPlayerNumber()).getBombs() == 0) {
            throw new RuntimeException("No bombs available :(");
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
