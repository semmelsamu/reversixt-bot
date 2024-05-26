package evaluation;

import board.*;
import game.Game;
import game.GamePhase;
import move.Move;

import java.util.*;

public class StaticGameStats {
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
     * Width of board
     */
    private final int width;
    /**
     * Width of board
     */
    private final int height;

    private final int[][] tileRatings;

    private short numberOfReachableTiles;

    public StaticGameStats(Game initialGame, int initialPlayers, int initialOverwriteStones,
                           int initialBombs, int bombRadius) {
        this.initialPlayers = initialPlayers;
        this.initialOverwriteStones = initialOverwriteStones;
        this.initialBombs = initialBombs;
        this.bombRadius = bombRadius;
        width = initialGame.getWidth();
        height = initialGame.getHeight();
        tileRatings = calculateTileRatings(initialGame);
        numberOfReachableTiles = 0;
    }

    private int[][] calculateTileRatings(Game game) {
        int[][] tileRatings = new int[height][width];
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                if(game.getTile(new Coordinates(x, y)) == Tile.WALL){
                    tileRatings[y][x] = 0;
                    continue;
                }
                tileRatings[y][x] = calculateParicularTileRating(x, y, game);
            }
        }
        return tileRatings;
    }

    public void initializeReachableTiles(Game initialGame) {
        Game purposeGame = initialGame.clone();
        while(purposeGame.getPhase() != GamePhase.PHASE_2){
            Set<Move> validMovesForCurrentPlayer = purposeGame.getValidMovesForCurrentPlayer();
            List<Move> ListofvalidMoves = new ArrayList<>(validMovesForCurrentPlayer);
            int randomIndex = (int) (Math.random() * validMovesForCurrentPlayer.size());
            if(!validMovesForCurrentPlayer.isEmpty()){
                Move randomMove = ListofvalidMoves.get(randomIndex);
                purposeGame.executeMove(randomMove);
            }
            else{
                break;
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(purposeGame.getTile(new Coordinates(x,y)).isPlayer()){
                    numberOfReachableTiles++;
                }
            }
        }
        printAmountOfReachableTiles();
    }

    public void printAmountOfReachableTiles(){
        System.out.println();
        System.out.println("\nNumber of reachable tiles: " + numberOfReachableTiles);
    }

    /**
     * Default tile rating: 1
     * Checks all 4 angles on, whether they are only open in one direction
     * Such angles are valuable, as tiles can "attack" in the angle but can not be "attacked"
     * 1 bonuspoint for every angle, that fullfills the criterion
     * Rating for WALLS: 0
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Tile rating as an Integer
     */

    private int calculateParicularTileRating(int x, int y, Game game){
        int tileRating = 1;
        Direction[] halfOfAllDirections = Arrays.copyOfRange(Direction.values(),0, 4);
        for(Direction direction : halfOfAllDirections) {
            TileReader tileReader = new TileReader(game, new Coordinates(x, y), direction);
            TileReader oppositeDirectionTileReader =
                    new TileReader(game, new Coordinates(x, y), direction.getOppositeDirection());
            if(tileReader.hasNext() && !oppositeDirectionTileReader.hasNext()){
                tileRating++;
                continue;
            }
            if(!tileReader.hasNext() && oppositeDirectionTileReader.hasNext()){
                tileRating++;
            }
        }
        return tileRating;
    }

    /**
     * Getter
     */
    public int getBombRadius() {
        return bombRadius;
    }

    public int getInitialPlayers() {
        return initialPlayers;
    }

    public int getInitialOverwriteStones() {
        return initialOverwriteStones;
    }

    public int getInitialBombs() {
        return initialBombs;
    }

    public int[][] getTileRatings() {
        return tileRatings;
    }
}
