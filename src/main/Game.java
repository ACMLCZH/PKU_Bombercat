package main;

import render.MainRenderer;
import BaseObject.Bomb;
import BaseObject.GameMap;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;

import java.util.Collections;
// import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Set;

public class Game			// 我先写一点，测试用，你看着改
{
	public static final int PVP = 0, PVE = 1;

	public ArrayBlockingQueue<Runnable> commandQueue = new ArrayBlockingQueue<>(32768);		// 实现一个线程安全队列？
	private GameMap gameMap = null;
	private Set<BasePlayer> players = Collections.synchronizedSet(new TreeSet<>());		// 你看看有没有更好的实现线程安全的方法
	private Set<Bomb> bombs = Collections.synchronizedSet(new TreeSet<>());
	private HumanPlayer infoPlayer = null;
	private boolean soundOn = true;

	public Set<BasePlayer> getPlayers() {return this.players;}
	public HumanPlayer getInfoPlayer() {return this.infoPlayer;}
	public Set<Bomb> getBombs() {return this.bombs;}
	public GameMap getMap() {return this.gameMap;}
	public boolean isSoundOn() {return this.soundOn;}

	public void switchSound() {this.soundOn = !this.soundOn;}//

    public Game()
	{
		new MainRenderer(this);
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