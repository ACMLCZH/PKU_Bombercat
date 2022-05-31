package BaseObject;

public class Bomb extends BaseObject{
    static final int bombTime = 5;  
    int timeBeforeBomb;
    int bombRange;
    Bomb(int x, int y) {
        super(x, y);
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
}
