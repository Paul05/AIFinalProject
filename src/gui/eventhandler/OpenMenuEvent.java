/**
 * 
 */
package gui.eventhandler;

import game.WumpusGame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author bafarnwo
 *
 */
public class OpenMenuEvent implements EventHandler<ActionEvent> 
{
	private WumpusGame game;
	/**
	 * 
	 */
	public OpenMenuEvent( WumpusGame game) 
	{
		this.game = game;
	}

	@Override
	public void handle(ActionEvent arg0) 
	{
		game.init( true );
	}

}
