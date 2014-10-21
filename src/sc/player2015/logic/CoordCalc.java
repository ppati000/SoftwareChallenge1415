/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sc.player2015.logic;

import java.util.ArrayList;
import java.util.Iterator;
import static tk.ppati000.logging.Logging.logger;

/**
 * 
 * @author Patrick P. (<a href="http://ppati000.tk/">ppati000.tk</a> or <a href="mailto:ppati.email@gmail.com">ppati.email@gmail.com</a>)
 * @since
 */
public class CoordCalc {

    /**
     * Checks whether coordinates are valid
     * @param coords int array with two coordinates, x: coords[0]; y: coords[1]
     * @return true if valid, false if not
     */
    public static boolean validCoords(int[] coords) {
        return !(coords[0] < 0 || coords[1] < 0 || coords[1] > 7 || (coords[1] % 2 == 0 && coords[0] > 6) || (coords[1] % 2 == 1 && coords[0] > 7));
    }

    /**
     * Returns the coordinates of all surrounding fields to a specific field.
     * @param fieldCoords the coordinates of the field, with fieldCoords[0]
     * being the x- and fieldCoords[1] being the y-coordinate
     * @return an ArrayList of arrays containing the surrounding field's coordinates, with
     * [0] being the x-, and [1] being the y-coordinate of a field
     */
    public static ArrayList getSurroundingCoordinates(int[] fieldCoords) {
        ArrayList<int[]> surroundingCoords = new ArrayList<>();
        surroundingCoords.add(new int[]{fieldCoords[0] - 1, fieldCoords[1]});
        surroundingCoords.add(new int[]{fieldCoords[0] + 1, fieldCoords[1]});
        /**
         * Here we calculate the rightmost x coordinate for the surrounding fields
         * in the rows above and below our field. If our field is in a long row
         * this number is the same as the x coordinate of our field. If it's
         * in a short row, we have to add 1 to that number.
         */
        int rightmostXCoord;
        if (fieldCoords[1] % 2 == 0) {
            rightmostXCoord = fieldCoords[0] + 1;
        } else {
            rightmostXCoord = fieldCoords[0];
        }
        surroundingCoords.add(new int[]{rightmostXCoord, fieldCoords[1] + 1});
        surroundingCoords.add(new int[]{rightmostXCoord, fieldCoords[1] - 1});
        surroundingCoords.add(new int[]{rightmostXCoord - 1, fieldCoords[1] + 1});
        surroundingCoords.add(new int[]{rightmostXCoord - 1, fieldCoords[1] - 1});
        for (Iterator<int[]> it = surroundingCoords.iterator(); it.hasNext();) {
            int[] surroundingCoord = it.next();
            if (!validCoords(surroundingCoord)) {
                it.remove();
            }
        }
        System.out.println("*** SURROUNDING COORDS: " + surroundingCoords.toArray().toString());
        return surroundingCoords;
    }

}
