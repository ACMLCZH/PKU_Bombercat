package BaseObject;

public class Barrier extends BaseObject{
    private int durability;
    Barrier(int x, int y, boolean isDestroy) {
        super(isDestroy ? "destroyable" : "unbreakable", x, y);
        isBreakable = isDestroy;
        isPassable = false;
        durability = isDestroy ? 1 : 100000;//大概是用来区分是否可以破坏的屏障，不过有isDestroy是不是就没什么用了。
    }
    // use for remove barrier?
    void destroy() {
        durability = 0;
        isPassable = true;
    }
}
