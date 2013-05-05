
/**
 * Basic class to hold two integer arrays for the grid moves of 
 * a human and monster player. 
 */
public class GameMoves {
	
	private int[] humanMoves;
	private int[] monsterMoves;
	
	public GameMoves(int[] movesOfHuman, int[] movesOfMonster) {
		this.humanMoves = movesOfHuman;
		this.monsterMoves = movesOfMonster;
	}

	
	/**
	 * @return the humanMoves
	 */
	public int[] getHumanMoves() {
		return humanMoves;
	}

	
	/**
	 * @param humanMoves the humanMoves to set
	 */
	public void setHumanMoves(int[] humanMoves) {
		this.humanMoves = humanMoves;
	}

	
	/**
	 * @return the monsterMoves
	 */
	public int[] getMonsterMoves() {
		return monsterMoves;
	}

	
	/**
	 * @param monsterMoves the monsterMoves to set
	 */
	public void setMonsterMoves(int[] monsterMoves) {
		this.monsterMoves = monsterMoves;
	}
	
} //end class GameMoves
