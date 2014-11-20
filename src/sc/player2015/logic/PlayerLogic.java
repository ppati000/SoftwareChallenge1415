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
public class PlayerLogic implements IGameHandler {

	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	/**
	 * Creates a PlayerLogic object.	 * 
	 * @param client Der Zugrundeliegende Client der mit dem Spielserver 
         * kommunizieren kann.
	 */
	public PlayerLogic(Starter client) {
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
                
                
                /*
                THIS IS FOR THE FIRST FOUR MOVES OF THE GAME
                Here, we choose where to deploy our penguins based the number of
                fish on the surrounding fields.
                */
		if (gameState.getCurrentMoveType() == MoveType.SET) {
			List<SetMove> possibleMoves = gameState.getPossibleSetMoves();
                        int maxSurroundingFishCount = 0;
                        
                        //initialize selection so the compiler doesn't complain
                        SetMove selection = possibleMoves.get(0);
                        
                    for (SetMove possibleMove : possibleMoves) { //iterate through all the fields we could put our penguin on

                        int[] coordinates = possibleMove.getSetCoordinates();
                        
                        //get an ArrayList with the coords of the surrounding fields
                        ArrayList<int[]> surroundingCoords = CoordCalc.getSurroundingCoordinates(coordinates);
                        int surroundingFishCount = 0;
                        boolean friendlyPenguinOnField = false;
                        
                        //Get the number of fish on the surrounding fields, and check if there are friendly penguins on them
                        for (int i = 0; i < surroundingCoords.size(); i++) {
                            Field surroundingField = gameState.getBoard().getField(surroundingCoords.get(i)[0], surroundingCoords.get(i)[1]);
                            if (surroundingField.hasPenguin() && surroundingField.getPenguin().getOwner() == currentPlayer.getPlayerColor()) {
                                friendlyPenguinOnField = true;
                            }
                            surroundingFishCount += surroundingField.getFish();
                        }

                        /*if this is the highest number of fish surrounding a field as of now,
                         update the max fish count and the selected move (unless a friendly
                         penguin is on one of the fields. We don't want to place two penguins
                         immediately adjacent to each other.)*/
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
                    
                } 
                
                /*
                THIS IS FOR THE REGULAR GAME MOVES (MOVES 5-30)
                */
                else {
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
