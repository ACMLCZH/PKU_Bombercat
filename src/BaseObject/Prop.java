package BaseObject;

import java.util.HashMap;
import java.util.Map;

// 道具类
public class Prop extends BaseObject
{
	public static Map<Integer, String> toItemName = new HashMap<>(){{
		put(0, "bombitem"); put(1, "flowitem"); put(2, "speeditem");
	}};
    public Prop(String name, int x, int y) {
        super(name, x, y);
        isPassable = true;
        isBreakable = true;
    }
    public Prop(int x, int y, int t) { //可以快速用随机数摇出来
        super((t==0) ? "bombitem" : ((t==1) ? "flowitem" : "speeditem"), x, y);
        isPassable = true;
        isBreakable = true;
    }

    @Override
    public void interactWithBomb(Bomb b) {
        // TODO Auto-generated method stub
        // 可以在这里尝试重叠flow的贴图和道具的贴图？
    }
	@Override
	public String toString() {return this.name;}
}
