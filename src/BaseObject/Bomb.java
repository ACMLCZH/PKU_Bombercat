package BaseObject;

import BasePlayer.BasePlayer;
import BaseObject.Flow;

public class Bomb extends BaseObject {
    static final int bombTime = 5;  // change if need.
    private int timeBeforeBomb;
    private int bombRange;
    private BasePlayer master;
    private long lastUpdated;
    private boolean exploded;

    public Bomb(int x, int y, BasePlayer m) {
        super("bomb", x, y);
        master = m;
        bombRange = 1;   // 如果有道具影响的话再作更改
        timeBeforeBomb = bombTime;
        isBreakable = true;
        isPassable = false;
        exploded = false;
        lastUpdated = System.currentTimeMillis();
    }

    // use for decrease and return true if bomb.
    public boolean countDown() {
        if (exploded)
            return true;
        long current = System.currentTimeMillis();
        if (current - lastUpdated >= 1000)
        {
            lastUpdated = current;
            timeBeforeBomb -= 1;
            if(timeBeforeBomb == 0) 
            {
                exploded = true;
                return true;
            }
        }
        return false;
    }

    // return the time bomb have placed.
    public int getState() {
        return bombTime - timeBeforeBomb;
    }

    public void explode(GameMap gameMap) 
    {
        int x = 0, y = 0;
        for (x = loc.x - bombRange; x <= loc.x + bombRange; x++)
        {
            if (x == loc.x)
                continue;
            y = loc.y;
            if (x >= 0 && x < GameMap.WIDTH)
            {
                BaseObject obj = gameMap.get(new Coordinate(x, y));
                if (obj != null)
                    obj.interactWithBomb(this);
                else
                    gameMap.set(new Coordinate(x, y), new Flow(x, y, "horiflow"));
            }
        }
        for (y = loc.y - bombRange; y <= loc.y + bombRange; y++)
        {
            if (y == loc.y)
                continue;
            x = loc.x;
            if (y >= 0 && y < GameMap.HEIGHT)
            {
                BaseObject obj = gameMap.get(new Coordinate(x, y));
                if (obj != null)
                    obj.interactWithBomb(this);
                else
                    gameMap.set(new Coordinate(x, y), new Flow(x, y, "vertflow"));
            }
        }
        gameMap.set(loc, new Flow(x, y, "crossflow"));
    }

    public void interactWithBomb(Bomb bomb)
    {
        timeBeforeBomb = 0;
        lastUpdated = System.currentTimeMillis();
        exploded = true;
    }
}
