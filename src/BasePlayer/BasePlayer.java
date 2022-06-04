package BasePlayer;

import BaseObject.Coordinate;
import main.Game;


public class BasePlayer {
    enum Indirect {					// 这个是必要的，最好别改
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right");
		private String s;
		private Indirect(String s) {this.s = s;}
		@Override
		public String toString() {return s;}
    };
	static int invincibleTime = 1500; // 收到攻击后无敌1.5s
	static int pixelsPerBlock = 40; // 每个格子40个像素
    // private int coolingTime;
    protected int HP;
	protected Indirect dir;
	protected String name = null;
	protected int x1, y1, x2, y2;		// Bounding Box
	protected long lastHurt;

	public BasePlayer(int HP, Indirect dir, String name)
	{
		this.HP = HP;
		this.dir = dir;
		this.name = name;
		this.lastHurt = 0;
	}
    
	public Indirect getDirection() {return dir;}
	public int getBottom() {return y2;}
	public int getRight() {return x2;}
	public int getLeft() {return x1;}
	public String getName() {return name;}

    public boolean move(Game g, Indirect dir) {
        return false;
    }

    public boolean placeBomb(Game g) {
        return false;
    }

	public boolean isAlive()
	{
		return HP > 0;
	}

	public void getHurt()
	{
		long current = System.currentTimeMillis();
		if (HP > 0 && current - lastHurt >= BasePlayer.invincibleTime)
		{
			HP--;
			lastHurt = current;
		}
	}

	public Coordinate getGridLoc()
	{
		int centerX = (int)Math.round((x1 + x2) / 2.0);
		int centerY = (int)Math.round((y1 + y2) / 2.0);
		return new Coordinate(centerX, centerY);
	}
}
