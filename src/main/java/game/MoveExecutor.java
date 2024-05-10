package game;

import board.Coordinates;
import board.Direction;
import board.Tile;
import board.TileReader;
import exceptions.BonusNotSpecifiedException;
import move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;

public final class MoveExecutor {

    static Logger logger = new Logger(MoveExecutor.class.getName());

    public static void executeMove(Game game, Move move) {

        logger.verbose("Executing move " + move);

        if (move.getPlayerNumber() != game.getCurrentPlayerNumber()) {
            logger.warn("Executing move of player who is currently not their turn");
        }

        if (!(move instanceof BombMove)) {
            executeColoringMove(game, move);
        } else {
            executeBombMove(game, (BombMove) move);
        }

        game.nextPlayer();

        logger.debug("Game after move execution: " + game);
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

        // Check if an overwrite stone has to be used
        if (!(game.getTile(move.getCoordinates()).isUnoccupied())) {
            game.getPlayer(move.getPlayerNumber()).decrementOverwriteStones();
        }

        // Color all tiles
        for (var coordinates : getAllTilesToColor(game, playerValue, move.getCoordinates())) {
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

                if (tileReader.getTileNumber() == 1) {
                    if (currentTile.isUnoccupied() ||
                            currentTile == playerValue ||
                            tileReader.getCoordinates() == position) {
                        break;
                    }
                }
                else {
                    if(currentTile.isUnoccupied()) {
                        break;
                    }
                    if(currentTile == playerValue || tileReader.getCoordinates().equals(position)) {
                        result.addAll(buffer);
                        break;
                    }
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
                game.getGameStats().getAllCoordinatesWhereTileIs(player.getPlayerValue()));

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

    private static void executeInversionLogic(Game game) {

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

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Bomb move logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    public static void executeBombMove(Game game, BombMove move) {
        if (game.getPlayer(move.getPlayerNumber()).getBombs() == 0) {
            throw new RuntimeException("No bombs available :(");
        }

        for (Coordinates bombedTile : getAllTilesToBeBombed(game, move.getCoordinates())) {
            game.setTile(bombedTile, Tile.WALL);
        }

        game.getPlayer(move.getPlayerNumber()).decrementBombs();
    }

    private static Set<Coordinates> getAllTilesToBeBombed(Game game, Coordinates coordinates) {
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
