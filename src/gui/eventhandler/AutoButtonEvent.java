package gui.eventhandler;

import game.WumpusGame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class AutoButtonEvent implements EventHandler<ActionEvent> {

	private WumpusGame game;
	
	public AutoButtonEvent(WumpusGame game) 
	{
		this.game = game;
	}

	@Override
	public void handle(ActionEvent event) 
	{
		do
		{
			game.nextMove();
		}while( game.isDone() != true );

	}

}
