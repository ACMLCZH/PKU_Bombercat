package BaseObject;

public class Bomb extends BaseObject {
    static final int bombTime = 5;  // change if need.
    int timeBeforeBomb;
    int bombRange;
    public Bomb(int x, int y) {
        super("bomb", x, y);
        bombRange = 1;
        isBreakable = true;
        isPassable = false;
        timeBeforeBomb = bombTime;
    }

    // use for decrease and return true if bomb.
    boolean countDown() {
        timeBeforeBomb -= 1;
        if(timeBeforeBomb == 0) {
            return true;
        }
        return false;
    }

    // return the time bomb have placed.
    public int getState() {
        return bombTime - timeBeforeBomb;
    }
}
