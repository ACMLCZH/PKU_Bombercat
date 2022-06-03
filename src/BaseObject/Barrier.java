package BaseObject;

public class Barrier extends BaseObject{
    int durability;
    Barrier(int x, int y) {
        super("?", x, y);
        isBreakable = true;
        isPassable = false;
        durability = 1; // 
    }
    Barrier(int x, int y, boolean isDestroy) {
        super(isDestroy ? "destroyable" : "unbreakable", x, y);
        isBreakable = false;
        isPassable = false;
        durability = isDestroy ? 100000 : 1 ;
    }
    // use for remove barrier?
    void destroy() {
        durability = 0;
        isPassable = true;
    }
}
