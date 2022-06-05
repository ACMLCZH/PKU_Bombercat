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
	static int speed = 2; // 每秒移动多少个格子
	static int periodPerMove = (int)(1000.0 / (speed * pixelsPerBlock)); // 每次移动1像素后停多久
    protected int HP;
	protected Indirect dir;
	protected String name = null;
	protected int x1, y1, x2, y2;		// Bounding Box
	protected long lastHurt;
	protected long lastMove;
	protected Game game;

	public BasePlayer(int HP, Indirect dir, String name, Game game)
	{
		this.HP = HP;
		this.dir = dir;
		this.name = name;
		this.lastHurt = 0;
		this.lastMove = 0;
		this.game = game;
	}
    
	public Indirect getDirection() {return dir;}
	public int getBottom() {return y2;}
	public int getRight() {return x2;}
	public int getLeft() {return x1;}
	public int getUp() {return y1;}
	public String getName() {return name;}
	public boolean getInvincible() 
	{
		return true;
	}

    public boolean move(Indirect dir) 
	{
        // 判断move的时间间隔是否满足
		long current = System.currentTimeMillis();
		if (current - lastMove < BasePlayer.periodPerMove)
			return false;
		// 计算移动后四个角所在的格子, 向下取整
		int x1Grid = x1 / BasePlayer.pixelsPerBlock;
		int x2Grid = x2 / BasePlayer.pixelsPerBlock;
		int y1Grid = y1 / BasePlayer.pixelsPerBlock;
		int y2Grid = y2 / BasePlayer.pixelsPerBlock;
		if (dir == UP)
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
		int centerX = (int)((x1 + x2) / 2.0 / BasePlayer.pixelsPerBlock);
		int centerY = (int)((y1 + y2) / 2.0 / BasePlayer.pixelsPerBlock);
		return new Coordinate(centerX, centerY);
	}
}
