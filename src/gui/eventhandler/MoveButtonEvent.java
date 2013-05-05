package gui.eventhandler;

import game.WumpusGame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MoveButtonEvent implements EventHandler<ActionEvent>
{
	private WumpusGame game;
	
	public MoveButtonEvent( )
	{
		super();
	}
	
	public MoveButtonEvent( WumpusGame game )
	{
		this.game = game;
	}
	
	@Override
	public void handle(ActionEvent arg0) 
	{
		game.nextMove();
	}

}
