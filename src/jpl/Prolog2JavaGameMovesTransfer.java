package jpl;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.lang.Integer;
import jpl.Atom;
import jpl.Query;
import jpl.Term;
import jpl.Variable;


/**
 * Transfer class that given a prolog file
 * calls the game logic to get moves for
 * a human and a monster in grid pair form.
 * Initialize constructor with a file, 
 * then call initPositions(...) to set up the game board
 * and call getGameMoves() to get the moves which retruns a GameMoves object
 * with two integer arrays.
 */
public class Prolog2JavaGameMovesTransfer {

	private File mazeFile; 
	private Variable Human;
	private Variable Monster;
	private jpl.Integer explorerStartX;
	private jpl.Integer explorerStartY;
	private jpl.Integer monsterStartX;
	private jpl.Integer monsterStartY;
	private jpl.Integer goldRoomX;
	private jpl.Integer goldRoomY;
	
	
	public Prolog2JavaGameMovesTransfer(File gameLogicFile) {
		this.mazeFile = gameLogicFile; 				
		this.Human = new Variable("Human");
		this.Monster = new Variable("Monster");
	}
	
	/**
	 * Call this method before calling getGameMoves(), to set up the position of the explorer, monster and gold
	 * @param expStartX X-coordinate of the explorers start room
	 * @param expStartY Y-coordinate of the explorers start room
	 * @param monStartX X-coordinate of the monsters start room
	 * @param monStartY Y-coordinate of the monsters start room
	 * @param goldX X-coordinate of the treasure room
	 * @param goldY Y-coordinate of the treasure room
	 */
	public void initPositions(int expStartX, int expStartY, int monStartX, int monStartY, int goldX, int goldY) {
		this.explorerStartX = new jpl.Integer(expStartX);
		this.explorerStartY = new jpl.Integer(expStartY);
		this.monsterStartX = new jpl.Integer(monStartX);
		this.monsterStartY = new jpl.Integer(monStartY);
		this.goldRoomX = new jpl.Integer(goldX);
		this.goldRoomY = new jpl.Integer(goldY);
	}
	
	public GameMoves getGameMoves()
	{
		int[] humanMovesOut = null;
		int[] monsterMovesOut = null;	
		
		try{
			
			System.out.println("\nFile Path = " +mazeFile.getCanonicalPath() +" \n"); 
			
			Query consultQuery = new Query("consult", new Term[] {
					new Atom(mazeFile.getCanonicalPath()) 		
			});
			
			System.out.println( "consult " + (consultQuery.hasSolution() ? "succeeded\n" : "failed"));		 	
			
			consultQuery.close();
			
		   
			
			Query playGameBFS = new Query("playGame", new Term[] {explorerStartX, explorerStartY,
					monsterStartX, monsterStartY, goldRoomX, goldRoomY, Human, Monster});
		    
			@SuppressWarnings("rawtypes")
			Hashtable gameDisplay = playGameBFS.oneSolution(); //can change here to get more solutions etc. 
					
			if (gameDisplay != null) {
			
				System.out.println("\n(Raw Form) Human= " +gameDisplay.get("Human"));
				System.out.println("(Raw Form) Monster= " +gameDisplay.get("Monster") +"\n\n");
				
				String[] monster = gameDisplay.get("Monster").toString().split("\\s+");
				String[] human = gameDisplay.get("Human").toString().split("\\s+");
			
				humanMovesOut = new int[human.length];
				monsterMovesOut = new int[monster.length];
																
				for (int i=0; i < monster.length-1; i++)
				{
					monsterMovesOut[i] = Integer.parseInt(monster[i].replaceAll("[\\D]"," ").trim());
					humanMovesOut[i] = Integer.parseInt(human[i].replaceAll("[\\D]", " ").trim());											
				}
				
			}
			else {
				System.out.println("\n***Query Returned Empty***\n");
			}
			
			playGameBFS.close();	   
		
		} catch (NumberFormatException n) {
			System.out.println("\n***Error Accessing Number***\n");
			n.printStackTrace();
		} catch (IOException e)	{
			System.out.println("\n***Error Reading File!***\n");
			e.printStackTrace();
		}//end try/catch
		
		return new GameMoves(humanMovesOut, monsterMovesOut);
	
	} //end getGameMoves method

} //end class Prolog2JavaGameMovesTransfer
