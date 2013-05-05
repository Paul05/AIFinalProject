package gui.data;

import javafx.scene.control.TextArea;

public class Log 
{
	private static TextArea gameStatus;
	
	public Log( )
	{
		super( );
	}
	
	public static void out( String text )
	{
		gameStatus.appendText( text + "\n" );
	}
	
	public static void clear( String text )
	{
		gameStatus.clear();
		gameStatus.setText( text + "\n");
	}

	public static void init(TextArea gameStatus) 
	{
		Log.gameStatus = gameStatus;
		
	}

}
