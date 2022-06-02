package BasePlayer;

// import Game;


public class BasePlayer {
    int coolingTime;
    int HP;
    enum indirect {
        UP, DOWN, LEFT, RIGHT
    };
    
    //TODO: functions
    boolean move(Game g, indirect dir) {
        return false;
    }
    boolean placeBomb(Game g) {
        return false;
    }
}
