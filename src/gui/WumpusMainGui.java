package gui;

import java.io.File;

import game.WumpusGame;
import gui.data.Log;
import gui.eventhandler.AutoButtonEvent;
import gui.eventhandler.CloseButtonEvent;
import gui.eventhandler.MoveButtonEvent;
import gui.eventhandler.OpenMenuEvent;
import gui.eventhandler.RestartButtonEvent;
import gui.eventhandler.UpdateButtonEvent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class WumpusMainGui extends Application
{
	private AnchorPane mainAnchorPane;
	private BorderPane mainBorderPane;
	private AnchorPane leftPane;
	private GridPane boardLayout;
	private VBox topVBox, leftVBox;
	
	private TextArea boardText[][];
	private MenuBar mainMenu;
	private ToolBar toolBar;
	private Button restart, move, auto, update;
	private MenuItem open, close;
	private TextField xbox, ybox;
	private ComboBox<String> postionbox;
	private ObservableList<String> options;
	
	private TextArea playerStatus, monsterStatus, gameStatus;
	
	public static final int BOARD_ROW_MAX  = 5;
	public static final int BOARD_COL_MAX  = 5;
	
	private WumpusGame game;
	
	private void initMenu( )
	{
		options = FXCollections.observableArrayList( UpdateButtonEvent.AGENT, UpdateButtonEvent.MONSTER, UpdateButtonEvent.GOLD,
				UpdateButtonEvent.SWORD );
		
		//Setup MainMenu
		Menu file = new Menu("File");
		open = new MenuItem("Open");
		close = new MenuItem("Close");
		topVBox = new VBox( );
		mainMenu = new MenuBar( );
		
		//Setup ToolBar
		Label xlbl = new Label("X-Value");
		xbox = new TextField();
		Label ylbl = new Label("Y-Value");
		ybox = new TextField();
		toolBar = new ToolBar( );
		restart = new Button( "Restart" );
		move = new Button( "Move");
		auto = new Button( "Auto" );
		update = new Button( "Update" );
		Separator s = new Separator( );
		postionbox = new ComboBox<String>( options );
		
		//Add Items to MainMenu
		file.getItems().addAll( open, close );
		mainMenu.getMenus().add( file );
		
		//AddItems to ToolBar
		s.setPrefHeight( 20.0 );
		s.setPrefWidth( 7.0 );
		xbox.setPrefWidth( 50.0 );
		ybox.setPrefWidth( 50.0 );
		
		toolBar.getItems().addAll( restart, move, auto, s, xlbl, xbox, ylbl, ybox, postionbox, update );
		
		//add to VBox
		topVBox.getChildren().addAll( mainMenu, toolBar );
	}
	
	private void initStatus( )
	{

		leftPane = new AnchorPane();
		leftVBox = new VBox( );
		playerStatus = new TextArea( );
		monsterStatus = new TextArea( );
		gameStatus = new TextArea( );
		
		AnchorPane.setBottomAnchor( leftVBox, 0.0 );
		AnchorPane.setLeftAnchor( leftVBox, 0.0 );
		AnchorPane.setRightAnchor( leftVBox, 0.0 );
		AnchorPane.setTopAnchor( leftVBox, 0.0 );
		
		leftPane.setPrefWidth(300.0);
		leftPane.setPrefHeight(780.0);
		leftVBox.setPrefHeight(780.0);
		
		gameStatus.setPrefHeight(408.0);
		Log.init( gameStatus );
		
		playerStatus.setText("Adventure Knowledge\n");
		playerStatus.setEditable( false );
		
		monsterStatus.setText("Monster Knowledge\n");
		playerStatus.setEditable( false );
		
		playerStatus.setEditable( false );
		
		leftVBox.getChildren().addAll( playerStatus, monsterStatus, gameStatus );
		leftPane.getChildren().addAll( leftVBox );
	}
	
	private void initBoard( )
	{
		boardLayout = new GridPane();
		boardText = new TextArea[ BOARD_ROW_MAX ][ BOARD_COL_MAX ];
		
		boardLayout.setHgap(2.0);
		boardLayout.setVgap(2.0);
		
		for( int c = 0; c < BOARD_ROW_MAX; c++ )
		{
			for( int r = 0; r < BOARD_COL_MAX; r++ )
			{
				boardText[r][c] = new TextArea();
				boardLayout.add(boardText[r][c], r, c);
				boardText[r][c].setText( "( " + r + ", " + c + ")");
				boardText[r][c].setEditable( false );
			}
		}
		
		
		
	}
	
	private Scene initGui( )
	{
		mainAnchorPane = new AnchorPane( );
		mainBorderPane = new BorderPane( );
		
		AnchorPane.setBottomAnchor(mainBorderPane, 0.0);
		AnchorPane.setLeftAnchor(mainBorderPane, 0.0);
		AnchorPane.setRightAnchor(mainBorderPane, 0.0);
		AnchorPane.setTopAnchor(mainBorderPane, 0.0);
		
		mainAnchorPane.setPrefHeight( 800.0 );
		mainAnchorPane.setPrefWidth( 1000.0 );
		
		mainBorderPane.setPrefHeight( 800.0 );
		mainBorderPane.setPrefWidth( 1000.0 );
		
		initMenu( );
		initBoard( );
		initStatus( );
		
		mainBorderPane.setTop( topVBox );
		mainBorderPane.setCenter( boardLayout );
		mainBorderPane.setLeft( leftPane );
		mainAnchorPane.getChildren().add( mainBorderPane );
		
		return new Scene( mainAnchorPane );
	}
	
	private void setupEvents()
	{
		//Setup Button events
		move.setOnAction( new  MoveButtonEvent(game) );
		open.setOnAction( new OpenMenuEvent( game ) );
		close.setOnAction( new CloseButtonEvent( ) );
		auto.setOnAction( new AutoButtonEvent( game ) );
		restart.setOnAction( new RestartButtonEvent( game ) );
		update.setOnAction( new UpdateButtonEvent( game, xbox, ybox, postionbox ) );
	}
	
	@Override
	public void start( Stage stage ) throws Exception 
	{
		String source = new File("The Legend of Zelda Music.mp3").toURI().toString();
		Media media = new Media(source);
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		Scene scene = initGui();
		stage.setTitle("Wumpus World");
		
		game = new WumpusGame( boardText, playerStatus, monsterStatus );
		game.init( true );
		
		setupEvents();
		
		mediaPlayer.play();
		
		stage.setScene( scene );
		stage.show();
		
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
