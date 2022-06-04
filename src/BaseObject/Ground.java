package BaseObject;

public class Ground extends BaseObject {
    Ground(int x, int y) {
        super("ground", x, y);
        isPassable = true;
        isBreakable = false;
    }
    @Override
    public void interactWithBomb(Bomb b) {
        // TODO Auto-generated method stub
        // 计划用flow类来替代这个对象？所以这个留白，炸弹范围留给炸弹内操作
    }
}
