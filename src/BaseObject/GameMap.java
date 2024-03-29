package BaseObject;

import java.io.FileInputStream;
import java.util.Scanner;

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
	public static final int WIDTH = 15, HEIGHT = 15;			// 实际可活动范围为 15 * 15
	public static final int GROUND = 0, DESTROYABLE = 1, UNBREAKABLE = 2;
	public static final int NUM_SPAWN = 4;
	private String type = null;
	private double renderRate;
	private BaseObject[][] mp = null;
	private Coordinate[] spawnPoint = new Coordinate[NUM_SPAWN];
	
	public GameMap(String type, int id) throws FailureReadMapException
	{
		this.type = type;
		try {
			Scanner mapInput;
			mapInput = new Scanner(new FileInputStream("./res/map/" + type + "_" + id + ".txt"));
			mp = new BaseObject[HEIGHT][];
			for (int i = 0; i < HEIGHT; ++i) mp[i] = new BaseObject[WIDTH];
			for (int i = 0; i < HEIGHT; ++i) 
				for (int j = 0; j < WIDTH; ++j)
					switch (mapInput.nextInt()) 
					{
						case GROUND: mp[i][j] = null; break;
						case DESTROYABLE: mp[i][j] = new Barrier(j, i, true); break;
						case UNBREAKABLE: mp[i][j] = new Barrier(j, i, false); break;
						default: throw new OutOfMapIndexException();
					}
			for (int i = 0; i < NUM_SPAWN; ++i)
			{
				int y = mapInput.nextInt();
				int x = mapInput.nextInt();
				spawnPoint[i] = new Coordinate(x, y);
			}
			renderRate = mapInput.nextDouble();
			mapInput.close();
		} catch (Exception ex) {
			throw new FailureReadMapException(ex);
		}
	}

	public String getType() {return this.type;}
	public double getRate() {return this.renderRate;}
	public Coordinate getSpawn(int idx) {return this.spawnPoint[idx];}
	public BaseObject get(Coordinate loc) {return mp[loc.y][loc.x];}	// 注意loc.x是横向
	public BaseObject get(int x, int y) {return mp[y][x];}
	public void set(Coordinate loc, BaseObject obj) {mp[loc.y][loc.x] = obj;}
}
