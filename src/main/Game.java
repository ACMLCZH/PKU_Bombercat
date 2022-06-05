package main;

import render.MainRenderer;
import BaseObject.Barrier;
import BaseObject.BaseObject;
import BaseObject.Bomb;
import BaseObject.Coordinate;
import BaseObject.Flow;
import BaseObject.GameMap;
import BaseObject.GameMap.FailureReadMapException;
import BasePlayer.*;

import java.util.*;
import java.util.concurrent.*;;

public class Game
{
	public static final int PVP = 0, PVE = 1;

	public int mode;
	public Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
	private GameMap gameMap = null;
	private Set<BasePlayer> players = new TreeSet<>();
	private Set<Bomb> bombs = new TreeSet<>();
	private Set<Flow> flows = new TreeSet<>();
	private Set<Barrier> barriers = new TreeSet<>();
	private HumanPlayer infoPlayer = null;
	private boolean soundOn = true;
	private boolean started = false;
	private MainRenderer renderer;

	public Set<BasePlayer> getPlayers() {return this.players;}
	public HumanPlayer getInfoPlayer() {return this.infoPlayer;}
	public Set<Bomb> getBombs() {return this.bombs;}
	public GameMap getMap() {return this.gameMap;}
	public Set<Flow> getFlows() {return this.flows;}
	public Set<Barrier> getBarriers() {return this.barriers;};
	public boolean isSoundOn() {return this.soundOn;}
	public boolean isStarted() {return this.started;}

	public void switchSound() {this.soundOn = !this.soundOn;}//

    private void mainloop()
	{
		// 清空任务队列, 执行每个任务
		while (true)
		{
			Runnable cmd = commandQueue.poll();
			if (cmd != null) cmd.run(); else break;
		}

		if (!started) return;

		// 为Flow做倒计时
		Iterator<Flow> iterFlow = flows.iterator();
		while (iterFlow.hasNext())
		{
			Flow flow = iterFlow.next();
			if (flow.countDown())
			{
				// 把flow从gameMap里去掉
				gameMap.set(flow.getLoc(), null);
				// 把flow从flows里去掉
				iterFlow.remove();
			}
		}

		// 为炸弹做倒计时
		Iterator<Bomb> iterBomb = bombs.iterator();
		while (iterBomb.hasNext())
		{
			Bomb bomb = iterBomb.next();
			if (bomb.countDown())
			{
				// 和其他物体的交互, 并且把炸弹从gameMap里去掉
				bomb.explode();
				// 把炸弹从bombs里去掉
				iterBomb.remove();
			}
		}

		// 去掉被炸掉的Barrier
		Iterator<Barrier> iterBarrier = barriers.iterator();
		while (iterBarrier.hasNext())
		{
			Barrier barrier = iterBarrier.next();
			if (barrier.isDestroyed())
			{
				// 把barrier从gameMap里去掉
				gameMap.set(barrier.getLoc(), null);
				// 把barrier从barriers里去掉
				iterBarrier.remove();
			}
		}

		// 计算玩家收到伤害
		for (BasePlayer player: players)
		{
			if (!player.isAlive())
				continue;
			Coordinate loc = player.getGridLoc();
			BaseObject obj = gameMap.get(loc);
			if (obj != null)
			{
				String name = obj.getName();
				if (name == "crossflow" || name == "vertflow" || name == "horiflow")
					player.getHurt();
			}
		}
	}
	public Game()
	{
		renderer = new MainRenderer(this);
	}

	public void end() {}
	public void start(String selChar, String selScene, int mode)	// 选择的人物，选择的场景，选择的游戏模式
	{
		int modeBias = mode == PVP ? 1 : 3;
		try {
			gameMap = new GameMap(selScene, (int)(Math.random() * 2) + modeBias);
		} catch (FailureReadMapException e) {
			e.printStackTrace();
			System.exit(0);
		}
		infoPlayer = new HumanPlayer(HumanPlayer.INIT_HP, gameMap.getSpawn(0), BasePlayer.Indirect.DOWN, selChar, this);
		players.add(infoPlayer);
		for (int i = 1; i < 4; ++i)
			players.add(new AIPlayer(AIPlayer.INIT_HP, gameMap.getSpawn(i), BasePlayer.Indirect.DOWN, "enemy1", this));
		started = true;
	}

	public static void main(String[] args)
	{
		new Game();
	}
}
