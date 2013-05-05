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
		gold = new Goal( 5, 1 );
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
		
		agent.move();
		monster.move();
		
		agent.printMoves();
		monster.printMoves();
		
		gameBoard[gold.getGoldX() - 1][gold.getGoldY() - 1].setText( "$$$ Gold $$$" );
		
	}
	
	public void nextMove()
	{
		if( done != true )
		{
			if( monsterTurn == false )
			{
				agent.move();
				monsterTurn = true;
				
				if( agent.isRich( ) )
				{
					done = true;
				}
			}
			else
			{
				monster.move();
				monsterTurn = false;
				
				if( (agent.getX() == monster.getX( ) ) && (agent.getY( ) == monster.getY( ) ) )
				{
					Log.out( agent.getName() + " has been eaten by " + monster.getName() );
					agent.setAlive( false );
					done = true;
				}
			}
		}

	}	
}
