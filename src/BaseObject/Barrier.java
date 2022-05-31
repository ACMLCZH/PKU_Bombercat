package BaseObject;

public class Barrier extends BaseObject{
    Barrier(int x, int y) {
        super(x,y);
        isBreakable = true;
        isPassable = false;
    }
}
