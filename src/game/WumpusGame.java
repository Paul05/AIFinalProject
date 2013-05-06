package game;

import game.agents.Agent;
import game.agents.HumanAgent;
import game.statespace.Goal;
import game.statespace.Sword;
import gui.WumpusMainGui;
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
	private boolean monsterTurn, done, gameStateChanged;
	private Goal gold;
	private Sword sword;
	private File prolog;
	private TextArea agentStatus, monsterStatus;
	private int agentStartX, agentStartY, monsterStartX, monsterStartY, goldStartX, goldStartY, exitX, exitY, swordStartX, swordStartY;
	private String goal;


	private final String goldText = "$$$ Gold $$$";
	private final String goldEmptyText = "$$$ Empty $$$";
	private final String swordText = "0==|----Sword----->";
	private final String swordEmptyText = "0==|----Gone------>";
	private final String monsterStink = "0~~ Stink Cloud ~~0";
	private final String goldGoal = "gold";
    private final String swordGoal = "sword";
	private final String exitGoal = "exit";
	
	public WumpusGame( )
	{
		super();
		monsterTurn = false;
		gameBoard = null;
		prolog = null;
	}
	
	public WumpusGame( TextArea[][] gameBoard, TextArea agentStatus, TextArea monsterStatus )
	{
		this.gameBoard = gameBoard;
		monsterTurn = false;
		prolog = null;
		this.agentStatus = agentStatus;
		this.monsterStatus = monsterStatus;
		agentStartX = 5;
		agentStartY = 1;
		monsterStartX = 1;
		monsterStartY = 1;
		goldStartX = 5;
		goldStartY = 5;
		exitX = 0;
		exitY = 0;
		swordStartX = 5;
		swordStartY = 4;
		
		gold = new Goal( goldStartX, goldStartY );
		sword = new Sword( swordStartX, swordStartY );
		
	}
	
	public boolean isDone() 
	{
		return done;
	}

	public void setDone( boolean done ) 
	{
		this.done = done;
	}
	
	public int getAgentStartX() 
	{
		return agentStartX;
	}

	public void setAgentStartX(int agentStartX) 
	{
		this.agentStartX = agentStartX;
	}

	public int getAgentStartY() 
	{
		return agentStartY;
	}

	public void setAgentStartY(int agentStartY) 
	{
		this.agentStartY = agentStartY;
	}

	public int getMonsterStartX() {
		return monsterStartX;
	}

	public void setMonsterStartX(int monsterStartX) 
	{
		this.monsterStartX = monsterStartX;
	}

	public int getMonsterStartY() {
		return monsterStartY;
	}

	public void setMonsterStartY(int monsterStartY) 
	{
		this.monsterStartY = monsterStartY;
	}

	public int getGoldStartX() {
		return goldStartX;
	}

	public void setGoldStartX(int goldStartX) 
	{
		this.goldStartX = goldStartX;
	}

	public int getGoldStartY() 
	{
		return goldStartY;
	}

	public void setGoldStartY(int goldStartY) 
	{
		this.goldStartY = goldStartY;
	}

	public int getSwordX() {
		return swordStartX;
	}

	public void setSwordX(int swordX) 
	{
		this.swordStartX = swordX;
	}
	
	public int getSwordY() {
		return swordStartY;
	}

	public void setSwordY(int swordY) 
	{
		this.swordStartY = swordY;
	}

	public void init( boolean openNewFile )
	{
		FileChooser fileChooser = new FileChooser();
		
		//Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Prolog files (*.pl)", "*.pl");
		fileChooser.getExtensionFilters().add(extFilter);
		
		if( openNewFile )
		{
			if( prolog != null )
				fileChooser.setInitialDirectory( prolog.getParentFile() );
			else
				fileChooser.setInitialDirectory( new File( System.getProperty("user.dir")));
			
			fileChooser.setTitle("Choose Maze Database");
		
			prolog = fileChooser.showOpenDialog( null );
		}
		
		if (prolog.isFile())
		{
			jpl = new Prolog2JavaGameMovesTransfer( prolog );
			
			jpl.initPositions( agentStartX, agentStartY, monsterStartX, monsterStartY, goldStartX, goldStartY );
			
			jpl.loadFile();
			
			moves = jpl.getGameMoves();
			agent = new HumanAgent( moves.getHumanMoves(), gameBoard, "Dr.Bansal", gold, sword );
			monster = new Agent( moves.getMonsterMoves(), gameBoard, "Wumpus");
			
			done = false;
			
			Log.clear("Game Output");
			Log.out("=-=-=-=-==-=-=-==-=-=-=-=-=-=-=-");
			
			
			for( int i = 0; i < gameBoard.length; i ++ )
				for( int ii = 0; ii <gameBoard[i].length; ii++ )
					gameBoard[i][ii].setText("");
			
			gold = new Goal( goldStartX, goldStartY );
			sword = new Sword( swordStartX, swordStartY );
			
			updateSquare( gameBoard[gold.getGoldX()][gold.getGoldY()], gold.getGoldX(), gold.getGoldY( ) );
			updateSquare( gameBoard[sword.getSwordX()][sword.getSwordY()], sword.getSwordX( ), sword.getSwordY( ) ) ;
			
			agent.move();
			updateSquare(gameBoard[agent.getX()][agent.getY()], agent.getX(), agent.getY() );
			
			monster.move();
			updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
			
			updateSquare(gameBoard[exitX][exitY], exitX, exitY );
			clearStinkCloud( false );
			
			agent.printMoves();
			monster.printMoves();
			
			monsterTurn = false;
		}
		
	}
	
	public void nextMove()
	{
		int prevX, prevY;
		if( done != true )
		{

			if( monsterTurn != true )
			{
				agentStatus.clear();
				agentStatus.setText( percieveSurroundings( agent.getName() + " is percieving surrondings\nContemplating move..." ) );
				
				prevX = agent.getX();
				prevY = agent.getY();
				agent.move();
				
				agent.foundGold();
				agent.foundSword();
				
				updateSquare(gameBoard[prevX][prevY], prevX, prevY );
				updateSquare(gameBoard[agent.getX()][agent.getY()], agent.getX(), agent.getY() );
				agentStatus.appendText( percieveSurroundings( "\n" + agent.getName() + " is looking around before next turn..." ) );
				
				if( !monster.isAlive() )
					monsterTurn = false;
				else
					monsterTurn = true;
				
				if( agent.isHere( exitX, exitY ) && agent.foundGold() )
				{
					Log.out( agent.getName() + " made it safely out ");
					done = true;
				}
			}
			else
			{
				if( monster.isAlive() )
				{
					monsterStatus.clear();
					clearStinkCloud( true );
					monsterStatus.setText( percieveSurroundings( monster.getName() + " percieving surrondings\nContemplating move..." ) );
					
					prevX = monster.getX();
					prevY = monster.getY();
					monster.move();
					
					updateSquare(gameBoard[prevX][prevY], prevX, prevY );
					updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
					
					monsterStatus.appendText( percieveSurroundings("\n" +  monster.getName() + " is looking around before next turn.." ) );
					clearStinkCloud( false );
					
					monsterFight();
	
				}
				monsterTurn = false;
			}
		}
	}
	
	private void clearStinkCloud( boolean clearStink )
	{
		TextArea tempRoom;
		int tempX, tempY;
		
		tempX = monster.getX() - 1;
		if( tempX >= 0 )
		{
			tempRoom = gameBoard[tempX][monster.getY()];
			setStink( clearStink, tempRoom );
		}
		
		tempX = monster.getX() + 1;
		if( tempX < WumpusMainGui.BOARD_ROW_MAX )
		{
			tempRoom = gameBoard[tempX][monster.getY()];
			setStink( clearStink, tempRoom );
		}
		
		tempY = monster.getY() - 1;
		if( tempY >= 0 )
		{
			tempRoom = gameBoard[monster.getX()][tempY];
			setStink( clearStink, tempRoom );
		}
		
		tempY = monster.getY() + 1;
		if( tempY < WumpusMainGui.BOARD_COL_MAX )
		{
			tempRoom = gameBoard[monster.getX()][tempY];
			setStink( clearStink, tempRoom );
		}
	}
	
	private void setStink( boolean clearStink, TextArea tempRoom )
	{
		if( clearStink )
			tempRoom.setText( tempRoom.getText().replace( monsterStink, "") );
		else
			tempRoom.setText( tempRoom.getText() + monsterStink );
	}
	
	private void monsterFight()
	{
		
		if( agent.isHere( monster.getX( ), monster.getY( ) ) )
		{
			
			if( agent.isArmed() )
			{
				Log.out( agent.getName() + " has slain the " + monster.getName() );
				monster.setName("[R.I.P]" + monster.getName() );
				updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
				monster.setAlive( false );
				clearStinkCloud( true );
					
				moves = jpl.monsterDeadGetPathToGoal(agent.getX() + 1, agent.getY() + 1, exitX + 1, exitY + 1 );
				
				agent.setMoves(moves.getHumanMoves());
				agent.setTurn( 2 );//We are already at the starting point which is zero
				agent.printMoves();
				
			}
			else
			{
				Log.out( agent.getName() + " has been eaten by " + monster.getName() );
				agent.setName( "[R.I.P]" + agent.getName() );
				updateSquare(gameBoard[monster.getX()][monster.getY()], monster.getX(), monster.getY() );
				agent.setAlive( false );
				done = true;
			}
		}
	}
	
	private void updateSquare( TextArea square, int x, int y )
	{
		String value = "";
		
		if( square.getText().contains( monsterStink ) )
			value = monsterStink;
		
		square.clear();
		
		if( gold.foundGold(x, y) )
		{
			if( !agent.hasGold())
				value += goldText + "\n";
			else
				value += goldEmptyText + "\n";
		}
		else
			value += "\n";
		
		if( sword.foundSword(x, y))
		{
			if( !agent.isArmed() )
				value += swordText + "\n";
			else
				value += swordEmptyText + "\n";
		}
		
		if( agent.isHere(x, y))
			value += agent.getName() + "\n";
		
		if( monster.isHere(x, y))
			value += monster.getName() + "\n";
		
		if( exitX == x && exitY == y)
			value += "|------Exit------|\n";
		
		square.setText(value);			
	}
	
	private String percieveSurroundings( String banner )
	{
		String text = banner + "\n";
		int x, y;
		
		text += "=-=-=-=-=-=-=-=-=-=-=-=-\n";
		
		if( monsterTurn != true)
		{
			x = agent.getX();
			y = agent.getY();
		}
		else
		{
			x = monster.getX();
			y = monster.getY();
		}
		
		text += checkForWall( x, y );
		text += checkForGold( x, y );
		text += checkForSword( x, y );
		
		if( monsterTurn != true )
		{
			text += checkForOtherAgent( agent, monster, "RUN" );
			text += checkForStick( x, y );
		}
		else
		{
			text += checkForOtherAgent( monster, agent, "EAT" );
		}

		return text;
	}

	private String checkForStick( int x, int y ) 
	{
		String text = "";
		int tempX, tempY;
		
		tempX = x - 1;
		if( tempX >= 0 )
		{
			if( foundStink( gameBoard[tempX][y].getText() ) )
				text += "See: StinkCloud found to the west\n";
		}
		
		tempX = x + 1;
		if( tempX < WumpusMainGui.BOARD_ROW_MAX )
		{
			if( foundStink( gameBoard[tempX][y].getText() ) )
				text += "See: StinkCloud found to the east\n";
		}
		
		tempY = y - 1;
		if( tempY >= 0 )
		{
			if( foundStink( gameBoard[x][tempY].getText() ) )
				text += "See: StinkCloud found to the north\n";
		}
		
		tempY = y + 1;
		if( tempY < WumpusMainGui.BOARD_COL_MAX )
		{
			if( foundStink( gameBoard[x][tempY].getText() ) )
				text += "See: StinkCloud found to the south\n";
		}
		
		return text;
	}
	
	private boolean foundStink( String room )
	{
		return ( room.contains( monsterStink) );
	}

	private String checkForSword( int x, int y ) 
	{
		String text = "";
		
		//check for gold current x & y
		if( sword.foundSword(x, y) )
			text = "Touch: The sword is here!\n";
		
		//check for gold +- x
		if( (x - 1) >= 0 )
		{
			if( sword.foundSword( (x -1), y) )
				text = "See: The gleem of silver from AIcaliber is west!\n";
		}
		
		if( (x + 1) <= WumpusMainGui.BOARD_ROW_MAX )
		{
			if( sword.foundSword( (x + 1), y) )
				text = "See: The gleem of silver from AIcaliber is east!\n";
		}
		
		if( (y - 1) >= 0 )
		{
			if( sword.foundSword( x, ( y - 1 )) )
				text = "See: The gleem of silver from AIcaliber is north!\n";
		}
		
		if( (y + 1) <= WumpusMainGui.BOARD_COL_MAX )
		{
			if( sword.foundSword( x, ( y + 1) ) )
				text = "See: The gleem of silver from AIcaliber is south!\n";
		}
		
		return text;
	}

	private String checkForOtherAgent( Agent a, Agent b, String action ) 
	{
		String text = "";
		int x = a.getX();
		int y = a.getY();
		
		if( x - 1 >= 0 )
		{
			if( b.isHere( (x - 1 ), y) )
				text = "See: A movement to the west time to " + action + " \n";
		}
		
		if( x + 1 <= WumpusMainGui.BOARD_ROW_MAX )
		{
			if( b.isHere( ( x + 1 ), y) )
				text = "See: A movement to the east time to " + action + " \n";
		}
		
		if( y - 1 >= 0 )
		{
			if( b.isHere( x, ( y - 1 )) )
				text = "See: A movement to the north time to " + action + " \n";
		}
		
		if( y + 1 <= WumpusMainGui.BOARD_COL_MAX )
		{
			if( b.isHere( x, ( y + 1) ) )
				text = "See: A movement to the south time to " + action + " \n";
		}
		
		if( b.isHere(x, y) )
		{
			if( monsterTurn == true )
			{
				text = "See/Eat/Feel:  Time to eat!";
			}
			//need to add arrow logic here
		}
		
		return text;
		
	}

	private String checkForWall( int x, int y ) 
	{
		String text = "";
		
		if( x == 0 )
			text += "Touch: wall to the west\n";
		else if( x == ( WumpusMainGui.BOARD_ROW_MAX - 1) )
			text += "There is a wall to the east\n";
		
		if( y == 0 )
			text += "Touch: wall to the north\n";
		else if( y == ( WumpusMainGui.BOARD_COL_MAX - 1) )
			text += "There is a wall to the south\n";
		
		return text;
	}
	
	private String checkForGold( int x, int y ) 
	{
		String text = "";
		
		//check for gold current x & y
		if( gold.foundGold(x, y) )
			text = "Touch: The gold is here!\n";
		
		//check for gold +- x
		if( (x - 1) >= 0 )
		{
			if( gold.foundGold( (x -1), y) )
				text = "See: A sparkle of gold is seen to the west!\n";
		}
		
		if( (x + 1) <= WumpusMainGui.BOARD_ROW_MAX )
		{
			if( gold.foundGold( (x + 1), y) )
				text = "See: A sparkle of gold is seen to the east!\n";
		}
		
		if( (y - 1) >= 0 )
		{
			if( gold.foundGold( x, ( y - 1 )) )
				text = "See: A sparkle of gold is seen to the north!\n";
		}
		
		if( (y + 1) <= WumpusMainGui.BOARD_COL_MAX )
		{
			if( gold.foundGold( x, ( y + 1) ) )
				text = "See: A sparkle of gold is seen to the south!\n";
		}
		
		return text;
	}
}
