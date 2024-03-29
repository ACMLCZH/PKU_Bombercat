package BasePlayer;

import BaseObject.Coordinate;
import BaseObject.GameMap;
import BaseObject.BaseObject;
import BaseObject.Bomb;
import main.Game;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class AIPlayer extends BasePlayer
{
    public static final int[] INIT_HP = new int[] {700, 2000, 8000};
    private static final int[][] directs = {{0, -1},{0, 1},{-1, 0},{1, 0}}; // 姑且这样存着
    private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    private int maxSaveDir = 1;          // 保存路径的最大数，即移动步数过多后要重算
	private int def;
    private Queue<Indirect> saveDir;     // 简单保存确定的一系列行动
    private long stopTime;               // 控制停止的时间
    private Coordinate curTarget;        // 当前移动的目标地点
    private Coordinate curBombPlaceLoc;
    private Random randChoice;

    public AIPlayer(Game game, String name, Coordinate spawn)
	{
        super(
			game, name,
			game.getMode() == Game.PVP ? 4000 : INIT_HP[(int)(Math.random() * 3)],
			spawn,
			game.getMode() == Game.PVP ? ((int)(Math.random() * 1000) + 500) : 400,
			game.getMode() == Game.PVP ? 1 : 0,
			game.getMode() == Game.PVP ? 1 : 0,
			game.getMode() == Game.PVP ? 4.0 : 2.5
		);
        this.stopTime = System.currentTimeMillis(); // 初始时默认停一下
        this.curTarget = spawn;
        this.saveDir = new LinkedList<>();
        this.randChoice = new Random();
		if (game.getMode() == Game.PVP)
			this.def = 0;
		else
			switch (this.HP)
			{
				case 700: this.def = (int)(Math.random() * 500) + 2000; break;
				case 2000: this.def = (int)(Math.random() * 1000) + 1000; break;
				case 8000: this.def = (int)(Math.random() * 1000); break;
			}
	}

    public boolean inGridCenter(Coordinate tarGrid) {
        return p1.x == tarGrid.x * BasePlayer.pixelsPerBlock && p1.y == tarGrid.y * BasePlayer.pixelsPerBlock;
    }
	public int getAtk() {return this.atk;}
	
	@Override
	public void getHurt(int dmg) {if (dmg > def) super.getHurt(dmg - def);}

	public void decideCharge()
	{
		GameMap mp = this.game.getMap();
		Coordinate[] locs = new Coordinate[] {
			p1.toGrid(), new Coordinate(p1.x, p2.y).toGrid(),
			new Coordinate(p2.x, p1.y).toGrid(), p2.toGrid()
		};
        Queue<Coordinate> queue = new LinkedList<Coordinate>();
		int[][] smp = new int[GameMap.HEIGHT][GameMap.WIDTH];
		Indirect[][] lastMp = new Indirect[GameMap.HEIGHT][GameMap.WIDTH];
		for (int i = 0; i < GameMap.HEIGHT; ++i)
			for (int j = 0; j < GameMap.WIDTH; ++j)
				smp[i][j] = 0;
		for (int i = 0; i < 4; ++i)
			if (mp.get(locs[i]) == null || mp.get(locs[i]).getIsPassable())
			{
				smp[locs[i].y][locs[i].x] = 1;
				queue.add(locs[i]);
			}
		while (!queue.isEmpty())
		{
			Coordinate cur = queue.poll();
			for (Indirect curD : Indirect.values())
			{
				Coordinate nex = new Coordinate(cur);
				nex.step(curD);
                if (nex.x < 0 || nex.x >= GameMap.WIDTH || nex.y < 0 || nex.y >= GameMap.HEIGHT) continue;
				if (mp.get(nex) == null || mp.get(nex).getIsPassable())
				{
					if (smp[nex.y][nex.x] == 0)
					{
						smp[nex.y][nex.x] = smp[cur.y][cur.x] + 1;
						lastMp[nex.y][nex.x] = curD;
						queue.add(nex);
					}
					else if (smp[nex.y][nex.x] == smp[cur.y][cur.x] + 1)
						if (Math.random() < 0.5) lastMp[nex.y][nex.x] = curD;
				}
			}
		}
		Coordinate humanP = this.game.getInfoPlayer().getGridLoc();
		if (smp[humanP.y][humanP.x] == 0) {this.isMoving = false; return;}
		this.isMoving = true;
		for (;smp[humanP.y][humanP.x] != 1; humanP.step(lastMp[humanP.y][humanP.x], -1))
			this.dir = lastMp[humanP.y][humanP.x];
		if (inGridCenter(humanP)) return;
		humanP = humanP.toPixel();
		if (p1.y < humanP.y) this.dir = Indirect.DOWN;
		else if (p1.y > humanP.y) this.dir = Indirect.UP;
		else
			if (p1.x < humanP.x) this.dir = Indirect.RIGHT;
			else this.dir = Indirect.LEFT;
	}

    public void decideMove()
	{
        // 只有在时间间隔满足的时候才能move（这个应该不归他管）
        if (!this.isMoving)		// 先处理停止状况，目前stopTime的操作只在这个函数里进行
		{
            long current = System.currentTimeMillis();
            if (current - stopTime < 1000) return;
        }
        if (!inGridCenter(curTarget)) return;	// 这个是判断是否走到目标格子中心点的，

        GameMap mp = this.game.getMap();
        Coordinate curLoc = getGridLoc();		// 计算中心所在的格点
        
        if (!saveDir.isEmpty())					// 每次会完成保存的路径
		{
			this.dir = saveDir.poll();
			curTarget = new Coordinate(curLoc);
			curTarget.step(this.dir, 1);
			this.isMoving = true;				// savedir里面只有上下左右
			return;
        }

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
            smp[curLoc.y][curLoc.x] = -1; 	//即在这个位置刚放了一个炸弹
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
        }
        // 下面是向玩家前行的路
        Coordinate humanP = this.game.getInfoPlayer().getGridLoc();
        if(smp[humanP.y][humanP.x] != 0)
		{
            findWay(curLoc, humanP, lastMp);
            return;
        }
        // 最后寻找一个可以放炸弹的地方
		curBombPlaceLoc = findBombPlace(smp, mp); //这是必要的更新
		findWay(curLoc, curBombPlaceLoc, lastMp);
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
        switch (k)
		{
            case UP:    this.dir = Indirect.UP; break;
            case DOWN:  this.dir = Indirect.DOWN; break;
            case LEFT:  this.dir = Indirect.LEFT; break;
            case RIGHT: this.dir = Indirect.RIGHT; break;
            default: this.isMoving = false; return;
        }
		curTarget = getGridLoc();
		curTarget.step(this.dir, 1);
        // curTarget = nextLocation(getGridLoc(), this.dir);
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
                if(smp[i][j] == -1 && (mp.get(j,i).getName() == "bomb")) {
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
        if (endObj != null && (endObj.getName() == "bomb" || endObj.toString().contains("flow")))
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
        for(int i = 0; i < maxSaveDir && i < SaveList.size(); ++i) {
            saveDir.add(SaveList.get(i));
        }
		this.dir = saveDir.poll();
		curTarget = new Coordinate(start);
		curTarget.step(this.dir, 1);
        // curTarget = nextLocation(start, this.dir);
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
    // private Coordinate nextLocation(Coordinate start, Indirect chg) {
    //     int x = start.x, y = start.y;
    //     switch(chg) {
    //         case UP:    y -= 1; break;
    //         case DOWN:  y += 1; break;
    //         case LEFT:  x -= 1; break;
    //         case RIGHT: x += 1; break;
    //         default:
    //     }
    //     return new Coordinate(x, y);
    // }
}
