package BaseObject;

public class Flow extends BaseObject
{
    static int flowTime = 500; 	// 假设flow的寿命是0.5秒 
    long lastUpdated;

    public Flow(int x, int y, String name)
    {
        super(name, x, y);
        isBreakable = false;
        isPassable = true;
        lastUpdated = System.currentTimeMillis();
    }

    public boolean countDown()
    {
        long current = System.currentTimeMillis();
        return current - lastUpdated >= flowTime;
    }

    public void interactWithBomb(Bomb bomb)
    {
        if (bomb.getPosX() == loc.x && bomb.getPosY() == loc.y)
            name = "crossflow";
        else if (bomb.getPosX() == loc.x)
            name = "vertflow";
        else if (bomb.getPosY() == loc.y)
            name = "horiflow";
        lastUpdated = System.currentTimeMillis();
    }
}
