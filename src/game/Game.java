package game;

import board.*;
import player.Player;
import player.Stone;
import util.File;
import util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Game {

    /*
    |--------------------------------------------------------------------------
    | Member variables
    |--------------------------------------------------------------------------
    */

    /**
     * The number of players this game usually starts with.
     */
    private final int initialPlayers;

    /**
     * The number of overwrite stones each player has in the beginning.
     */
    private final int initialOverwriteStones;

    /**
     * The number of bombs each player has in the beginning.
     */
    private final int initialBombs;

    /**
     * The amount of steps from the center of the explosion a bomb blows tiles up.
     */
    private final int bombRadius;

    /**
     * The actual game board.
     */
    private Board board;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    /**
     * Create a new game.
     */
    public Game(int initialPlayers, int initialOverwriteStones, int initialBombs, int bombRadius, Board board) {
        Logger.log("Creating game");
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        this.board = board;

        board.print();
    }

    /*
    |--------------------------------------------------------------------------
    | Factories
    |--------------------------------------------------------------------------
    */

    /**
     * Creates a new game from input lines.
     * @param lines A list of lines, where each line is trimmed and all lowercase (normalized).
     * @return The new game.
     */
    private static Game createFromLines(LinkedList<String> lines) {

        Logger.log("Creating game from lines");

        // We unshift line for line and parse it

        // Parsing initial player data
        int initialPlayers = Integer.parseInt(lines.remove(0));
        Logger.verbose("Initial players: " + initialPlayers);
        int initialOverwriteStones = Integer.parseInt(lines.remove(0));
        Logger.verbose("Initial overwrite stones: " + initialOverwriteStones);

        // Parsing initial bomb data
        String[] bombs = lines.remove(0).split((" "));
        int initialBombs = Integer.parseInt(bombs[0]);
        Logger.verbose("Initial bombs: " + initialBombs);
        int bombRadius = Integer.parseInt(bombs[1]);
        Logger.verbose("Bomb radius: " + bombRadius);

        // Create board from remaining lines
        Board board = Board.createFromLines(lines);

        // Creating game
        return new Game(initialPlayers, initialOverwriteStones, initialBombs, bombRadius, board);
    }

    private static Game createFromString(String string) {

        Logger.log("Creating game from string");

        // Mind lines can be separated by nl or cr+nl
        String[] lines = string.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            // Normalise each line
            lines[i] = lines[i].trim().toLowerCase();
        }

        return createFromLines(new LinkedList<>(Arrays.asList(lines)));
    }

    public static Game createFromFile(String filename) {

        Logger.log("Creating game from file " + filename);
        return createFromString(File.readFile(filename));
    }

    /**
     *
     * @param player
     * @return Valid moves for one player
     */
    public List<Coordinates> getValidMovesForPlayer(Player player){
        List<Coordinates> moves = new ArrayList<>();
        for(Tile s : player.getOccupiedTiles()){
            if(s.getValue() != player.getPlayerValue()){
                Logger.error("Wrong coordinates in " + player.getPlayerValue() + "'s List stones");
                continue;
            }
            moves.addAll(getValidMovesForPiece(s));
        }
        System.out.println("Valid moves for player " + player.getPlayerValue());
        for(Coordinates c : moves){
            System.out.println(c.x + " " + " " + c.y);
        }
        return moves;
    }

    /**
     *
     * @param ownPiece
     * @return Valid moves for one piece
     */
    private List<Coordinates> getValidMovesForPiece(Tile ownPiece){
        List<Coordinates> moves = new ArrayList<>();
        for(Direction d : Direction.values()){
            //currentDirection = d;

            moves.add(getValidMovesForPieceInDirection(ownPiece, d));

        }
        return moves;
    }

    /**
     *
     * @param ownPiece
     * @param direction
     * @return Valid moves for one piece for one of eight directions
     */
    private Coordinates getValidMovesForPieceInDirection(Tile ownPiece, Direction direction){
        boolean foundEmptyTile = false;
        Direction currentDirection = direction;
        Tile currentTile = ownPiece;
        Neighbour currentNeighbour = currentTile.getNeighbour(currentDirection);
        //terminates  if an empty Tile was found or if the neighbour is an own piece or if there is no neighbour
        while(!(foundEmptyTile) && currentNeighbour != null &&
                currentNeighbour.tile().getValue() != ownPiece.getValue())
        {
            currentTile = currentNeighbour.tile();
            switch (currentTile.getValue()) {
                case EMPTY:
                case CHOICE:
                case INVERSION:
                case BONUS:
                    foundEmptyTile = true;
                    break;
                default:
                    currentNeighbour = currentTile.getNeighbour(currentDirection);
                    if (currentNeighbour.directionChange() != null) {
                        currentDirection = currentNeighbour.directionChange();
                    }
                    currentNeighbour = currentTile.getNeighbour(currentDirection);
            }
        }
        if(foundEmptyTile){
            return currentTile.getPosition();
        }
        else{
            return null;
        }
    }
}
