package sc.player2015.logic;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import sc.player2015.Starter;
import sc.plugin2015.Field;
import sc.plugin2015.NullMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.GameState;
import sc.plugin2015.IGameHandler;
import sc.plugin2015.Move;
import sc.plugin2015.MoveType;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.SetMove;
import sc.shared.GameResult;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik, die ihre Zuege zufaellig
 * waehlt, aber gueltige Zuege macht. Ausserdem werden zum Spielverlauf
 * Konsolenausgaben gemacht.
 */
public class RandomLogic implements IGameHandler {

	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	/*
	 * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse
	 * einmalig erzeugt wird und darn immer zur Verfuegung steht.
	 */
	private static final Random rand = new SecureRandom();

	/**
	 * Erzeugt ein neues Strategieobjekt, das zufaellige Zuege taetigt.
	 * 
	 * @param client
	 *            Der Zugrundeliegende Client der mit dem Spielserver
	 *            kommunizieren kann.
	 */
	public RandomLogic(Starter client) {
		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {

		System.out.println("*** Das Spiel ist beendet");
	}
        
        /**
         * Returns the coordinates of all surrounding fields to a specific field.
         * NOTE: THIS DOES NOT WORK WITH FIELDS ON THE EDGE OF THE BOARD AS OF NOW (will probably crash)
         * @param fieldCoordinates the coordinates of the field, with fieldCoordinates[0] 
         * being the x- and fieldCoordinates[1] being the y-coordinate
         * @return a 2D array with the surrounding field's coordinates, with
         * [x][0] being the x-, and [x][1] being the y-coordinate of a field
         */
        private int[][] getSurroundingCoordinates(int[] fieldCoordinates){
            //6 surrounding fields and 2 coordinates for each, so:
            int[][] surroundingCoordinates = new int [6][2];
            
            //the coordinates of the two surrounding fields in the same row
            surroundingCoordinates[0][0] = fieldCoordinates[0]-1;
            surroundingCoordinates[0][1] = fieldCoordinates[1];
            
            surroundingCoordinates[1][0] = fieldCoordinates[0]+1;
            surroundingCoordinates[1][1] = fieldCoordinates[1];
            
            /**
             * Here we calculate the highest x coordinate for the surrounding fields
             * in the rows above and below our field. If our field is in a long row
             * this number is the same as the x coordinate of our field. If it's
             * in a short row, we have to add 1 to that number.
             */
            int highestXCoordinate;
            if (fieldCoordinates[1] % 2 == 0){
                highestXCoordinate = fieldCoordinates[0]+1;
            }
            else {
                highestXCoordinate = fieldCoordinates[0];
            }
            
            //Now we can calculate the coordinates of the surrounding fields in the rows above and below
            //upper right field
            surroundingCoordinates[2][0] = highestXCoordinate;
            surroundingCoordinates[2][1] = fieldCoordinates[1] + 1;
            
            //lower right field
            surroundingCoordinates[3][0] = highestXCoordinate;
            surroundingCoordinates[3][1] = fieldCoordinates[1]-1;
            
            //upper left field
            surroundingCoordinates[4][0] = highestXCoordinate - 1;
            surroundingCoordinates[4][1] = fieldCoordinates[1] + 1;
            
            //lower left field
            surroundingCoordinates[5][0] = highestXCoordinate - 1;
            surroundingCoordinates[5][1] = fieldCoordinates[1] - 1;
            
            return surroundingCoordinates;
        }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRequestAction() {
		System.out.println("*** Es wurde ein Zug angefordert");
		if (gameState.getCurrentMoveType() == MoveType.SET) {
			List<SetMove> possibleMoves = gameState.getPossibleSetMoves();
                        int maxSurroundingFishCount = 0;
                        
                        //initialize selection so the compiler doesn't complain
                        SetMove selection = possibleMoves.get(0);
                        
                        for (SetMove possibleMove : possibleMoves){
                            
                            int[] coordinates = possibleMove.getSetCoordinates();
                            
                            //only take fields that are not on the edge of the board
                            if (coordinates[1] != 0 && coordinates[1] != 7 && coordinates[0] != 0 
                                    && !(coordinates[1] % 2 == 0 && coordinates[0] == 6) 
                                    && !(coordinates[1] % 2 != 0 && coordinates[0] == 7)){
                                
                                //now count the fish surrounding the field
                                int[][] surroundingCoordinates = getSurroundingCoordinates(coordinates);
                                int surroundingFishCount = 0;
                                boolean friendlyPenguinOnField = false;
                                for (int i = 0; i < 5; i++){
                                    Field surroundingField = gameState.getBoard().getField(surroundingCoordinates[i][0], surroundingCoordinates[i][1]);
                                    if (surroundingField.hasPenguin() && surroundingField.getPenguin().getOwner() == currentPlayer.getPlayerColor()){
                                        friendlyPenguinOnField = true;
                                    }
                                    surroundingFishCount += surroundingField.getFish();
                                }
                                
                                /*if this is the highest number of fish surrounding a field as of now,
                                update the max fish count and the selected move*/
                                if (surroundingFishCount > maxSurroundingFishCount && !friendlyPenguinOnField){
                                    maxSurroundingFishCount = surroundingFishCount;
                                    selection = possibleMove;
                                }
                            
                            }
                            
                        }
                        
                        
                        
			System.out.println("*** sende zug: SET ");
                        
			System.out.println("*** setze Pinguin auf x="
					+ selection.getSetCoordinates()[0] + ", y="
					+ selection.getSetCoordinates()[1]);
			sendAction(selection);
		} else {
			List<Move> possibleMoves = gameState.getPossibleMoves();
			System.out.println("*** sende zug: RUN ");
			Move selection = possibleMoves.get(rand.nextInt(possibleMoves
					.size()));
			if (selection.getClass() == NullMove.class)
				System.out.println("*** Ich setze aus.");
			else {
				RunMove runSelection = (RunMove) selection;
				System.out.println("*** bewege Pinguin von x="
						+ runSelection.fromX + ", y=" + runSelection.fromY
						+ " auf x=" + runSelection.toX + ", y=" + runSelection.toY);
			}
			sendAction(selection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;

		System.out.println("*** Spielerwechsel: " + player.getPlayerColor());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();

		System.out.print("*** Das Spiel geht vorran: Zug = "
				+ gameState.getTurn());
		System.out.println(", Spieler = " + currentPlayer.getPlayerColor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}

}
