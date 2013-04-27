import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import jpl.Atom;
import jpl.Query;
import jpl.Term;
import jpl.Variable;


/**
 * Transfer class that given a prolog file
 * calls the game logic to get moves for
 * a human and a monster in grid pair form.
 * Initialize with a file and call getGameMoves() 
 * to get the moves which retruns a GameMoves object
 * with two integer arrays.
 */
public class Prolog2JavaGameMovesTransfer {

	private File mazeFile; 
	private Variable Human;
	private Variable Monster;
	
	
	public Prolog2JavaGameMovesTransfer(File gameLogicFile) {
		this.mazeFile = gameLogicFile; 				
		this.Human = new Variable("Human");
		this.Monster = new Variable("Monster");
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
			
		   
			
			Query playGameBFS = new Query("playGameBFS", new Term[] {Human, Monster});
		    
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
