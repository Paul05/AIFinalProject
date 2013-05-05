package game;

import game.agents.Agent;
import game.agents.HumanAgent;
import game.statespace.Goal;
import gui.data.Log;

import java.io.File;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import jpl.GameMoves;
import jpl.Prolog2JavaGameMovesTransfer;

public class WumpusGame 
{
	private Prolog2JavaGameMovesTransfer jpl;
	private GameMoves moves;
	private TextArea[][] gameBoard;
	private Agent monster;
	private HumanAgent agent;
	private boolean monsterTurn;
	private boolean done;
	private Goal gold;
	private File prolog;
	
	private final String goldText = "$$$ Gold $$$";
	
	public WumpusGame( )
	{
		super();
		monsterTurn = false;
		gameBoard = null;
		prolog = null;
	}
	
	public WumpusGame( TextArea[][] gameBoard )
	{
		this.gameBoard = gameBoard;
		monsterTurn = false;
		gold = new Goal( 2, 3 );
		prolog = null;
		
	}
	
	public boolean isDone() 
	{
		return done;
	}

	public void setDone( boolean done ) 
	{
		this.done = done;
	}

	public void init()
	{
		FileChooser fileChooser = new FileChooser();
		
		//Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Prolog files (*.pl)", "*.pl");
		fileChooser.getExtensionFilters().add(extFilter);
		
		if( prolog != null )
			fileChooser.setInitialDirectory( prolog.getParentFile() );
		else
			fileChooser.setInitialDirectory( new File( System.getProperty("user.dir")));
		
		fileChooser.setTitle("Choose Maze Database");
		
		prolog = fileChooser.showOpenDialog( null );
		
		if (prolog.isFile())
		{
			jpl = new Prolog2JavaGameMovesTransfer( prolog );
			
			jpl.initPositions(1,5,5,1,2,3);
			
			moves = jpl.getGameMoves();
			agent = new HumanAgent( moves.getHumanMoves(), gameBoard, "Dr.Bansal", gold );
			monster = new Agent( moves.getMonsterMoves(), gameBoard, "Wumpus");
			
			done = false;
			
		}
		
		Log.clear("Game Output");
		Log.out("=-=-=-=-==-=-=-==-=-=-=-=-=-=-=-");
		
		
		for( int i = 0; i < gameBoard.length; i ++ )
			for( int ii = 0; ii <gameBoard[i].length; ii++ )
				gameBoard[i][ii].setText("");
		
		updateSquare( gameBoard[gold.getGoldX()][gold.getGoldY()], gold.getGoldX(), gold.getGoldY());
		
		agent.move();
		updateSquare(gameBoard[agent.getX()][agent.getY()], agent.getX(), agent.getY() );
		
		monster.move();
		updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
		
		agent.printMoves();
		monster.printMoves();
		
	}
	
	public void nextMove()
	{
		if( done != true )
		{
			if( monsterTurn == false )
			{
				agent.move();
				monsterTurn = true;
				
				updateSquare(gameBoard[agent.getX()][agent.getY()], agent.getX(), agent.getY() );
				
				if( agent.isRich( ) )
				{
					done = true;
				}
			}
			else
			{
				monster.move();
				monsterTurn = false;
				
				updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
				
				if( (agent.getX() == monster.getX( ) ) && (agent.getY( ) == monster.getY( ) ) )
				{
					Log.out( agent.getName() + " has been eaten by " + monster.getName() );
					agent.setAlive( false );
					done = true;
				}
			}
		}
	}
	
	private void updateSquare( TextArea square, int x, int y )
	{
		String value = "";
		
		square.clear();
		
		if( gold.foundGold(x, y) )
			value += goldText + "\n";
		else
			value += "\n";
		
		if( agent.isHere(x, y))
			value += agent.getName() + "\n";
		
		if( monster.isHere(x, y))
			value += monster.getName() + "\n";
		
		square.setText(value);			
	}
}
