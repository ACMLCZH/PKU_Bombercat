package BaseObject;

import BasePlayer.BasePlayer;
import main.Game;

import java.util.Set;

import BaseObject.Flow;

public class Bomb extends BaseObject
{
    static final int bombTime = 5;  // change if need.
    private int timeBeforeBomb;
    private int bombRange;
    private BasePlayer master;
    private Game game;
    private long lastUpdated;
    private boolean exploded;

    public Bomb(int x, int y, BasePlayer m, Game g)
	{
        super("bomb", x, y);
        master = m;
        game = g;
        bombRange = 1;   // 如果有道具影响的话再作更改
        timeBeforeBomb = bombTime;
        isBreakable = true;
        isPassable = false;
        exploded = false;
        lastUpdated = System.currentTimeMillis();
    }

	@Override
	public String toString() {return name + "_" + getState();}
	
    public int getState() {return bombTime - timeBeforeBomb;}	// return the time bomb have placed.

    // use for decrease and return true if bomb.
    public boolean countDown()
	{
        if (exploded) return true;
        long current = System.currentTimeMillis();
        if (current - lastUpdated >= 1000)
        {
            lastUpdated = current;
            timeBeforeBomb -= 1;
            if (timeBeforeBomb == 0) {exploded = true; return true;}
        }
        return false;
    }

    public void explode() 
    {
        // 对周围的障碍物造成影响, 并且在范围内创建Flow对象
        GameMap gameMap = game.getMap();
        Set<Flow> flows = game.getFlows();
        int x = 0, y = 0;
        for (x = loc.x - bombRange; x <= loc.x + bombRange; ++x)
        {
            if (x == loc.x) continue;
            y = loc.y;
            if (x >= 0 && x < GameMap.WIDTH)
            {
                BaseObject obj = gameMap.get(new Coordinate(x, y));
                if (obj != null) obj.interactWithBomb(this);
                else
                {
                    Flow flow = new Flow(x, y, "horiflow");
                    flows.add(flow);
                    gameMap.set(new Coordinate(x, y), flow);
                }
            }
        }
        for (y = loc.y - bombRange; y <= loc.y + bombRange; ++y)
        {
            if (y == loc.y) continue;
            x = loc.x;
            if (y >= 0 && y < GameMap.HEIGHT)
            {
                BaseObject obj = gameMap.get(new Coordinate(x, y));
                if (obj != null)
                    obj.interactWithBomb(this);
                else
                {
                    Flow flow = new Flow(x, y, "vertflow");
                    flows.add(flow);
                    gameMap.set(new Coordinate(x, y), flow);
                }
            }
        }
        Flow flow = new Flow(x, y, "crossflow");
        flows.add(flow);
        gameMap.set(loc, flow);
    }

    public void interactWithBomb(Bomb bomb)
    {
        timeBeforeBomb = 0;
        lastUpdated = System.currentTimeMillis();
        exploded = true;
    }
}
