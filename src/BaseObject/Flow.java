package BaseObject;

public class Flow extends BaseObject
{
    static int flowTime = 2; 	// 假设flow的寿命是2秒 // TODO:（2秒太长啦！可以0.5秒吗）
    int timeBeforeDisappear;
    long lastUpdated;

    public Flow(int x, int y, String name)
    {
        super(name, x, y);
        timeBeforeDisappear = flowTime; 
        isBreakable = false;
        isPassable = true;
        lastUpdated = System.currentTimeMillis();
    }

    public boolean countDown()
    {
        if (timeBeforeDisappear == 0) return true;
        long current = System.currentTimeMillis();
        if (current - lastUpdated >= 1000)
        {
            lastUpdated = current;
            timeBeforeDisappear -= 1;
            if (timeBeforeDisappear == 0) return true;
        }
        return false;
    }

    public void interactWithBomb(Bomb bomb)
    {
        if (bomb.getPosX() == loc.x && bomb.getPosY() == loc.y)
            name = "crossflow";
        else if (bomb.getPosX() == loc.x)
            name = "vertflow";
        else if (bomb.getPosY() == loc.y)
            name = "horiflow";
        timeBeforeDisappear = Flow.flowTime;
        lastUpdated = System.currentTimeMillis();
    }
}
