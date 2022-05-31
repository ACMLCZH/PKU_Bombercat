package BaseObject;

interface Renderable {
    int textureId = 0;
}

public class BaseObject implements Renderable {
    static int objNums = 0;
    int id;               // use for sort 
    boolean isPassable;   //
    boolean isBreakable;  //
    class coordinate {
        int x, y;
    };
    coordinate loc;

    BaseObject(int x, int y) {
        loc.x = x;
        loc.y = y;
        objNums += 1;
        id = objNums;
    }
}
