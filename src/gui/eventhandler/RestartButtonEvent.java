package gui.eventhandler;

import game.WumpusGame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class RestartButtonEvent implements EventHandler<ActionEvent> {

	private WumpusGame game;
	public RestartButtonEvent(WumpusGame game) 
	{
		this.game = game;
	}

	@Override
	public void handle(ActionEvent event) 
	{
		game.init();
	}

}
