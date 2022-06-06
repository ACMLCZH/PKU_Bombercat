package main;

import static DEBUG.Dbg.*;

import render.MainRenderer;
import thread.GameKeyListener;
import BaseObject.*;
import BaseObject.Flow;
import BaseObject.GameMap.FailureReadMapException;
import BasePlayer.*;
import BasePlayer.Indirect;

import java.util.*;
import java.util.concurrent.*;;

public class Game
{
	public static final int PVP = 0, PVE = 1;

	public int mode;
	public Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
	private GameMap gameMap = null;
	private Set<BasePlayer> players = new TreeSet<>();
	private Set<AIPlayer> aiPlayers = new TreeSet<>();
	private HumanPlayer infoPlayer = null;
	private ArrayList<Bomb> bombs = new ArrayList<>();
	private ArrayList<Flow> flows = new ArrayList<>();
	private ArrayList<Barrier> barriers = new ArrayList<>();
	private boolean soundOn = true;
	private boolean started = false;
	private MainRenderer renderer = new MainRenderer(this);
	private GameKeyListener gameKeyListener = new GameKeyListener(this);

	public GameMap getMap() {return this.gameMap;}
	public Set<BasePlayer> getPlayers() {return this.players;}
	public Set<AIPlayer> getAIPlayers() {return this.aiPlayers;}
	public HumanPlayer getInfoPlayer() {return this.infoPlayer;}
	public ArrayList<Bomb> getBombs() {return this.bombs;}
	public ArrayList<Flow> getFlows() {return this.flows;}
	public ArrayList<Barrier> getBarriers() {return this.barriers;};
	public boolean isSoundOn() {return this.soundOn;}
	public boolean isStarted() {return this.started;}
	public void switchSound() {this.soundOn = !this.soundOn;}

    private void mainloop()
	{
		// 清空任务队列, 执行每个任务
		while (true)
		{
			Runnable cmd = commandQueue.poll();
			if (cmd != null) cmd.run(); else break;
		}

		if (!started) return;

		// 人物及每个AI决定移动
		infoPlayer.move();
		for (AIPlayer aiPlayer: aiPlayers)
		{
			aiPlayer.decideMove();
			aiPlayer.move();
			boolean placeBomb = aiPlayer.decidePlaceBomb();
			if (placeBomb) aiPlayer.placeBomb();
		}

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
					player.getHurt(((Flow)obj).getAtk());
			}
		}

		renderer.updateRender();
		renderer.getGameScene().repaint();
	}
	// public Game() {}// renderer = }

	public void end()
	{
		renderer.removeKeyListener(gameKeyListener);
		gameMap = null;
		infoPlayer = null;
		aiPlayers.clear();
		gameKeyListener.clearPlayer();
		players.clear();
		bombs.clear();
		barriers.clear();
		flows.clear();
		
		started = false;
	}
	public void start(String selChar, String selScene, int mode)	// 选择的人物，选择的场景，选择的游戏模式
	{
		int modeBias = mode == PVP ? 1 : 3;
		try {
			gameMap = new GameMap(selScene, (int)(Math.random() * 2) + modeBias);
		} catch (FailureReadMapException e) {
			e.printStackTrace();
			System.exit(0);
		}
		infoPlayer = new HumanPlayer(this, selChar, HumanPlayer.INIT_HP, gameMap.getSpawn(0), Indirect.DOWN);
		players.add(infoPlayer);
		for (int i = 1; i < 4; ++i)
		{
			AIPlayer aiPlayer = new AIPlayer(this, "enemy1", AIPlayer.INIT_HP, gameMap.getSpawn(i), Indirect.DOWN, (int)(Math.random() * 1000) + 500);
			aiPlayers.add(aiPlayer);
			players.add(aiPlayer);
		}
		for (int i = 0; i < GameMap.HEIGHT; ++i)
			for (int j = 0; j < GameMap.WIDTH; ++j)
			{
				BaseObject obj = gameMap.get(j, i);
				if (obj != null) barriers.add((Barrier)obj);
			}
		gameKeyListener.setPlayer(infoPlayer);
		renderer.addKeyListener(gameKeyListener);

		started = true;
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		while (true) g.mainloop();
	}
}
