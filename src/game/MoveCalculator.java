package game;

import board.Direction;
import board.Tile;
import player.Player;
import player.move.*;
import util.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class MoveCalculator {
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
     * Get all valid moves for this player
     */
    public Set<Move> getValidMoves() {
        Player currentPlayer = game.getCurrentPlayer();
        TileValue currentPlayerValue = currentPlayer.getPlayerValue();
        Logger.get().log("Searching for all valid moves for Player " + currentPlayerValue);
        Set<Move> moves = new TreeSet<>();
        for (Tile occupiedTile : currentPlayer.getOccupiedTiles()) {
            if (occupiedTile.getValue() != currentPlayerValue) {
                Logger.get().error("Wrong coordinates in Player" + currentPlayerValue + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(occupiedTile, currentPlayer));
        }
        return moves;
    }

    /**
     * @param ownPiece one piece of this player
     * @return Valid moves for one piece of this player
     */
    private Set<Move> getValidMovesForPiece(Tile ownPiece, Player currentPlayer) {
        Logger.get().verbose("Searching for valid moves originating from piece " + ownPiece);
        Set<Move> moves = new TreeSet<>();
        for (Direction direction : Direction.values()) {
            Neighbour neighbour = ownPiece.getNeighbour(direction);
            // check if there is a dead end
            if (neighbour == null) {
                continue;
            }
            TileValue neighbourValue = neighbour.tile().getValue();
            // check if tile value is seen as an enemy or if it's the same color
            if (TileValue.getAllFriendlyValues().contains(neighbourValue)
                    || neighbourValue == currentPlayer.getPlayerValue()) {
                continue;
            }
            Set<Move> move = getValidMoveForPieceInDirection(ownPiece, direction, currentPlayer);
            if (move != null) {
                moves.addAll(move);
            }
        }
        return moves;
    }

    /**
     * @param currentTile      one piece of this player
     * @param currentDirection one of eight directions
     * @return Valid moves for one piece for one of eight directions
     */
    private Set<Move> getValidMoveForPieceInDirection(Tile currentTile, Direction currentDirection,
                                                      Player currentPlayer) {

        Logger.get().verbose("Searching for valid moves in direction ");
        Tile firstTile = currentTile;
        Set<Move> movesPerDirection = new HashSet<>();
        Set<Tile> alreadyVisited = new HashSet<>();
        int howFarFromFirstTile = 0;
        // as long as there is an empty field
        while (!TileValue.getAllFriendlyValues().contains(currentTile.getValue())) {
            alreadyVisited.add(currentTile);
            Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);

            // check for a dead end
            if (currentNeighbour == null) {
                return movesPerDirection;
            }

            currentTile = currentNeighbour.tile();

            if (currentNeighbour.directionChange() != null) {
                currentDirection = currentNeighbour.directionChange();
            }

            if (alreadyVisited.contains(currentTile)) {
                if (currentTile == firstTile) {
                    return movesPerDirection;
                }
                continue;
            }

            if (currentPlayer.getOverwriteStones() != 0) {
                howFarFromFirstTile++;
                // overwrite stone logic
                if (currentTile.getValue().isPlayer() && currentTile.getValue() != currentPlayer.getPlayerValue()
                        && howFarFromFirstTile > 1) {
                    movesPerDirection.add(new Move(currentPlayer, currentTile));
                }

                // tile has the same value as another tile, but isn't the same tile and is more fare away than 1 -> overwrite stone on the first tile
                if (currentTile.getValue() == currentPlayer.getPlayerValue() && howFarFromFirstTile > 1) {
                    movesPerDirection.add(new Move(currentPlayer, firstTile));
                    return movesPerDirection;
                }
            }
        }
        // decide which move
        switch (currentTile.getValue()) {
            case CHOICE -> {
                for (Player player : game.getPlayers()) {
                    if (player != currentPlayer) {
                        movesPerDirection.add(new ChoiceMove(currentPlayer, currentTile, player));
                    }
                }
            }
            case INVERSION -> movesPerDirection.add(new InversionMove(currentPlayer, currentTile));
            case BONUS -> {
                movesPerDirection.add(new BonusMove(currentPlayer, currentTile, Bonus.BOMB));
                movesPerDirection.add(new BonusMove(currentPlayer, currentTile, Bonus.OVERWRITE_STONE));
            }
            default -> movesPerDirection.add(new Move(currentPlayer, currentTile));

        }

        return movesPerDirection;
    }
}
