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
   
    public boolean isDestroyed() {return durability == 0;}
	
    public void interactWithBomb(Bomb bomb)  // use for remove barrier
    {
        if (isBreakable) durability = 0;
    }
}
