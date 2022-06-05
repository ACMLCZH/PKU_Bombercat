package BasePlayer;

import BaseObject.Coordinate;
import main.Game;

public class HumanPlayer extends BasePlayer
{
	public static final int INIT_HP = 10000;
	public static final int ATK = 1000;
	private boolean placing = false;

	public HumanPlayer(Game game, String name, int HP, Coordinate spawn, Indirect dir)
	{
		super(game, name, HP, spawn, dir, ATK);
	}

	@Override
	public boolean placeBomb()
	{
		if (!placing) {this.placing = true; return super.placeBomb();}
		else return false;
	}
	public void release() {placing = false;}
	// public boolean isPlacing() {return this.placing;}
}
