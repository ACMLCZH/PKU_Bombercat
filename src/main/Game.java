package main;

import render.MainRenderer;
import BaseObject.Bomb;
import BaseObject.GameMap;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;

import java.util.*;
import java.util.concurrent.*;;

public class Game
{
	public static final int PVP = 0, PVE = 1;

	public int mode;
	public Queue<Runnable> commandQueue = new ConcurrentLinkedQueue<>();
	private GameMap gameMap;
	private List<BasePlayer> players = new ArrayList<>();
	private List<Bomb> bombs = new LinkedList<>();
	private HumanPlayer infoPlayer = null;
	private boolean soundOn = true;
	private MainRenderer renderer;

	public List<BasePlayer> getPlayers() {return this.players;}
	public HumanPlayer getInfoPlayer() {return this.infoPlayer;}
	public List<Bomb> getBombs() {return this.bombs;}
	public GameMap getMap() {return this.gameMap;}
	public boolean isSoundOn() {return this.soundOn;}

	public void switchSound() {this.soundOn = !this.soundOn;}//

    private void mainloop()
	{
		// 清空任务队列, 执行每个任务
		while (true)
		{
			Runnable cmd = commandQueue.poll();
			if (cmd != null)
				cmd.run();
			else
				break;
		}

		// 为炸弹做倒计时
		for (Bomb bomb: bombs)
		{
			bomb.countDown();
			if (bomb.getState() == 0)
			{
				bomb.explode(gameMap);
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

	}

	public static void main(String[] args)
	{
		new Game();
	}
}