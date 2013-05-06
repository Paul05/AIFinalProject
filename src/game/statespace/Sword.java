package game.statespace;

public class Sword 
{
	private int swordX, swordY;

	public Sword(int swordX, int swordY ) 
	{
		this.swordX = swordX - 1;
		this.swordY = swordY - 1;
	}

	public int getSwordX() {
		return swordX;
	}

	public void setSwordX(int swordX) {
		this.swordX = swordX;
	}

	public int getSwordY() {
		return swordY;
	}

	public void setSwordY(int swordY) {
		this.swordY = swordY;
	}
	
	public boolean foundSword( int x, int y )
	{
		if( x == swordX && y == swordY)
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		return "Sword is at ( " + ( swordX + 1 ) + ", " + ( swordY + 1 ) + " )";
	}
}
