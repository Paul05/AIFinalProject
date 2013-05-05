package game.agents;

import gui.data.Log;
import javafx.scene.control.TextArea;

public class Agent 
{
	private String name;
	private boolean alive;
	private int x, y, turn;
	private int[] moves;
	private TextArea[][] gameBoard;
	
	public Agent( int[] moves, TextArea[][] gameBoard, String name )
	{
		alive = true;
		x = y = turn = 0;
		this.moves =  moves;
		this.gameBoard = gameBoard;
		this.name = name;
	}
	
	public boolean isAlive() 
	{
		return alive;
	}
	public void setAlive(boolean alive) 
	{
		this.alive = alive;
	}
	public int getTurn( )
	{
		return turn;
	}
	
	public void setTurn( int turn )
	{
		this.turn = turn;
	}
	
	public int getX() 
	{
		return x;
	}
	
	public void setX(int x) 
	{
		this.x = x;
	}
	
	public int getY() 
	{
		return y;
	}
	
	public void setY(int y) 
	{
		this.y = y;
	}
	
	public int[] getMoves() 
	{
		return moves;
	}
	
	public void setMoves(int[] moves) 
	{
		this.moves = moves;
	}	
	
	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public boolean isHere( int x, int y )
	{
		return ( this.x == x && this.y == y );
	}

	public void move( )
	{
		if( (turn + 1) <= moves.length - 1)
		{
			gameBoard[x][y].setText("");
			
			x = moves[turn] - 1;
			turn += 1;
			y = moves[turn] - 1;
			turn += 1;
			
			Log.out(name + ": is moving to ( " + ( x + 1 ) + ", " + (y + 1) + " )");
		}
	}

	public void printMoves() 
	{
		System.out.println(name + " moves");
		
		for( int i = 0; i < moves.length - 1; i++ )
		{
			int tempx = moves[i];
			i++;
			int tempy = moves[i];
			
			System.out.println("\t( " + tempx + ", " + tempy + ")");
		}
			
	}
}
