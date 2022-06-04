package BaseObject;

public class Barrier extends BaseObject{
    private int durability;
    Barrier(int x, int y, boolean isDestroy) {
        super(isDestroy ? "destroyable" : "unbreakable", x, y);
        isBreakable = isDestroy;
        isPassable = false;
    }
    // use for remove barrier?
    public void interactWithBomb(Bomb bomb) 
    {
        durability = 0;
        isPassable = true;
    }
}
