package BaseObject;

public class Flow extends BaseObject
{
    static int flowTime = 500; 	// 假设flow的寿命是0.5秒
	private int atk;
    private long lastUpdated;

    public Flow(String name, int x, int y, int atk)
    {
        super(name, x, y);
		this.atk = atk;
        isBreakable = false;
        isPassable = true;
        lastUpdated = System.currentTimeMillis();
    }

	@Override
	public String toString() {return this.name;}
	@Override
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

	public int getAtk() {return this.atk;}

    public boolean countDown()
    {
        long current = System.currentTimeMillis();
        return current - lastUpdated >= flowTime;
    }

}
