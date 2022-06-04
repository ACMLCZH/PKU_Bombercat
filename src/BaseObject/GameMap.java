package BaseObject;

import java.io.FileInputStream;
import java.util.Scanner;
import BaseObject.BaseObject;

public class GameMap //implements Serializable
{
	public static class OutOfMapIndexException extends Exception
	{
		public OutOfMapIndexException() {super("非法的地图元件编号！");}
	}
	public static class FailureReadMapException extends Exception
	{
		public FailureReadMapException(Throwable cause) {super("读取地图失败！", cause);}
	}
	public static enum MapElement {
		Ground("ground", 0), Destroyable("destroyable", 1), Unbreakable("unbreakable", 2),
		Bomb("bomb", 3), Horiflow("horiflow", 4), Vertflow("vertflow", 5), Crossflow("crossflow", 6),
		Bombitem("bombitem", 7), Flowitem("flowitem", 8), Speeditem("speeditem", 9);
		private String s;
		private int num;
		private MapElement(String s, int num) {this.s = s; this.num = num;}
		public String toString() {return this.s;}
		public int toInt() {return this.num;}
		// public boolean equals(int x) {return this.num == x;}
	}
	// public static final MapElement[] toElement = new MapElement[]{
	// 	MapElement.Ground, MapElement.Destroyable, MapElement.Unbreakable,
	// 	MapElement.Bomb, MapElement.Horiflow, MapElement.Vertflow, MapElement.Crossflow,
	// 	MapElement.Bombitem, MapElement.Flowitem, MapElement.Speeditem
	// };
	// static final int GROUND = 0, DESTROYABLE = 1, UNBREAKABLE = 2;
	// static final int BOMB = 3, HORIFLOW = 4, VERTFLOW = 5, CROSSFLOW = 6;
	// static final int BOMBITEM = 7, FLOWITEM = 8, SPEEDITEM = 9;
	public static final int WIDTH = 15;			// 实际可活动范围为 15 * 15
	public static final int HEIGHT = 15;
	private String type = null;
	// private Image[] textures = new Image[3];
	// private Image ground = null;
	// private Image destroyable = null;
	// private Image unbreakable = null;
	public BaseObject[][] mp;
	
	public GameMap(String type, int id) throws FailureReadMapException
	{
		this.type = type;
		try
		{
			Scanner mapInput = new Scanner(new FileInputStream("./res/map/" + type + "_" + id + ".txt"));
			mp = new BaseObject[HEIGHT][];
			for (int i = 0; i < HEIGHT; ++i) mp[i] = new BaseObject[WIDTH];
			for (int i = 1; i < HEIGHT - 1; ++i) 
			{
				for (int j = 1; j < WIDTH - 1; ++j) 
				{
					int k = mapInput.nextInt();
					switch (k) 
					{
						case 0: //ground
							mp[i][j] = null;
							break;
						case 1: //destroyable
							mp[i][j] = new Barrier(i, j, true);
							break;
						case 2: //unbreakable
							mp[i][j] = new Barrier(i, j, false);
							break;
						default:
							// 初始化的地图应该只有以上三种地形，有道具另说。
							throw new Exception("the init map have some error.");
					}
				}
			}
			mapInput.close();
		}
		catch (Exception ex)
		{
			// ex.printStackTrace();
			throw new FailureReadMapException(ex);
		}
	}
	// public Image getTexture(int idx) {return textures[idx];}
	public String getType() {return this.type;}
	// public Image getGround() {return this.ground;}
	// public Image getDestroyable() {return this.destroyable;}
	// public Image getUnbreakable() {return this.unbreakable;}
	// public int getWidth() {return this.width;}
	// public int getHeight() {return this.height;}

	public BaseObject get(Coordinate loc)
	{
		return mp[loc.x][loc.y];
	}
	public void set(Coordinate loc, BaseObject obj)
	{
		BaseObject setmp = mp[loc.x][loc.y];
		// 不可被破坏的障碍无法被操作
		if(setmp!= null && !setmp.isBreakable && !setmp.isPassable)
			return ;
		mp[loc.x][loc.y] = obj;
	}
}
