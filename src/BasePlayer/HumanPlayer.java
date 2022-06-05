package BasePlayer;

import BaseObject.Coordinate;

public class HumanPlayer extends BasePlayer
{
	public static final int INIT_HP = 10000;
	public HumanPlayer(int HP, Coordinate spawn, Indirect dir, String name)
	{
		super(HP, spawn, dir, name);
	}    
}
