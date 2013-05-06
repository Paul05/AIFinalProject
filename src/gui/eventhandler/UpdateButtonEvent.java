package gui.eventhandler;

import game.WumpusGame;
import gui.data.Log;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class UpdateButtonEvent implements EventHandler<ActionEvent> {

	public final static String AGENT = "Adventurer";
	public final static String MONSTER = "Monster";
	public final static String GOLD = "Gold";
	public final static String SWORD = "Sword";
	public final static String LAKE_1 = "Lake 1";
	public final static String LAKE_2 = "Lake 2";
	private WumpusGame game;
	private ComboBox<String> selection;
	private TextField xBox;
	private TextField yBox;
	
	public UpdateButtonEvent( WumpusGame game, TextField xbox, TextField ybox, ComboBox<String> selection )
	{
		this.game = game;
		this.xBox = xbox;
		this.yBox = ybox;
		this.selection = selection;
	}


	@Override
	public void handle(ActionEvent event) 
	{
		int x = -1;
		int y = -1;
		
		boolean valid = true;
		
		if( xBox.getText().length() == 1 )
			x = Integer.parseInt( xBox.getText() );
		else
			valid = false;
		
		if( yBox.getText().length() == 1 )
			y = Integer.parseInt( yBox.getText() );
		else
			valid = false;
		
		if( valid )
		{
			if( selection.getValue().equals(AGENT))
			{
				game.setAgentStartX(x);
				game.setAgentStartY(y);
				Log.out("Agent Starting Postion changed to ( " + x + ", " + y + " ) ");
			}
			else if( selection.getValue().equals(MONSTER))
			{
				game.setMonsterStartX(x);
				game.setMonsterStartY(y);
				Log.out("Monster Starting Postion changed to ( " + x + ", " + y + " ) ");
			}
			else if( selection.getValue().equals(GOLD))
			{
				game.setGoldStartX(x);
				game.setGoldStartY(y);
				Log.out("Gold Starting Postion changed to ( " + x + ", " + y + " ) ");
			}
			else if( selection.getValue().equals(SWORD))
			{
				game.setSwordX(x);
				game.setSwordY(y);
				Log.out("Sword Starting Postion changed to ( " + x + ", " + y + " ) ");
			}
		}
		else
		{
			Log.out("ERROR:  Please only input a single integer between 0 and 5");
		}
		
	}

}
