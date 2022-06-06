package BasePlayer;

import BaseObject.Coordinate;
import BaseObject.GameMap;
import main.Game;

public class HumanPlayer extends BasePlayer
{
	public static final int INIT_HP = 10000;
	public static final int ATK = 1000;
	public static final int MOVE_TOLERANCE = 10;
	private boolean placing = false;

	public HumanPlayer(Game game, String name, int HP, Coordinate spawn, Indirect dir)
	{
		super(game, name, HP, spawn, dir, ATK);
	}

	@Override
	public boolean move()
	{
		if (super.move())
			return true;
		if (!hitBarrier)
			return false;
		hitBarrier = false;
		if (dir == Indirect.UP || dir == Indirect.DOWN)
		{
			// x校正
			int oldX1 = p1.x;
			int oldX2 = p2.x;
			int newX1 = Math.round((float)p1.x / BasePlayer.pixelsPerBlock) * BasePlayer.pixelsPerBlock;
			int newX2 = newX1 + oldX2 - oldX1;
			if (Math.abs(newX1 - oldX1) > HumanPlayer.MOVE_TOLERANCE)
				return false;
			p1.x = newX1;
			p2.x = newX2;
			// 再次尝试移动
			if (super.move())
				return true;
			// 移动依然失败, 回滚
			p1.x = oldX1;
			p2.x = oldX2;
			return false;
		}
		else
		{
			// y校正
			int oldY1 = p1.y;
			int oldY2 = p2.y;
			int newY1 = Math.round((float)p1.y / BasePlayer.pixelsPerBlock) * BasePlayer.pixelsPerBlock;
			int newY2 = newY1 + oldY2 - oldY1;
			if (Math.abs(newY1 - oldY1) > HumanPlayer.MOVE_TOLERANCE)
				return false;
			p1.y = newY1;
			p2.y = newY2;
			// 再次尝试移动
			if (super.move())
				return true;
			// 移动依然失败, 回滚
			p1.y = oldY1;
			p2.y = oldY2;
			return false;
		}
	}

	@Override
	public boolean placeBomb()
	{
		if (!placing) {this.placing = true; return super.placeBomb();}
		else return false;
	}
	public void release() {placing = false;}
	// public boolean isPlacing() {return this.placing;}
}
