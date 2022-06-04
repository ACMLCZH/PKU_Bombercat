package BaseObject;

public class Barrier extends BaseObject
{
    private int durability;
    public Barrier(int x, int y, boolean isDestroy)
	{
        super(isDestroy ? "destroyable" : "unbreakable", x, y);
        isBreakable = isDestroy;
        isPassable = false;
        durability = 1;
    }

	@Override
	public String toString() {return name;}
    // use for remove barrier?
    public void interactWithBomb(Bomb bomb) 
    {
        if (isBreakable) {durability = 0; isPassable = true;}
    }
    public boolean isDestroyed()
    {
        return durability > 0;
    }
}
