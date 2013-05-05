package game.statespace;

public class Goal 
{
	private int goldX, goldY;

	public Goal(int goldX, int goldY ) 
	{
		this.goldX = goldX - 1;
		this.goldY = goldY - 1;
	}

	public int getGoldX() {
		return goldX;
	}

	public void setGoldX(int goldX) {
		this.goldX = goldX;
	}

	public int getGoldY() {
		return goldY;
	}

	public void setGoldY(int goldY) {
		this.goldY = goldY;
	}
	
	public boolean foundGold( int x, int y )
	{
		if( x == goldX && y == goldY)
			return true;
		else
			return false;
	}
	
}
