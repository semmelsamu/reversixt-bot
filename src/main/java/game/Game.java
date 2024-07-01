package game;

import board.Board;
import board.Coordinates;
import board.Tile;
import exceptions.GamePhaseNotValidException;
import exceptions.MoveNotValidException;
import game.logic.MoveCalculator;
import game.logic.MoveExecutor;
import move.ChoiceMove;
import move.InversionMove;
import move.Move;
import util.Logger;
import util.NullLogger;

import java.util.Map;
import java.util.Set;

public class Game implements Cloneable {

    Logger logger = new Logger(this.getClass().getName());

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The actual game board.
     */
    private Board board;

    /**
     * Array containing all players with their information
     */
    private Player[] players;

    public final GameConstants constants;

    int currentPlayer;

    private int moveCounter;

    private GamePhase phase;

    Set<Move> validMoves;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Caches
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * For each tile value, caches the set of coordinates that have this value.
     */
    public CoordinatesGroupedByTile coordinatesGroupedByTile;

    /**
     * Caches the total number of tiles occupied.
     */
    public TotalTilesOccupiedCounter totalTilesOccupiedCounter;

    /**
     * Caches the communities on the board.
     */
    public Communities communities;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Create a new game.
     */
    public Game(int initialPlayers, int initialOverwriteStones, int initialBombs, int bombRadius,
                Board board) {

        logger.verbose("Creating game");

        // Set board
        this.board = board;

        // Initialize players
        players = new Player[initialPlayers];
        for (int i = 0; i < initialPlayers; i++) {
            players[i] = new Player(Tile.fromChar((char) (i + 1 + '0')), initialOverwriteStones,
                    initialBombs);
        }

        constants =
                new GameConstants(initialPlayers, initialOverwriteStones, initialBombs, bombRadius);

        // Caching
        coordinatesGroupedByTile = new CoordinatesGroupedByTile(this);
        totalTilesOccupiedCounter = new TotalTilesOccupiedCounter(this);
        // communities = new Communities(this);

        phase = GamePhase.BUILD;

        moveCounter = 1;
        findValidPlayer();
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Players
    |
    |-----------------------------------------------------------------------------------------------
    */

    private void rotateCurrentPlayer() {
        currentPlayer = (currentPlayer % players.length) + 1;

        if (communities != null && communities.simulating != null) {
            validMoves = MoveCalculator.getValidMovesForPlayer(this, currentPlayer,
                    communities.simulating.coordinates);
        } else {
            validMoves = MoveCalculator.getValidMovesForPlayer(this, currentPlayer, null);
        }
    }

    void findValidPlayer() {

        if (phase == GamePhase.END) {
            return;
        }

        if (currentPlayer < 1 || currentPlayer > players.length) {
            nextPlayer();
            return;
        }

        if (validMoves.isEmpty()) {
            nextPlayer();
            return;
        }

        if (getPlayer(currentPlayer).isDisqualified()) {
            nextPlayer();
        }
    }

    public void nextPlayer() {
        if (phase == GamePhase.END) {
            throw new GamePhaseNotValidException("Cannot calculate next player in end phase");
        }

        int oldPlayer = currentPlayer;
        int i = 0;

        do {
            rotateCurrentPlayer();

            if (oldPlayer == currentPlayer && validMoves.isEmpty()) {
                if (phase == GamePhase.BUILD) {
                    logger.log(
                            "No more player has any moves in the coloring phase, entering bomb " +
                                    "phase");
                    phase = GamePhase.BOMB;
                    communities = null;
                    rotateCurrentPlayer();
                    oldPlayer = currentPlayer;
                } else if (phase == GamePhase.BOMB) {
                    logger.log("No more player has any bomb moves, entering end");
                    phase = GamePhase.END;
                    // Set player to no player because the game ended
                    currentPlayer = 0;
                    return;
                }
            }

            if (i++ > 100) {
                throw new RuntimeException("Too many iterations");
            }

        } while (validMoves.isEmpty() || getPlayer(currentPlayer).isDisqualified());

        logger.debug("Current player is now " + currentPlayer);
    }

    public void disqualifyPlayer(int player) {
        getPlayer(player).disqualify();
        if (getPlayer(currentPlayer).isDisqualified()) {
            nextPlayer();
        }
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int playerNumber) {
        return players[playerNumber - 1];
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Moves
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void executeMove(Move move) {
        if (!validMoves.contains(move)) {
            throw new MoveNotValidException("Tried to execute a move that is not valid: " + move);
        }

        if (move instanceof InversionMove || move instanceof ChoiceMove) {
            logger.log("Disabling Communities: Inversion/Choice Move");
            communities = null;
        }

        MoveExecutor.executeMove(this, move);

        if (communities != null) {
            communities.update(move.getCoordinates());
        }

        moveCounter++;
        nextPlayer();
    }

    public Set<Move> getValidMoves() {
        return validMoves;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Board
    |
    |-----------------------------------------------------------------------------------------------
    */

    public int getHeight() {
        return board.getHeight();
    }

    public int getWidth() {
        return board.getWidth();
    }

    public Tile getTile(Coordinates position) {
        return board.getTile(position);
    }

    public void setTile(Coordinates position, Tile value) {
        coordinatesGroupedByTile.updateCoordinates(position, board.getTile(position), value);
        board.setTile(position, value);
    }

    public Map<Short, Short> getTransitions() {
        return board.getTransitions();
    }

    public boolean coordinatesLayInBoard(Coordinates position) {
        return board.coordinatesLayInBoard(position);
    }

    public GamePhase getPhase() {
        return phase;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Overrides
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Game\n");

        result.append("Initial players: ").append(constants.initialPlayers()).append("\n");
        result.append("Initial overwrite stones: ").append(constants.initialOverwriteStones())
                .append("\n");
        result.append("Initial bombs: ").append(constants.initialBombs()).append("\n");
        result.append("Bomb radius: ").append(constants.bombRadius()).append("\n");

        result.append("Phase: ").append(phase).append("\n");
        result.append("Move: ").append(moveCounter).append("\n");

        result.append("Players (Overwrite Stones / Bombs)").append("\n");
        for (Player player : players) {
            result.append("- ").append(player.getPlayerValue().toString()).append(" (")
                    .append(player.getOverwriteStones()).append(" / ").append(player.getBombs())
                    .append(")");
            if (player.isDisqualified()) {
                result.append(" X");
            }
            if (player.getPlayerValue().toPlayerIndex() + 1 == currentPlayer) {
                result.append(" *");
            }
            result.append("\n");
        }

        result.append(board.toString());

        return result.toString();
    }

    @Override
    public Game clone() {
        try {

            Game clone = (Game) super.clone();

            clone.board = this.board.clone();

            clone.players = new Player[this.players.length];
            for (int i = 0; i < this.players.length; i++) {
                clone.players[i] = this.players[i].clone();
            }

            if (!(clone.logger instanceof NullLogger)) {
                clone.logger = new NullLogger("");
            }

            // Caches
            clone.coordinatesGroupedByTile = this.coordinatesGroupedByTile.clone();
            clone.totalTilesOccupiedCounter = this.totalTilesOccupiedCounter.clone();
            if (communities != null) {
                clone.communities = this.communities.clone();
                clone.communities.game = clone;
                for (var community : clone.communities.communities) {
                    community.game = clone;
                }
            }

            return clone;

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }
}
