package BaseObject;

import static DEBUG.Dbg.msg;

import BasePlayer.BasePlayer;
import BasePlayer.Indirect;
import main.Game;
import thread.MusicPlayer;

import java.util.Map;
import java.util.HashMap;


public class Bomb extends BaseObject
{
    private static final int bombTime = 5;  // change if need.
	private static final Map<Indirect, String> toFlowName = new HashMap<Indirect, String>() {{
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
    private MusicPlayer boom = new MusicPlayer("music\\baozha.wav",false);

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
	public int getAtk() {return atk;}

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
    	if(boom.getState() == Thread.State.NEW){ // 如果多次start，不会重新播放音乐
    		boom.start();
        	}else {
        		boom.continues();
        	}
        	
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
					if (obj instanceof Barrier && obj.isBreakable) createFlow(
						toFlowName.get(dir), curLoc, Math.random() < game.getMap().getRate()
					); else if (obj instanceof Prop)
						createFlow(toFlowName.get(dir), curLoc, false);
					// Flow flow = new Flow(toFlowName.get(dir), x, y, atk);
					if (!obj.isPassable) break;
				}
				else createFlow(toFlowName.get(dir), curLoc, false);
			}
		}
		createFlow("crossflow", loc, false);
		master.recoverBomb();
	}

    public void interactWithBomb(Bomb bomb)
    {
        timeBeforeBomb = 0;
        lastUpdated = System.currentTimeMillis();
        exploded = true;
    }
    public int getBombRange() { return bombRange;}
}
