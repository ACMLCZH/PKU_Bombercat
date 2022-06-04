package BaseObject;

import BasePlayer.BasePlayer;

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
        for (int x = loc.x - bombRange; x <= loc.x + bombRange; x++)
            for (int y = loc.y - bombRange; y <= loc.y + bombRange; y++)
            {
                if (x >= 0 && x < GameMap.WIDTH && y >= 0 && y < GameMap.HEIGHT)
                {
                    BaseObject obj = gameMap.get(new Coordinate(x, y));
                    if (obj != null)
                        obj.interactWithBomb(this);
                }
            }
    }

    public void interactWithBomb(Bomb bomb)
    {
        timeBeforeBomb = 0;
        lastUpdated = System.currentTimeMillis();
        exploded = true;
    }
}
