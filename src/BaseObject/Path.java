package BaseObject;

public class Path extends BaseObject {
    Path(int x, int y) {
        super(x, y);
        isPassable = true;
        isBreakable = false;
    }
}
