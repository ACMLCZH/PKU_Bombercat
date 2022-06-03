package BaseObject;


public class BaseObject implements Comparable<BaseObject> {
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

    @Override
    public int compareTo(BaseObject o) {
        // TODO Auto-generated method stub
        int r = this.loc.y - o.loc.y;
        if(r == 0) 
            return this.loc.x - o.loc.x;
        return r;
    }

    public int getPosY() {
        return loc.y;
    }

}
