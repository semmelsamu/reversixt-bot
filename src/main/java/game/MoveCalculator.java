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
import java.util.stream.Collectors;

public class MoveCalculator {

    Logger logger = new Logger(this.getClass().getName());
    private Game game;

    public MoveCalculator(Game game) {
        this.game = game;
    }

    /*
    |--------------------------------------------------------------------------
    | Functions for calculating valid moves
    |--------------------------------------------------------------------------
    */

    /**
     * @param player Player for which the moves are calculated
     * @return A set of all valid moves for this player
     */
    public Set<Move> getValidMovesForPlayer(Player player) {
        logger.log("Searching for all valid moves for Player " + player.getPlayerValue());

        if (game.getGamePhase().equals(GamePhase.PHASE_2)) {
            return getAllBombMoves(player);
        }

        Set<Move> moves = new HashSet<>();
        for (Coordinates occupiedTile : game.getAllCoordinatesWhereTileIs(
                player.getPlayerValue())) {
            if (game.getTile(occupiedTile) != player.getPlayerValue()) {
                logger.error("Wrong coordinates in Player" + player.getPlayerValue() +
                        "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(occupiedTile, player));
        }

        if (player.getOverwriteStones() > 0) {
            // Add overwrite moves on expansion tiles
            for (var coordinate : game.gameStats.getAllCoordinatesWhereTileIs(Tile.EXPANSION)) {
                moves.add(new OverwriteMove(player, coordinate));
            }
        }

        logger.debug("Valid moves for Player " + player.getPlayerValue() + ":\n" +
                moves.stream().map(move -> "    " + move).collect(Collectors.joining("\n")));

        return moves;
    }

    private Set<Move> getAllBombMoves(Player player) {

        Set<Move> result = new HashSet<>();

        // Bombs can be thrown on every tile which is not a wall.
        for (Tile tile : Tile.values()) {
            if (tile == Tile.WALL) {
                continue;
            }

            for (Coordinates position : game.getGameStats().getAllCoordinatesWhereTileIs(tile)) {
                result.add(new BombMove(player, position));
            }

        }
        return result;
    }

    /**
     * @param ownTileCoordinates one piece of this player
     * @param player             Tile of player that moves are calculated for
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Coordinates ownTileCoordinates, Player player) {
        logger
                .verbose("Searching for valid moves originating from piece " + ownTileCoordinates);
        Set<Move> moves = new HashSet<>();
        for (Direction direction : Direction.values()) {
            TileReader tileReader = new TileReader(game, ownTileCoordinates, direction);
            // Check if tile has a neighbour in this direction
            if (!tileReader.hasNext()) {
                continue;
            }
            tileReader.next();
            Tile firstNeighbourTile = tileReader.getTile();
            // Check if first neighbour tile is unoccupied
            if (firstNeighbourTile.isUnoccupied()) {
                continue;
            }

            // Check if first neighbour tile is an own tile
            if (firstNeighbourTile == player.getPlayerValue()) {
                continue;
            }

            // Check if the neighbour is the same tile due to a transition
            if (tileReader.getCoordinates() == ownTileCoordinates) {
                continue;
            }

            Set<Move> movesInDirection = getValidMovesForPieceInDirection(tileReader, player);
            if (movesInDirection != null) {
                moves.addAll(movesInDirection);
            }
        }

        return moves;
    }

    /**
     * @param tileReader tileReader with coordinates and direction of first neighbour of own tile
     * @param player     Tile of player that moves are calculated for
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMovesForPieceInDirection(TileReader tileReader, Player player) {
        logger.verbose("Searching for valid moves in direction ");

        Set<Move> movesPerDirection = new HashSet<>();
        Tile currentTile = tileReader.getTile();
        Coordinates currentCoordinates = tileReader.getCoordinates();
        boolean tilesBetweenExistingAndNewPiece = false;
        // As long as there is an ococcupied tile
        while (!currentTile.isUnoccupied()) {

            // Check if there is a dead end
            if (!tileReader.hasNext()) {
                return movesPerDirection;
            }

            tileReader.next();

            // If there are no tiles between existing and new piece
            if (!tilesBetweenExistingAndNewPiece) {
                if (currentCoordinates != tileReader.getCoordinates()) {
                    tilesBetweenExistingAndNewPiece = true;
                }
            }
            currentTile = tileReader.getTile();
            currentCoordinates = tileReader.getCoordinates();

            // Overwrite Logic
            if (player.getOverwriteStones() > 0) {
                // Overwrite stone logic
                if (currentTile.isPlayer() && tilesBetweenExistingAndNewPiece) {
                    movesPerDirection.add(new OverwriteMove(player, currentCoordinates));
                }
            }

            // If an own tile is overwritten, return because this tile is handled separately
            if (currentTile == player.getPlayerValue()) {
                return movesPerDirection;
            }
        }
        // If necessary create special move
        switch (currentTile) {
            case CHOICE -> {
                for (Player currentPlayer : game.getPlayers()) {
                    if (currentPlayer.getPlayerValue() != currentTile) {
                        movesPerDirection.add(
                                new ChoiceMove(player, currentCoordinates, currentPlayer));
                    }
                }
            }
            case INVERSION -> movesPerDirection.add(new InversionMove(player, currentCoordinates));
            case BONUS -> {
                movesPerDirection.add(new BonusMove(player, currentCoordinates, Bonus.BOMB));
                movesPerDirection.add(
                        new BonusMove(player, currentCoordinates, Bonus.OVERWRITE_STONE));
            }
            default -> movesPerDirection.add(new NormalMove(player, currentCoordinates));

        }

        return movesPerDirection;
    }
}
