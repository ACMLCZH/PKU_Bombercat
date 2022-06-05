package BasePlayer;

import BaseObject.Coordinate;
import main.Game;

public class AIPlayer extends BasePlayer
{
	public static int INIT_HP = 8000;
    public AIPlayer(int HP, Coordinate spawn, Indirect dir, String name, Game game)
	{
		super(HP, spawn, dir, name, game);
	}

    void mainloop() {
        
    }
}
