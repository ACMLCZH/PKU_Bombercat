package BaseObject;


// 道具，是不是应该多设置几种？
public class Prop extends BaseObject {
    Prop(int x, int y) {
        super("prop", x, y);
        isPassable = true;
        isBreakable = false;
    }
}
