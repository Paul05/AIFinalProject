package gui.eventhandler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class CloseButtonEvent implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) 
	{
		System.exit( 0 );
	}

}
