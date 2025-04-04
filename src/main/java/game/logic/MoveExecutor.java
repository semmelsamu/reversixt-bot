package game.logic;

import board.*;
import exceptions.BonusNotSpecifiedException;
import game.Game;
import game.Player;
import move.*;

import java.util.HashSet;
import java.util.Set;

public final class MoveExecutor {

    public static void executeMove(Game game, Move move) {

        if (!(move instanceof BombMove)) {
            executeColoringMove(game, move);
            if(!(move instanceof OverwriteMove)){
                game.totalTilesOccupiedCounter.incrementTotalTilesOccupied();
            }
        } else {
            executeBombMove(game, (BombMove) move);
        }

    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Coloring Logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static void executeColoringMove(Game game, Move move) {

        Tile playerValue = game.getPlayer(move.getPlayerNumber()).getPlayerValue();

        if (move instanceof OverwriteMove) {
            game.getPlayer(move.getPlayerNumber()).decrementOverwriteStones();
        }

        // Color all tiles
        Set<Coordinates> allTilesToColor =
                getAllTilesToColor(game, playerValue, move.getCoordinates());

        for (var coordinates : allTilesToColor) {
            game.setTile(coordinates, playerValue);
        }

        if (move instanceof BonusMove) {
            executeBonusLogic(game, (BonusMove) move);
        }

        if (move instanceof ChoiceMove) {
            executeChoiceLogic(game, (ChoiceMove) move);
        }

        if (move instanceof InversionMove) {
            executeInversionLogic(game);
        }
    }

    private static Set<Coordinates> getAllTilesToColor(Game game, Tile playerValue,
                                                       Coordinates position) {

        Set<Coordinates> result = new HashSet<>();

        // Of course the Coordinates we set the stone on get colored
        result.add(position);

        // Coloring in every Direction
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, position, direction);

            Set<Coordinates> buffer = new HashSet<>();

            while (tileReader.hasNext()) {
                tileReader.next();
                Tile currentTile = tileReader.getTile();

                // Check if first neighbour is an own tile
                if (tileReader.getTileNumber() == 1) {
                    if (currentTile == playerValue) {
                        break;
                    }
                }

                if (currentTile.isUnoccupied()) {
                    break;
                }
                if(tileReader.getCoordinates().equals(position)) {
                    break;
                }
                if (currentTile == playerValue) {
                    result.addAll(buffer);
                    break;
                }

                buffer.add(tileReader.getCoordinates());
            }
        }

        return result;
    }


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Special move logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static void executeBonusLogic(Game game, BonusMove bonusMove) {
        Player player = game.getPlayer(bonusMove.getPlayerNumber());
        if (bonusMove.getBonus() == Bonus.BOMB) {
            player.incrementBombs();
        } else if (bonusMove.getBonus() == Bonus.OVERWRITE_STONE) {
            player.incrementOverwriteStone();
        } else {
            throw new BonusNotSpecifiedException(
                    "Tried to execute bonus move without bonus action");
        }
    }

    private static void executeChoiceLogic(Game game, ChoiceMove choiceMove) {
        Player player = game.getPlayer(choiceMove.getPlayerNumber());
        Player playerToSwapWith = game.getPlayer(choiceMove.getPlayerToSwapWith());

        // Collect all occupied tiles of current player
        var oldTilesPlayerFromCurrentPlayer = new HashSet<>(
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                        player.getPlayerValue()));

        // Iterate through all old tiles of player to swap with
        for (Coordinates coordinates : new HashSet<>(
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                        playerToSwapWith.getPlayerValue()))) {
            game.setTile(coordinates, player.getPlayerValue());
        }

        // Iterate through all old tiles of current player
        for (Coordinates coordinates : oldTilesPlayerFromCurrentPlayer) {
            game.setTile(coordinates, playerToSwapWith.getPlayerValue());
        }
    }

    private static void executeInversionLogic(Game game) {

        // Overwriting Tiles in Board
        Player[] players = game.getPlayers();

        // Updating OccupiedTiles of all Players

        var previousPlayer = players[players.length - 1];

        // We need to create a copy of the coordinate set as we are altering it in the loop
        // Else we get a ConcurrentModificationException.
        Set<Coordinates> oldTilesFromPred = new HashSet<>(
                game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                        previousPlayer.getPlayerValue()));

        Set<Coordinates> oldOwnTiles = null;

        for (int i = 0; i < players.length; i++) {

            if (i != 0) {
                oldTilesFromPred = oldOwnTiles;
            }

            oldOwnTiles = new HashSet<>(game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                    players[i].getPlayerValue()));

            for (Coordinates coordinates : oldTilesFromPred) {
                game.setTile(coordinates, players[i].getPlayerValue());
            }

        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Bomb move logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static void executeBombMove(Game game, BombMove move) {

        if (game.getPlayer(move.getPlayerNumber()).getBombs() == 0) {
            throw new RuntimeException("No bombs available :(");
        }

        int radius = game.constants.bombRadius();
        Set<Coordinates> coordinates = new HashSet<>();
        coordinates.add(move.getCoordinates());

        for (Coordinates c : CoordinatesExpander.expandCoordinates(game, coordinates, radius)) {
            game.setTile(c, Tile.WALL);
        }

        game.getPlayer(move.getPlayerNumber()).decrementBombs();
    }
}
