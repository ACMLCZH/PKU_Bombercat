package BasePlayer;

import BaseObject.BaseObject;
import BaseObject.Bomb;
import BaseObject.Coordinate;
import BaseObject.GameMap;
import main.Game;
import static render.MainRenderer.BLOCK_UNIT;


public class BasePlayer implements Comparable<BasePlayer>
{
    public enum Indirect {					// 这个是必要的，最好别改
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right"), STOP("stop");
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
	protected Coordinate p1, p2;		// Bounding Box
	protected long lastHurt;
	protected long lastMove;
	protected Game game;

	public BasePlayer(int HP, Coordinate spawn, Indirect dir, String name, Game game)
	{
		this.p1 = new Coordinate(spawn.x * BLOCK_UNIT, spawn.y * BLOCK_UNIT);
		this.p2 = new Coordinate(p1.x + PLAYER_UNIT, p1.y + PLAYER_UNIT);
		// this.x1 = spawn.x * BLOCK_UNIT;
		// this.x2 = this.x1 + PLAYER_UNIT;
		// this.y1 = spawn.y * BLOCK_UNIT;
		// this.y2 = this.y1 + PLAYER_UNIT;
		this.HP = HP;
		this.dir = dir;
		this.name = name;
		this.lastHurt = 0;
		this.lastMove = 0;
		this.game = game;
	}
    
	@Override
	public int compareTo(BasePlayer p) {return p1.compareTo(p.p1);}
	@Override
	public String toString() {return this.name + "_" + this.dir;}

	public Indirect getDirection() {return dir;}
	public int getBottom() {return p2.y;}
	public int getRight() {return p2.x;}
	public int getLeft() {return p1.x;}
	public int getUp() {return p1.y;}
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
		int x1New = p1.x, x2New = p2.x, y1New = p1.y, y2New = p2.y;
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
		int x1Grid = p1.x / BasePlayer.pixelsPerBlock;
		int x2Grid = p2.x / BasePlayer.pixelsPerBlock;
		int y1Grid = p1.y / BasePlayer.pixelsPerBlock;
		int y2Grid = p2.y / BasePlayer.pixelsPerBlock;
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
		p1.x = x1New;
		p2.x = x2New;
		p1.y = y1New;
		p2.y = y2New;
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
		int centerX = (int)((p1.x + p2.x) / 2.0 / BasePlayer.pixelsPerBlock);
		int centerY = (int)((p1.y + p2.y) / 2.0 / BasePlayer.pixelsPerBlock);
		return new Coordinate(centerX, centerY);
	}
}
