package BasePlayer;

import static DEBUG.Dbg.msg;

import BaseObject.BaseObject;
import BaseObject.Bomb;
import BaseObject.Coordinate;
import BaseObject.GameMap;
import BaseObject.Prop;
import main.Game;
import static render.MainRenderer.BLOCK_UNIT;

import java.util.Map;
import java.util.HashMap;


public class BasePlayer implements Comparable<BasePlayer>
{
	public static final int PLAYER_UNIT = 40;
	public static final int STRIDE = 1;
	public static final double SPEEDLIMIT = 7.0;
	public static final int BOMBLIMIT = 6;
	public static final int FLOWLIMIT = 10;
	protected static final int pixelsPerBlock = BLOCK_UNIT; 	// 每个格子40个像素
	private static final int invincibleTime = 1500; 			// 收到攻击后无敌1.5s
	private static final Map<Indirect, int[]> colliDetect = new HashMap<Indirect, int[]>() {{
		put(Indirect.UP, new int[] {0, 1}); put(Indirect.DOWN, new int[] {2, 3});
		put(Indirect.LEFT, new int[] {0, 2}); put(Indirect.RIGHT, new int[] {1, 3});
	}};
	// protected int gameMode;
    protected int HP;
	protected Coordinate p1, p2;		// Bounding Box
	protected int atk;
	protected int numBomb, bombRange;
	protected double speed;
	protected int periodPerMove;        // 每次移动1像素后停多久
	protected Indirect dir;
	protected String name = null;
	protected boolean isMoving = false;
	protected int numPutBomb = 0;
	protected int curDmg = 0;
	protected long lastHurt = 0;
	protected long lastMove = 0;
	protected Game game;
	protected boolean hitBarrier = false; // 辅助校正用

	public BasePlayer(Game game, String name,
					  int HP, Coordinate spawn, int atk, int numBomb, int bombRange, double speed)
	{
		this.dir = Indirect.DOWN;
		this.game = game;
		this.name = name;
		// this.gameMode = gameMode;
		this.p1 = new Coordinate(spawn.x * BLOCK_UNIT + (BLOCK_UNIT - PLAYER_UNIT) / 2,
								 spawn.y * BLOCK_UNIT + (BLOCK_UNIT - PLAYER_UNIT) / 2);
		this.p2 = new Coordinate(p1.x + PLAYER_UNIT - 1, p1.y + PLAYER_UNIT - 1);
		this.HP = HP;
		this.atk = atk;
		this.numBomb = numBomb;
		this.bombRange = bombRange;
		this.speed = speed;
		this.periodPerMove = (int)(1000.0 * STRIDE / (this.speed * pixelsPerBlock));
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
	public int getDmg() {return curDmg;}
	public boolean isAlive() {return HP > 0;}
	public boolean isInvincible(long cur) {return cur - lastHurt < BasePlayer.invincibleTime;}
	public boolean isInvincible() {return System.currentTimeMillis() - lastHurt < BasePlayer.invincibleTime;}

	public void setIndirect(Indirect dir) {this.dir = dir;}
	public void setMove(boolean isMoving) {this.isMoving = isMoving;}
	public void addSpeed(double ad) {this.speed = Math.min(this.speed + ad, SPEEDLIMIT);}
	public void addRange() {++this.bombRange; if (this.bombRange > FLOWLIMIT) this.bombRange = FLOWLIMIT;}
	public void addBomb() {++this.numBomb; if (this.numBomb > BOMBLIMIT) this.numBomb = BOMBLIMIT;}

    public boolean move() 
	{
		// if (dir != Indirect.STOP) this.dir = dir;		// 不管成没成功都转个向
		if (!isMoving) return false;

        // 判断move的时间间隔是否满足
		long current = System.currentTimeMillis();
		if (current - lastMove < this.periodPerMove)
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
		for (int i = 0; i < 2; ++i)		// 只要判断人物前面的两个角就行
		{
			BaseObject obj = gameMap.get(ca[colliDetect.get(dir)[i]][0]);
			BaseObject objNew = gameMap.get(ca[colliDetect.get(dir)[i]][1]);
			if (objNew != null && !objNew.getIsPassable() && objNew != obj)
			{
				hitBarrier = true;
				return false;
			}
		}
		for (int i = 0; i < 2; ++i)
		{
			Coordinate co = ca[colliDetect.get(dir)[i]][1];
			BaseObject objNew = gameMap.get(co);
			if (objNew != null && objNew instanceof Prop)
			{
				if (this instanceof HumanPlayer) ((Prop)objNew).buff(this);
				gameMap.set(co, null);
			}
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
		if (numPutBomb == numBomb) return false;
		if (game.getMap().get(center) != null) return false;
		Bomb bomb = new Bomb(game, center.x, center.y, this, this.atk, this.bombRange);
		game.getMap().set(center, bomb);
		game.getBombs().add(bomb);
		++numPutBomb;
		return true;
    }

	public void recoverBomb() {--numPutBomb;}

	public void getHurt(int dmg)
	{
		long current = System.currentTimeMillis();
		if (HP > 0 && !isInvincible(current))
		{
			HP -= dmg;
			lastHurt = current;
			curDmg = dmg;
		}
	}

	public Coordinate getGridLoc()
	{
		int centerX = (int)((p1.x + p2.x) / 2.0 / BasePlayer.pixelsPerBlock);
		int centerY = (int)((p1.y + p2.y) / 2.0 / BasePlayer.pixelsPerBlock);
		return new Coordinate(centerX, centerY);
	}
}
