package main;

import static DEBUG.Dbg.*;

import render.MainRenderer;
import thread.GameKeyListener;
import thread.MusicPlayer;
import BaseObject.*;
import BaseObject.Flow;
import BaseObject.GameMap.FailureReadMapException;
import BasePlayer.*;

import java.util.*;
import java.util.concurrent.*;;

public class Game
{
	public static final int PVP = 0, PVE = 1;

	public Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
	private int mode;
	private GameMap gameMap = null;
	private Set<BasePlayer> players = new TreeSet<>();
	private List<AIPlayer> aiPlayers = new LinkedList<>();
	private HumanPlayer infoPlayer = null;
	private List<Bomb> bombs = new LinkedList<>();
	private List<Flow> flows = new LinkedList<>();
	private boolean soundOn = true;
	private boolean started = false;
	private MainRenderer renderer = new MainRenderer(this);
	private GameKeyListener gameKeyListener = new GameKeyListener(this);
	private MusicPlayer musicPlayer = new MusicPlayer();

	public int getMode() {return mode;}
	public GameMap getMap() {return this.gameMap;}
	public Set<BasePlayer> getPlayers() {return this.players;}
	public List<AIPlayer> getAIPlayers() {return this.aiPlayers;}
	public HumanPlayer getInfoPlayer() {return this.infoPlayer;}
	public List<Bomb> getBombs() {return this.bombs;}
	public List<Flow> getFlows() {return this.flows;}
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

		if (!started) 
			return;

		// 人物及每个AI决定移动
		infoPlayer.move();
		for (AIPlayer aiPlayer: aiPlayers)
			if (mode == PVP)
			{
				aiPlayer.decideMove();
				aiPlayer.move();
				boolean placeBomb = aiPlayer.decidePlaceBomb();
				if (placeBomb) aiPlayer.placeBomb();
			}
			else if (mode == PVE)
			{
				aiPlayer.decideCharge();
				aiPlayer.move();
			}

		// 为Flow做倒计时
		Iterator<Flow> iterFlow = flows.iterator();
		while (iterFlow.hasNext())
		{
			Flow flow = iterFlow.next();
			if (flow.countDown())
			{
				// 把flow从gameMap里去掉
				flow.crash();
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

		// 计算玩家收到伤害
		for (BasePlayer player: players)
		{
			Coordinate loc = player.getGridLoc();
			BaseObject obj = gameMap.get(loc);
			if (obj != null)
			{
				String name = obj.getName();
				if (name == "crossflow" || name == "vertflow" || name == "horiflow")
					player.getHurt(((Flow)obj).getAtk());
			}
		}
		if (mode == PVE)
			for (AIPlayer player : aiPlayers)
				if (infoPlayer.contacts(player))
					infoPlayer.getHurt(player.getAtk());

		// 移除生命值归零的玩家
		Iterator<BasePlayer> iterPlayer = players.iterator();
		while (iterPlayer.hasNext())
		{
			BasePlayer player = iterPlayer.next();
			if (!player.isAlive())
			{
				if (player instanceof AIPlayer) aiPlayers.remove(player);
				else infoPlayer = null;
				iterPlayer.remove();
			}
		}

		if (infoPlayer == null) commandQueue.add(() -> {
			end(); renderer.getGameScene().failAnimation();
			renderer.getGameScene().setVisible(false);
			renderer.getTitleScene().setVisible(true);
		});
		if (aiPlayers.size() == 0) commandQueue.add(() -> {
			end(); renderer.getGameScene().successAnimation();
			renderer.getGameScene().setVisible(false);
			renderer.getTitleScene().setVisible(true);
		});
		// 更新绘图
		renderer.updateRender();
	}

	public void end()
	{
		renderer.removeKeyListener(gameKeyListener);
		gameMap = null;
		infoPlayer = null;
		aiPlayers.clear();
		players.clear();
		gameKeyListener.clearPlayer();
		bombs.clear();
		flows.clear();
		commandQueue.clear();
		musicPlayer.stops();
		
		started = false;
	}
	public void start(String selChar, String selScene, int mode)	// 选择的人物，选择的场景，选择的游戏模式
	{
		int modeBias = mode == PVP ? 1 : 3;
		this.mode = mode;
		try {
			gameMap = new GameMap(selScene, (int)(Math.random() * 2) + modeBias);
		} catch (FailureReadMapException e) {
			e.printStackTrace();
			System.exit(0);
		}
		infoPlayer = new HumanPlayer(this, selChar, gameMap.getSpawn(0));
		players.add(infoPlayer);
		for (int i = 1; i < 4; ++i)
		{
			AIPlayer aiPlayer = new AIPlayer(this, "enemy1", gameMap.getSpawn(i));
			aiPlayers.add(aiPlayer);
			players.add(aiPlayer);
		}
		gameKeyListener.setPlayer(infoPlayer);
		renderer.addKeyListener(gameKeyListener);
		
		if (musicPlayer.getState() == Thread.State.NEW) 	// 如果多次start，不会重新播放音乐
			musicPlayer.start();
		else musicPlayer.continues();

		started = true;
	}

	public static void main(String[] args)
	{
		Game g = new Game();
		while (true) g.mainloop();
	}
}
