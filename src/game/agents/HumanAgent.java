package game.agents;

import game.statespace.Goal;
import gui.data.Log;
import javafx.scene.control.TextArea;

public class HumanAgent extends Agent
{
	private boolean armed;
	private Goal gold;

	public HumanAgent(int[] moves, TextArea[][] gameBoard, String name, Goal gold ) 
	{
		super(moves, gameBoard, name );
		armed = false;
		this.gold = gold;
	}
	
	public boolean isRich() 
	{
		if( gold.foundGold( getX(), getY()) )
		{
			Log.out("$$ Found the Gold $$");
			return true;
		}
		else
		{
			return false;
		}
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
