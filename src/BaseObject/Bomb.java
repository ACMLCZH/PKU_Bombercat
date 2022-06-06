package BaseObject;

import static DEBUG.Dbg.msg;

import BasePlayer.BasePlayer;
import BasePlayer.Indirect;
import main.Game;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
// import java.util.Set;


public class Bomb extends BaseObject
{
    private static final int bombTime = 5;  // change if need.
	private static final Map<Indirect, String> toFlowName = new HashMap<>() {{
		put(Indirect.UP, "vertflow"); put(Indirect.DOWN, "vertflow");
		put(Indirect.LEFT, "horiflow"); put(Indirect.RIGHT, "horiflow");
	}};
    private int timeBeforeBomb;
    private int bombRange;
	private int atk;
    private BasePlayer master;
    private Game game;
    private long lastUpdated;
    private boolean exploded;

    public Bomb(Game g, int x, int y, BasePlayer m, int atk, int bombRange)
	{
        super("bomb", x, y);
        this.game = g;
		this.atk = atk;
        this.master = m;
        this.bombRange = bombRange;   // 如果有道具影响的话再作更改
        this.timeBeforeBomb = bombTime;
		this.isBreakable = false;
        this.isPassable = false;
        this.exploded = false;
        this.lastUpdated = System.currentTimeMillis();
    }

	@Override
	public String toString() {return name + "_" + getState();}
		// msg(name + "_" + getState());
	
		public int getState() {return Math.min(bombTime - timeBeforeBomb + 1, 5);}	// return the time bomb have placed.

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

	private void createFlow(String name, Coordinate co, boolean drop)
	{
		Flow flow = new Flow(game, name, co.x, co.y, atk, drop);
		// msg(name);
		game.getFlows().add(flow);
		game.getMap().set(co, flow);
	}

    public void explode() 
    {
        // 对周围的障碍物造成影响, 并且在范围内创建Flow对象
        // GameMap gameMap = game.getMap();
        // ArrayList<Flow> flows = game.getFlows();
        // int x = 0, y = 0;
		for (Indirect dir: Indirect.values())
		{
			Coordinate curLoc = new Coordinate(loc);
			for (int i = 1; i <= bombRange; ++i)
			{
				curLoc.step(dir, 1);
				if (curLoc.x < 0 || curLoc.x >= GameMap.WIDTH ||
					curLoc.y < 0 || curLoc.y >= GameMap.HEIGHT)
					break;
				BaseObject obj = game.getMap().get(curLoc);
				if (obj != null)
				{
					obj.interactWithBomb(this);
					if (obj.isBreakable) createFlow(toFlowName.get(dir), curLoc, Math.random() < 0.3);
					// Flow flow = new Flow(toFlowName.get(dir), x, y, atk);
					if (!obj.isPassable) break;
				}
				else createFlow(toFlowName.get(dir), curLoc, false);
			}
		}
		createFlow("crossflow", loc, false);
		master.recoverBomb();
	}
    //     for (x = loc.x - bombRange; x <= loc.x + bombRange; ++x)
    //     {
    //         if (x == loc.x) continue;
    //         y = loc.y;
    //         if (x >= 0 && x < GameMap.WIDTH)
    //         {
    //             BaseObject obj = gameMap.get(new Coordinate(x, y));
    //             if (obj != null) 
    //             {
    //                 obj.interactWithBomb(this);
    //                 if (obj.isBreakable)
    //                 {
    //                     Flow flow = new Flow("horiflow", x, y, atk);
    //                     flows.add(flow);
    //                     gameMap.set(new Coordinate(x, y), flow);
    //                 }
    //             }
    //             else
    //             {
    //                 Flow flow = new Flow("horiflow", x, y, atk);
    //                 flows.add(flow);
    //                 gameMap.set(new Coordinate(x, y), flow);
    //             }
    //         }
    //     }
    //     for (y = loc.y - bombRange; y <= loc.y + bombRange; ++y)
    //     {
    //         if (y == loc.y) continue;
    //         x = loc.x;
    //         if (y >= 0 && y < GameMap.HEIGHT)
    //         {
    //             BaseObject obj = gameMap.get(new Coordinate(x, y));
    //             if (obj != null)
    //             {
    //                 obj.interactWithBomb(this);
    //                 if (obj.isBreakable)
    //                 {
    //                     Flow flow = new Flow("vertflow", x, y, atk);
    //                     flows.add(flow);
    //                     gameMap.set(new Coordinate(x, y), flow);
    //                 }
    //             }
    //             else
    //             {
    //                 Flow flow = new Flow("vertflow", x, y, atk);
    //                 flows.add(flow);
    //                 gameMap.set(new Coordinate(x, y), flow);
    //             }
    //         }
    //     }
    //     Flow flow = new Flow("crossflow", loc.x, loc.y, atk);
    //     flows.add(flow);
    //     gameMap.set(loc, flow);
    // }

    public void interactWithBomb(Bomb bomb)
    {
        timeBeforeBomb = 0;
        lastUpdated = System.currentTimeMillis();
        exploded = true;
    }
    public int getBombRange() { return bombRange;}
}
