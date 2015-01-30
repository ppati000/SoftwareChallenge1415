/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc.player2015.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import sc.plugin2015.GameState;
import sc.plugin2015.Move;
import sc.plugin2015.NullMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.SetMove;
import sc.plugin2015.util.InvalidMoveException;
import static tk.ppati000.logging.Logging.logger;

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
     * The numbers following the first one set the number of credits for fish we
     * could get from the next+1 move and so on.
     * As of now, only the first two values are used.
     */
    private final int[] FISH_MULTIPLIERS = {100, 50, 25, 10, 5};
    
    /**
     * Number of credits added to the ranking for the number of possible moves that
     * we would have in the next (next+1, next+2) round if we chose that move.
     * e.g. POSSIBLE_MOVES_MULTIPLIERS[0] = 20: if we performed move a move that
     * would result in 6 possible moves available to us in the next round, this
     * would add 20*6 to the ranking.
     * For next+1 (two rounds from now), this computes the average of possible
     * moves we would have three rounds from now and multiplies it with
     * POSSIBLE_MOVES_MULTIPLIERS[1]
     * As of now, only the first two values are used.
     */
    private final int[] POSSIBLE_MOVES_MULTIPLIERS = {20, 5, 1, 1, 1};
    
    /**
     * NOTE: Apparently, this is not used anymore.
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
        rankMoves(); //do the math computer
    }
    
    /**
     * This simulates performing a RunMove, updates rankings, and calls itself to
     * see how promising future moves will be, simulating them too.
     * @param move the move to be simulated
     * @param gState your current gameState
     * @param rankingIndex the index for the ranking array (e.g. if you want to update rankings[0], set this to 0)
     * @param timesRepeated tells the function how many times it has called itself. Set to 0.
     * @param lastPossibleMoves tells the function how many future possible moves there are in future rounds. Set to 1.
     * @throws InvalidMoveException if an invalid move is simulated (should never happen)
     * @throws CloneNotSupportedException if whatever happens
     */
    private void simulateMove(RunMove move, GameState gState, int rankingIndex, int timesRepeated, int lastPossibleMoves)
            throws InvalidMoveException, CloneNotSupportedException {
        
        //clone the gameState so we don't perform anything on the real one
        GameState newGameState = (GameState) gState.clone();
        
        //if thos isn't the first time this function is called, tell rankByFishCount to compute an average
        //number of fish
        boolean useAverage = (timesRepeated != 0);
        
        //do some ranking magic with fish counts, then perform the move
        rankByFishCount(move, newGameState, rankingIndex, FISH_MULTIPLIERS[timesRepeated], useAverage);
        move.perform(newGameState, newGameState.getCurrentPlayer());

        //add sth to the ranking based on how many possible moves we have after the earlier move was performed
        rankings[rankingIndex] += POSSIBLE_MOVES_MULTIPLIERS[timesRepeated] * newGameState.getPossibleMoves().size() / lastPossibleMoves;

        if (timesRepeated < 1) { //if this hasn't called itself yet
            //for each future possible move, call this function again.
            for (int i = 0; i < newGameState.getPossibleMoves().size(); i++) {
                if (newGameState.getPossibleMoves().get(i) instanceof RunMove) {
                    RunMove runMove = (RunMove) newGameState.getPossibleMoves().get(i);
                    simulateMove(runMove, newGameState, rankingIndex, timesRepeated + 1, newGameState.getPossibleMoves().size() * lastPossibleMoves);
                }
            }
        }
    }
    
    /**
     * This iteerates over all possible moves, simulates them using simulateMove
     * and does some ranking stuff.
     */
    private void rankMoves() {
        
        for (int i = 0; i < possibleMoves.size(); i++) {

            RunMove currentMove;
            if (possibleMoves.get(i) instanceof RunMove) {
                try {
                    //simulate the move using simulateMove
                    currentMove = (RunMove) possibleMoves.get(i);
                    simulateMove(currentMove, gameState, i, 0, 1);
                    
                    //subtract something from the ranking based on how far away the destination is
                    if (gameState.getRound() <= DISTANCE_NORMAL_UNTIL_ROUND) {
                        rankings[i] = (int) (rankings[i] - DISTANCE_NORMAL_MULTIPLIER * CoordCalc.distance(currentMove.getRunCoordinates()));
                    } else {
                        rankings[i] = (int) (rankings[i] - DISTANCE_END_MULTIPLIER * CoordCalc.distance(currentMove.getRunCoordinates()));
                    }
                } catch (InvalidMoveException ex) {
                    Logger.getLogger(MoveRanking.class.getName()).log(Level.SEVERE, null, ex);
                    rankings[i] = -1337420;
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(MoveRanking.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else { //if the move is no RunMove, it sucks and should get a very low rating;
                rankings[i] = -420420; //420 == high, however this has a minus so it's low. This is like flying backwards on a jet ski
            }
        }
    }
    
    /**
     * This adds something to the ranking based on the number of fish at the destination field.
     * @param move the move you are testing
     * @param g the current gameState
     * @param index what index to use for the rankings array
     * @param multiplier what multiplier to use
     * @param average if we should compute an average or not. We don't need an average
     * for the first move, but for the future moves. That sucks to explain.
     */
    private void rankByFishCount(RunMove move, GameState g, int index, int multiplier, boolean average){
        int[] coordinatesOfMove = move.getRunCoordinates();
        if (average){
            rankings[index] += multiplier * g.getBoard().getFishNumber(coordinatesOfMove[2], coordinatesOfMove[3]) / g.getPossibleMoves().size();
        }
        else {
            rankings[index] += multiplier * g.getBoard().getFishNumber(coordinatesOfMove[2], coordinatesOfMove[3]);
        }
    }
    
    
    /**
     * Get the best move we have.
     * @return the best move ever
     */
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
