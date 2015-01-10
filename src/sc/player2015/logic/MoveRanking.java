/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc.player2015.logic;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2015.GameState;
import sc.plugin2015.Move;
import sc.plugin2015.NullMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.SetMove;

/**
 * A ranking of Moves
 *
 * @author Patrick P. (<a href="http://ppati000.tk/">ppati000.tk</a> or
 * <a href="mailto:ppati.email@gmail.com">ppati.email@gmail.com</a>)
 * @since 10/21/2014
 */
public class MoveRanking {
    
    //CONSTANTS
    //positive factors
    /**
     * Number of credits added to the ranking for each fish on the destination field.
     * e.g. 2 fish and multiplier = 100 -> 200 credits
     */
    private final int FISH_MULTIPLIER = 100;
    /**
     * Number of credits added for the average number of fish on the adjacent fields.
     * e.g. 2.3 fish on average and SURROUNDING_FISH_MULTIPLIER = 100 -> 230 credits
     */
    private final int SURROUNDING_FISH_MULTIPLIER = 100;
    
    //negative factors
    /**
     * Number of credits subtracted from the ranking based on distance between start
     * and destination fields.
     * e.g. distance = 4, multiplier = 50 -> -200 credits
     */
    private final int DISTANCE_NORMAL_MULTIPLIER = 50;
    
    /**
     * Same as above, but for the last rounds of the game.
     */
    private final int DISTANCE_END_MULTIPLIER = 20;
    
    /**
     * Until which round we should use the normal distance multiplier.
     */
    private final int DISTANCE_NORMAL_UNTIL_ROUND = 25;
    
    
    List<Move> possibleMoves;
    GameState gameState;
    int[] rankings;

    public MoveRanking(GameState gameState) {
        this.gameState = gameState;
        this.possibleMoves = gameState.getPossibleMoves();
        rankings = new int[possibleMoves.size()];
    }

    private void rankMoves() {
        for (int i = 0; i < possibleMoves.size(); i++) {
            if (possibleMoves.get(i).getClass() == RunMove.class) {
                RunMove possibleMove = (RunMove) possibleMoves.get(i);
                int[] coordinatesOfMove = possibleMove.getRunCoordinates();
                rankings[i] = FISH_MULTIPLIER * gameState.getBoard().getFishNumber(coordinatesOfMove[2], coordinatesOfMove[3]);

                ArrayList<int[]> surroundingCoords = CoordCalc.getSurroundingCoordinates(coordinatesOfMove);
                for (int[] surroundingCoord : surroundingCoords) {
                    rankings[i] = rankings[i] + (SURROUNDING_FISH_MULTIPLIER * gameState.getBoard()
                            .getFishNumber(surroundingCoord[0], surroundingCoord[1])) / surroundingCoords.size();
                }
                
                if (gameState.getRound() <= DISTANCE_NORMAL_UNTIL_ROUND){
                    rankings [i] = (int) (rankings[i] - DISTANCE_NORMAL_MULTIPLIER * CoordCalc.distance(possibleMove.getRunCoordinates()));
                }
                else {
                    rankings [i] = (int) (rankings[i] - DISTANCE_END_MULTIPLIER * CoordCalc.distance(possibleMove.getRunCoordinates()));
                }
            }
            else { //if the Move is a NullMove, give it a very low rating
                rankings[i] = -420420;
            }
        }
    }

    public Move getBestMove() {
        int highestRanking = 0;
        int highestRankedIndex = 0;
        for (int i = 0; i < rankings.length; i++) {
            if (rankings[i] > highestRanking) {
                highestRanking = rankings[i];
                highestRankedIndex = i;
            }
        }
        return possibleMoves.get(highestRankedIndex);
    }

}
