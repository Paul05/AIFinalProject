package game.agents;

import game.statespace.Goal;
import game.statespace.Sword;
import gui.data.Log;
import javafx.scene.control.TextArea;

public class HumanAgent extends Agent
{
	private boolean armed;
	private boolean foundGold;
	private Goal gold;
	private Sword sword;

	public HumanAgent(int[] moves, TextArea[][] gameBoard, String name, Goal gold, Sword sword ) 
	{
		super(moves, gameBoard, name );
		armed = false;
		foundGold = false;
		this.gold = gold;
		this.sword = sword;
	}
	
	public boolean hasGold() 
	{
		return foundGold;
	}
	
	public boolean foundGold() 
	{
		if( gold.foundGold( getX(), getY())  && !foundGold )
		{
			Log.out("$$ Found the Gold $$");
			foundGold = true;
		}
		
		return foundGold;
	}

	public boolean foundSword() 
	{
		if( sword.foundSword( getX(), getY() ) )
			setArmed( true );
		
		return isArmed();
	}
	
	public boolean isArmed()
	{
		return armed;
	}

	public void setArmed(boolean armed) 
	{
		this.armed = armed;
	}
		
}
