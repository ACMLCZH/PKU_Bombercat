package BaseObject;

import BasePlayer.BasePlayer;

public class Bomb extends BaseObject {
    static final int bombTime = 5;  // change if need.
    private int timeBeforeBomb;
    private int bombRange;
    private BasePlayer master;
    public Bomb(int x, int y, BasePlayer m) {
        super("bomb", x, y);
        master = m;
        bombRange = 1;   // 如果有道具影响的话再作更改
        timeBeforeBomb = bombTime;
        isBreakable = true;
        isPassable = false;
    }

    // use for decrease and return true if bomb.
    public boolean countDown() {
        assert timeBeforeBomb > 0: "the bomb has been destroyed";
        timeBeforeBomb -= 1;
        if(timeBeforeBomb == 0) {
            destroyed();
            return true;
        }
        return false;
    }

    // return the time bomb have placed.
    public int getState() {
        return bombTime - timeBeforeBomb;
    }

    public int destroyed() {
        // 更改player里与bomb相关的属性？
        // 还是生成一个爆炸事件返回到Game里去？
        return bombRange;
    }
}
