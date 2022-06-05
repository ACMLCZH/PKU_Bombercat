package BasePlayer;

import BaseObject.BaseObject;
import BaseObject.Bomb;
import BaseObject.Coordinate;
import BaseObject.GameMap;
import main.Game;
import static render.MainRenderer.BLOCK_UNIT;


public class BasePlayer
{
    public enum Indirect {					// 这个是必要的，最好别改
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right");
		private String s;
		private Indirect(String s) {this.s = s;}
		@Override
		public String toString() {return s;}
    };
	public static final int PLAYER_UNIT = BLOCK_UNIT;
	static final int invincibleTime = 1500; // 收到攻击后无敌1.5s
	static final int pixelsPerBlock = 40; // 每个格子40个像素
	static final int speed = 2; // 每秒移动多少个格子
	static final int periodPerMove = (int)(1000.0 / (speed * pixelsPerBlock)); // 每次移动1像素后停多久
    protected int HP;
	protected Indirect dir;
	protected String name = null;
	protected int x1, y1, x2, y2;		// Bounding Box
	protected long lastHurt;
	protected long lastMove;
	protected Game game;

	public BasePlayer(int HP, Coordinate spawn, Indirect dir, String name, Game game)
	{
		this.x1 = spawn.x * BLOCK_UNIT;
		this.x2 = this.x1 + PLAYER_UNIT;
		this.y1 = spawn.y * BLOCK_UNIT;
		this.y2 = this.y1 + PLAYER_UNIT;
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
		// 计算移动后四个角所在像素, 向下取整
		int x1New = x1, x2New = x2, y1New = y1, y2New = y2;
		if (dir == Indirect.UP)
		{
			y1New -= 1;
			y2New -= 1;
		}
		else if (dir == Indirect.DOWN)
		{
			y1New += 1;
			y2New += 1;
		}
		else if (dir == Indirect.LEFT)
		{
			x1New -= 1;
			x2New -= 1;
		}
		else
		{
			x1New += 1;
			x2New += 1;
		}

		// 计算移动前后所在格子
		int x1Grid = x1 / BasePlayer.pixelsPerBlock;
		int x2Grid = x2 / BasePlayer.pixelsPerBlock;
		int y1Grid = y1 / BasePlayer.pixelsPerBlock;
		int y2Grid = y2 / BasePlayer.pixelsPerBlock;
		int x1NewGrid = x1New / BasePlayer.pixelsPerBlock;
		int x2NewGrid = x2New / BasePlayer.pixelsPerBlock;
		int y1NewGrid = y1New / BasePlayer.pixelsPerBlock;
		int y2NewGrid = y2New / BasePlayer.pixelsPerBlock;
		// 判断是否会超出边界
		if (x1Grid < 0 || x2Grid >= GameMap.WIDTH || y1Grid < 0 || y2Grid >= GameMap.HEIGHT)
			return false;

		// 检查碰撞
		GameMap gameMap = game.getMap();
		BaseObject obj = null, objNew = null;
		obj = gameMap.get(new Coordinate(x1Grid, y1Grid));
		objNew = gameMap.get(new Coordinate(x1NewGrid, y1NewGrid));
		if (objNew != null && !objNew.getIsPassable() && objNew != obj)
			return false;
		obj = gameMap.get(new Coordinate(x2Grid, y1Grid));
		objNew = gameMap.get(new Coordinate(x2NewGrid, y1NewGrid));
		if (objNew != null && !objNew.getIsPassable() && objNew != obj)
			return false;
		obj = gameMap.get(new Coordinate(x1Grid, y2Grid));
		objNew = gameMap.get(new Coordinate(x1NewGrid, y2NewGrid));
		if (objNew != null && !objNew.getIsPassable() && objNew != obj)
			return false;
		obj = gameMap.get(new Coordinate(x2Grid, y2Grid));
		objNew = gameMap.get(new Coordinate(x2NewGrid, y2NewGrid));
		if (objNew != null && !objNew.getIsPassable() && objNew != obj)
			return false;
		
		// 移动成功
		x1 = x1New;
		x2 = x2New;
		y1 = y1New;
		y2 = y2New;
		this.dir = dir;
		lastMove = current;
		return true;
    }

    public boolean placeBomb() 
	{
        Coordinate center = getGridLoc();
		if (game.getMap().get(center) != null)
			return false;
		Bomb bomb = new Bomb(center.x, center.y, this, game);
		game.getMap().set(center, bomb);
		game.getBombs().add(bomb);
		return true;
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
