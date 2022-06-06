package BasePlayer;

import java.lang.annotation.Target;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import BaseObject.Coordinate;
import BaseObject.GameMap;
import BaseObject.BaseObject;
import BaseObject.Bomb;
import main.Game;

public class AIPlayer extends BasePlayer
{
    public static final int INIT_HP = 8000;
    private static final int[][] directs = {{0, -1},{0, 1},{-1, 0},{1, 0}}; //姑且这样存着
    private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    // private Indirect lastDir;
    private int maxSaveDir = 1;          // 保存路径的最大数，即移动步数过多后要重算
    private Queue<Indirect> saveDir;     // 简单保存确定的一系列行动
    private long stopTime;               // 控制停止的时间
    private boolean stopFlag = false;    // 标记目前的停止状态是否是必要的
    private Coordinate curTarget;        // 当前移动的目标地点
    private Coordinate curBombPlaceLoc;
    private Random randChoice;
    // private int atk;

    public AIPlayer(Game game, String name, int gameMode, Coordinate spawn, int atk)
	{
        super(
			game, name, gameMode, INIT_HP, spawn, atk,
			gameMode == Game.PVP ? 1 : 0,
			gameMode == Game.PVP ? 1 : 0
		);
		// this.atk = atk;
        // lastDir = dir;
        stopTime = System.currentTimeMillis(); // 初始时默认停一下
        curTarget = spawn;
        saveDir = new LinkedList<>();
        randChoice = new Random();
    }

		// this.atk = atk;
        // lastDir = dir;
    //     stopTime = System.currentTimeMillis(); // 初始时默认停一下
    //     curTarget = spawn;
    //     saveDir = new LinkedList<>();
    //     randChoice = new Random();
    // }

    // void mainloop() {}
    public boolean inGridCenter(Coordinate tarGrid) {
        return p1.x == tarGrid.x*BasePlayer.pixelsPerBlock && p1.y == tarGrid.y * BasePlayer.pixelsPerBlock;
    }

    public void decideMove()
	{
        // 只有在时间间隔满足的时候才能move（这个应该不归他管）
        // if (System.currentTimeMillis() - lastMove < BasePlayer.periodPerMove)
		// 	return; 	// Indirect.STOP;
        // 计算中心所在的格点
        GameMap mp = this.game.getMap();
        Coordinate curLoc = getGridLoc();
		// 先处理停止状况，目前stopTime的操作只在这个函数里进行
        if(!this.isMoving) {
            long current = System.currentTimeMillis();
            if(current - stopTime < 1000) return;
        }
        // 这个是判断是否走到目标格子中心点的，
        if(!inGridCenter(curTarget))
            return;
        // 每次会完成保存的路径
        if(!saveDir.isEmpty())
		{
			this.dir = saveDir.poll();
            curTarget = nextLocation(curLoc, this.dir);
			this.isMoving = true;		// savedir里面只有上下左右
			return;
        }
            // lastDir = saveDir.poll();
            // return lastDir;
        // if (lastDir == Indirect.STOP) {
        //     long current = System.currentTimeMillis();
        //     if (current - stopTime < 1000) return lastDir;
        // }
        // decideMoveRandom(); return;
        // 宽搜
        int[][] smp = new int[GameMap.HEIGHT][];
        int[][] lastMp = new int[GameMap.HEIGHT][];
        boolean haveBombFlag = false;
        for(int i = 0; i < GameMap.HEIGHT; ++i)
		{
            smp[i] = new int[GameMap.WIDTH]; 
            lastMp[i] = new int[GameMap.WIDTH];
        }
        Queue<Coordinate> queue = new LinkedList<Coordinate>();
        queue.add(curLoc);
        smp[curLoc.y][curLoc.x] = 1;
        while(!queue.isEmpty()) {
            Coordinate cur = queue.poll(), nextGrid;
            for(int k = 0; k < 4; ++k) {
                nextGrid = new Coordinate(cur.x+directs[k][0], cur.y+directs[k][1]);
                if (nextGrid.x < 0 || nextGrid.x >= GameMap.WIDTH || nextGrid.y < 0 || nextGrid.y >= GameMap.HEIGHT) continue;
                BaseObject nextObject = mp.get(nextGrid);
                if (nextObject != null && (nextObject.getName() == "bomb" || nextObject.toString().contains("flow"))) {
                    smp[nextGrid.y][nextGrid.x] = -1;  haveBombFlag = true;
                    continue;
                }
                if (smp[nextGrid.y][nextGrid.x] != 0 ||(nextObject!=null && !nextObject.getIsPassable())) continue;
                queue.add(nextGrid);
                smp[nextGrid.y][nextGrid.x] = smp[cur.y][cur.x]+1;
                lastMp[nextGrid.y][nextGrid.x] = k;
            }
        }
        // 首先躲避炸弹
        if(mp.get(curLoc) != null && mp.get(curLoc).getName() == "bomb") {
            haveBombFlag = true; 
            smp[curLoc.y][curLoc.x] = -1; //即在这个位置刚放了一个炸弹
        }
        if(haveBombFlag) {
            int[][] newMp = removeBombRange(smp, mp);
            int minSkip = 100000;
            Coordinate nearestP = curLoc;
            for(int i = 0; i < GameMap.HEIGHT; ++i) 
                for(int j = 0; j < GameMap.WIDTH; ++j) {
                    if(newMp[i][j] > 1) {
                        if(newMp[i][j] < minSkip) {
                            minSkip = newMp[i][j];
                            nearestP = new Coordinate(j, i);
                        }
                        else if(newMp[i][j] == minSkip && randChoice.nextInt(10)<5) {
                            nearestP = new Coordinate(j, i);
                        }
                    }
                }
            if(newMp[curLoc.y][curLoc.x] == -1)
            { 	// 在炸弹范围内
                findWay(curLoc, nearestP, lastMp);
                return;
            }
            else if (minSkip > 2)
            { 	//走一步的地方都被锁定，等炸弹
                this.isMoving = false; //ndirect.STOP;
                stopTime = System.currentTimeMillis();
                return ;
                // return lastDir;
            }
            else 
            {
                
            }
        }
        // 下面是向玩家前行的路
        Coordinate humanP = this.game.getInfoPlayer().getGridLoc();
        if(smp[humanP.y][humanP.x] != 0)
		{
            findWay(curLoc, humanP, lastMp);
            return;
        }
        // 最后寻找一个可以放炸弹的地方
        //if(curBombPlaceLoc == null) {
            curBombPlaceLoc = findBombPlace(smp, mp); //这是必要的更新
            findWay(curLoc, curBombPlaceLoc, lastMp);
        //}
        //else
		//	findWay(curLoc, curBombPlaceLoc, lastMp);
    }

    public boolean decidePlaceBomb()
	{
        // 如果有炸弹数量的限制在这里加判断

        Coordinate curLoc = getGridLoc();
        // 如果当前位置已经有炸弹了, 不放炸弹
        if (game.getMap().get(curLoc) != null)
            return false;
        Coordinate humanLoc = this.game.getInfoPlayer().getGridLoc();
        if(Math.abs(humanLoc.x- curLoc.x) + Math.abs(humanLoc.y-curLoc.y) <= 1) {
            return true; // 在玩家身边放炸弹
        }
        if(curBombPlaceLoc != null && inGridCenter(curBombPlaceLoc)) {
            curBombPlaceLoc = null;
            return true;
        }
        return false;
    }

    public void decideMoveRandom()
	{
        int k = randChoice.nextInt(4);
        switch(k)
		{
            case UP:    this.dir = Indirect.UP; break;
            case DOWN:  this.dir = Indirect.DOWN; break;
            case LEFT:  this.dir = Indirect.LEFT; break;
            case RIGHT: this.dir = Indirect.RIGHT; break;
            default: this.isMoving = false; return;
        }
        curTarget = nextLocation(getGridLoc(), this.dir);
		this.isMoving = true;
    }

    // 用于划出炸弹爆炸的范围，用来躲避炸弹
    private int[][] removeBombRange(int[][] smp, GameMap mp)
	{
        int[][] newMp = new int[GameMap.HEIGHT][];
        for(int i = 0; i < GameMap.HEIGHT; ++i) newMp[i] = new int[GameMap.WIDTH];
        for(int i = 0; i < GameMap.HEIGHT; ++i)
		{
            for(int j=0; j<GameMap.WIDTH; ++j)
			{
                if(newMp[i][j] == -1 && smp[i][j] != -1) continue;
                newMp[i][j] = smp[i][j];
                if(smp[i][j] == 0) continue;
                if(smp[i][j] == -1 && mp.get(j,i).getName() == "bomb") {
                    int t = ((Bomb) mp.get(j, i)).getBombRange();
                    for(int x = j - t; x <= j + t; ++x) {
                        if (x < 0 || x >= GameMap.WIDTH) continue;
                        if (smp[i][x] <= 0) continue;
                        newMp[i][x] = -1;
                    }
                    for(int y = i - t; y <= i + t; ++y) {
                        if(y < 0 || y >= GameMap.HEIGHT) continue;
                        if(smp[y][j] <= 0) continue;
                        newMp[y][j] = -1;
                    }
                }
            }
        }
        return newMp;
    }
    // 用于生成一条从start到end的一条最短路，存在SaveDir中，之后一定步数内不再计算新路径
    private void findWay(Coordinate start, Coordinate end, int[][] lastMp)
	{
        LinkedList<Indirect> SaveList = new LinkedList<>();
        BaseObject endObj = this.game.getMap().get(end);
        if (endObj != null && endObj.getName() == "bomb") 
        {  //冷静一下再去放炸弹...
            stopTime = System.currentTimeMillis() - 500;
            this.isMoving = false;
            return ;
        }
        if (end.equals(start))
		{
            stopTime = System.currentTimeMillis();
            this.isMoving = false;
			return;
        }
        Coordinate tar = new Coordinate(end);
        for(; !tar.equals(start); ) {
            int k = lastMp[tar.y][tar.x];
            tar.x = tar.x - directs[k][0];
            tar.y = tar.y - directs[k][1];
            switch (k) {
                case UP: SaveList.addFirst(Indirect.UP); break;
                case DOWN: SaveList.addFirst(Indirect.DOWN); break;
                case LEFT: SaveList.addFirst(Indirect.LEFT); break;
                case RIGHT: SaveList.addFirst(Indirect.RIGHT); break;
                default: 
            }
        }
        for(int i=0; i<maxSaveDir && i<SaveList.size(); ++i) {
            saveDir.add(SaveList.get(i));
        }
		this.dir = saveDir.poll();
        curTarget = nextLocation(start, this.dir);
		this.isMoving = true;
    }
    // 用于前期寻找能放炸弹的地方的函数，这个借用了宽搜的结果
    private Coordinate findBombPlace(int[][] smp, GameMap mp)
	{
        int maxBreakable = -1;
        Coordinate maxBreakableLoc = null;
        for (int y=0; y<GameMap.HEIGHT; ++y) {
            for (int x=0; x<GameMap.WIDTH; ++x) {
                if (smp[y][x] <= 0) continue;
                int tmp = 0;
                for(int k = 0; k < 4; ++k) {
                    int xNear = x + directs[k][0];
                    int yNear = y + directs[k][1];
                    if(xNear < 0 || xNear>=GameMap.WIDTH || yNear < 0 || yNear >= GameMap.HEIGHT) continue;
                    if(mp.get(xNear, yNear) == null) continue;
                    tmp += (mp.get(xNear, yNear).getName() == "destroyable") ? 1 : 0;
                }
                if(tmp > maxBreakable) {
                    maxBreakable = tmp;
                    maxBreakableLoc = new Coordinate(x, y);
                }
                else if(tmp == maxBreakable && randChoice.nextInt(10) < 5) {
                    maxBreakableLoc = new Coordinate(x,y);
                }
            }
        }
        return maxBreakableLoc;
    }
    private Coordinate nextLocation(Coordinate start, Indirect chg) {
        int x = start.x, y = start.y;
        switch(chg) {
            case UP:    y -= 1; break;
            case DOWN:  y += 1; break;
            case LEFT:  x -= 1; break;
            case RIGHT: x += 1; break;
            default:
        }
        return new Coordinate(x, y);
    }
}
