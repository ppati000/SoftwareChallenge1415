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

    private final int FISH_MULTIPLIER = 100;
    private final int SURROUNDING_FISH_MULTIPLIER = 100;

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
            if (possibleMoves.get(i).getClass() == NullMove.class) {
                RunMove possibleMove = (RunMove) possibleMoves.get(i);
                int[] coordinatesOfMove = possibleMove.getRunCoordinates();
                rankings[i] = FISH_MULTIPLIER * gameState.getBoard().getFishNumber(coordinatesOfMove[2], coordinatesOfMove[3]);

                ArrayList<int[]> surroundingCoords = CoordCalc.getSurroundingCoordinates(coordinatesOfMove);
                for (int[] surroundingCoord : surroundingCoords) {
                    rankings[i] += (SURROUNDING_FISH_MULTIPLIER * gameState.getBoard()
                            .getFishNumber(surroundingCoord[0], surroundingCoord[1])) / surroundingCoords.size();
                }
            }
            else {
                rankings[i] = 0;
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
