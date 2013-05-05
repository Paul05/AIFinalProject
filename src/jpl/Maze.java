package jpl;

import java.io.File;

/**
 * Main class to that runs and outputs a sample query on a prolog file 
 * and returns results of human and monster moves.  
 */
public class Maze 
{	
	public static void main(String[] args) {
		
	File mazeFile = new File("src"+File.separator+"maze.pl");
	
	if (mazeFile.isFile())	{
		Prolog2JavaGameMovesTransfer transferMoves = new Prolog2JavaGameMovesTransfer(mazeFile);
		
		
		transferMoves.loadFile();
		//this is where the inputs for the game start will go
		transferMoves.initPositions(1,5,5,1,2,3);
		
		GameMoves testMoves = transferMoves.getGameMoves();
		
		System.out.print("Human moves=   ");
		for (int h=0; h < testMoves.getHumanMoves().length; h++)
		{
			System.out.print(testMoves.getHumanMoves()[h] +", ");
		}
		System.out.println(".\n");
		
		System.out.print("Monster moves= ");
		for (int h=0; h < testMoves.getMonsterMoves().length; h++)
		{
			System.out.print(testMoves.getMonsterMoves()[h] +", ");
		}
		System.out.println(".\n");
		
		System.out.println("The End");
	}
	
	}
}
