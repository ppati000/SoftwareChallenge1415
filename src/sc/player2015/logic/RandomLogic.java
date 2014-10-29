package sc.player2015.logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
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
import static tk.ppati000.logging.Logging.logger;

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
                        
                    for (SetMove possibleMove : possibleMoves) {

                        int[] coordinates = possibleMove.getSetCoordinates();

                            //only take fields that are not on the edge of the board
                        //now count the fish surrounding the field
                        ArrayList<int[]> surroundingCoords = CoordCalc.getSurroundingCoordinates(coordinates);
                        int surroundingFishCount = 0;
                        boolean friendlyPenguinOnField = false;
                        for (int i = 0; i < surroundingCoords.size(); i++) {
                            Field surroundingField = gameState.getBoard().getField(surroundingCoords.get(i)[0], surroundingCoords.get(i)[1]);
                            if (surroundingField.hasPenguin() && surroundingField.getPenguin().getOwner() == currentPlayer.getPlayerColor()) {
                                friendlyPenguinOnField = true;
                            }
                            surroundingFishCount += surroundingField.getFish();
                        }

                        /*if this is the highest number of fish surrounding a field as of now,
                         update the max fish count and the selected move*/
                        if (surroundingFishCount > maxSurroundingFishCount && !friendlyPenguinOnField) {
                            maxSurroundingFishCount = surroundingFishCount;
                            selection = possibleMove;
                        }

                    }

                    System.out.println("*** sende zug: SET ");

                    System.out.println("*** setze Pinguin auf x="
                            + selection.getSetCoordinates()[0] + ", y="
                            + selection.getSetCoordinates()[1]);
                    sendAction(selection);
		} else {
			MoveRanking ranking = new MoveRanking(gameState);
                        Move selection = ranking.getBestMove();
                        
			if (selection == null){
                            logger.info("*** No RunMoves left. Performing NullMove");
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
