package BasePlayer;

import BaseObject.Coordinate;
import main.Game;

public class HumanPlayer extends BasePlayer
{
	public static final int INIT_HP = 10000;
	public HumanPlayer(int HP, Coordinate spawn, Indirect dir, String name, Game game)
	{
		super(HP, spawn, dir, name, game);
	}
}
