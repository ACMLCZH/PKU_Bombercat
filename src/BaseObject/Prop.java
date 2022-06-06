package BaseObject;

import java.util.HashMap;
import java.util.Map;

import BasePlayer.BasePlayer;

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
        super(toItemName.get(t), x, y);
        isPassable = true;
        isBreakable = true;
    }

    public void buff(BasePlayer player)
    {
        switch(name)
        {
            case "bombitem":
                player.addBomb();
                break;
            case "flowitem":
                player.addRange();
                break;
            case "speeditem":
                player.addSpeed(0.5);
                break;
        }
    }

    @Override
    public void interactWithBomb(Bomb b) {}
	@Override
	public String toString() {return this.name;}
}
