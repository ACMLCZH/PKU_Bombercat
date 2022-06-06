package BasePlayer;

import static DEBUG.Dbg.msg;

import BaseObject.BaseObject;
import BaseObject.Bomb;
import BaseObject.Coordinate;
import BaseObject.GameMap;
import main.Game;
import static render.MainRenderer.BLOCK_UNIT;


public class BasePlayer implements Comparable<BasePlayer>
{
	public static final int PLAYER_UNIT = 31;
	public static final int STRIDE = 1;
	static final int invincibleTime = 1500; 		// 收到攻击后无敌1.5s
	static final int pixelsPerBlock = BLOCK_UNIT; 	// 每个格子40个像素
	static final int speed = 5; // 每秒移动多少个格子
	static final int periodPerMove = (int)(1000.0 * STRIDE / (speed * pixelsPerBlock)); // 每次移动1像素后停多久
    protected int HP;
	protected int atk;
	protected Indirect dir;
	protected String name = null;
	protected Coordinate p1, p2;		// Bounding Box
	protected boolean isMoving = false;
	protected long lastHurt = 0;
	protected long lastMove = 0;
	protected Game game;

	public BasePlayer(Game game, String name, int HP, Coordinate spawn, Indirect dir, int atk)
	{
		this.game = game;
		this.p1 = new Coordinate(spawn.x * BLOCK_UNIT + (BLOCK_UNIT - PLAYER_UNIT) / 2,
								 spawn.y * BLOCK_UNIT + (BLOCK_UNIT - PLAYER_UNIT) / 2);
		this.p2 = new Coordinate(p1.x + PLAYER_UNIT - 1, p1.y + PLAYER_UNIT - 1);
		this.HP = HP;
		this.atk = atk;
		this.dir = dir;
		this.name = name;
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
	public boolean isAlive() {return HP > 0;}
	public boolean isInvincible(long cur) {return cur - lastHurt < BasePlayer.invincibleTime;}
	public boolean isInvincible() {return System.currentTimeMillis() - lastHurt < BasePlayer.invincibleTime;}

	public void setIndirect(Indirect dir) {this.dir = dir;}
	public void setMove(boolean isMoving) {this.isMoving = isMoving;}

    public boolean move() 
	{
		// if (dir != Indirect.STOP) this.dir = dir;		// 不管成没成功都转个向
		if (!isMoving) return false;

        // 判断move的时间间隔是否满足
		long current = System.currentTimeMillis();
		if (current - lastMove < BasePlayer.periodPerMove)
			return false;
		// 计算移动后四个角所在像素, 向下取整
		Coordinate p1New = new Coordinate(p1);
		Coordinate p2New = new Coordinate(p2);
		p1New.step(this.dir);
		p2New.step(this.dir);
		// 计算移动前后所在格子
		Coordinate p1Grid = p1.toGrid(), p2Grid = p2.toGrid();
		Coordinate p1NewGrid = p1New.toGrid(), p2NewGrid = p2New.toGrid();
		// msg(new Object[] {p1, p2, p1New, p2New});
		// msg(new Object[] {p1Grid, p2Grid, p1NewGrid, p2NewGrid});
		// 判断是否会超出边界
		if (p1NewGrid.x < 0 || p2NewGrid.x >= GameMap.WIDTH || p1NewGrid.y < 0 || p2NewGrid.y >= GameMap.HEIGHT)
			return false;
		// if (p1New.x < 0 || p2New.x >= GameMap.WIDTH * BasePlayer.PLAYER_UNIT || p1New.y < 0 || p2New.y >= GameMap.HEIGHT * BasePlayer.PLAYER_UNIT)
		// 	return false;

		// 检查碰撞
		GameMap gameMap = game.getMap();
		Coordinate[][] ca = {
			{p1Grid, p1NewGrid}, {new Coordinate(p2Grid.x, p1Grid.y), new Coordinate(p2NewGrid.x, p1NewGrid.y)},
			{new Coordinate(p1Grid.x, p2Grid.y), new Coordinate(p1NewGrid.x, p2NewGrid.y)}, {p2Grid, p2NewGrid}
		};
		for (int i = 0; i < 4; ++ i)
		{
			BaseObject obj = gameMap.get(ca[i][0]), objNew = gameMap.get(ca[i][1]);
			if (objNew != null && !objNew.getIsPassable() && objNew != obj) 
				return false;
		}

		// 移动成功
		this.p1 = p1New;
		this.p2 = p2New;
		lastMove = current;
		return true;
    }

    public boolean placeBomb() 
	{
        Coordinate center = getGridLoc();
		if (game.getMap().get(center) != null)
			return false;
		Bomb bomb = new Bomb(game, center.x, center.y, this, this.atk);
		game.getMap().set(center, bomb);
		game.getBombs().add(bomb);
		return true;
    }

	public void getHurt(int dmg)
	{
		long current = System.currentTimeMillis();
		if (HP > 0 && !isInvincible(current)) {HP -= dmg; lastHurt = current;}
	}

	public Coordinate getGridLoc()
	{
		int centerX = (int)((p1.x + p2.x) / 2.0 / BasePlayer.pixelsPerBlock);
		int centerY = (int)((p1.y + p2.y) / 2.0 / BasePlayer.pixelsPerBlock);
		return new Coordinate(centerX, centerY);
	}
}
