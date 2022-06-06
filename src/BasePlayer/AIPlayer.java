package BasePlayer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import BaseObject.Coordinate;
import BaseObject.GameMap;
import BaseObject.Bomb;
import main.Game;

public class AIPlayer extends BasePlayer
{
    public static final int INIT_HP = 8000;
    private static final int[][] directs = {{0, -1},{0, 1},{-1, 0},{1, 0}}; //姑且这样存着
    private static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    private Indirect lastDir;
    private int maxSaveDir = 3;          // 保存路径的最大数，即移动步数过多后要重算
    private Queue<Indirect> saveDir;     // 简单保存确定的一系列行动
    private long stopTime;           // 控制停止的时间
    private Coordinate curBombPlaceLoc;
    private Random randChoice;
    private int atk;

    public AIPlayer(Game game, String name, int HP, Coordinate spawn, Indirect dir, int atk)
	{
        super(game, name, HP, spawn, dir, atk);
		this.atk = atk;
        lastDir = Indirect.STOP;
        stopTime = System.currentTimeMillis(); //初始时默认停一下
        randChoice = new Random();
    }

    void mainloop() {
        
    }

    public Indirect decideMove()
	{
        // 只有在时间间隔满足的时候才能move
        if (System.currentTimeMillis() - lastMove < BasePlayer.periodPerMove)
			return Indirect.STOP;
        GameMap mp = this.game.getMap();
        // 计算中心所在的格点
        Coordinate curLoc = getGridLoc();
        // 不在中心时沿用前一步的移动
        if((p1.x+p2.x)/2 != curLoc.x*BasePlayer.pixelsPerBlock || (p1.y+p2.y)/2 != curLoc.y*pixelsPerBlock) {
            return lastDir;
        }
        // 每次会完成保存的路径
        if(!saveDir.isEmpty()) {
            lastDir = saveDir.poll();
            return lastDir;
        }
        if(lastDir == Indirect.STOP) {
            long current = System.currentTimeMillis();
            if(current - stopTime < 1000) return lastDir;
        }
        // return decideMoveRandom(); 
        // 宽搜
        int[][] smp = new int[GameMap.HEIGHT][];
        int[][] lastMp = new int[GameMap.HEIGHT][];
        for(int i=0; i<GameMap.HEIGHT; ++i) {
            smp[i] = new int[GameMap.WIDTH]; 
            lastMp[i] = new int[GameMap.WIDTH];
        }
        Queue<Coordinate> queue = new LinkedList<Coordinate>();
        queue.add(curLoc);
        smp[curLoc.y][curLoc.x] = 1;
        while(!queue.isEmpty()) {
            Coordinate cur = queue.poll(), nextGrid;
            for(int k=0; k<4; ++k) {
                nextGrid = new Coordinate(cur.x+directs[k][0], cur.y+directs[k][1]);
                if(nextGrid.x < 0 || nextGrid.x >= GameMap.WIDTH || nextGrid.y < 0 || nextGrid.y >= GameMap.HEIGHT) continue;
                if(mp.get(nextGrid).getName() == "bomb") smp[nextGrid.y][nextGrid.x] = -1;
                if(smp[nextGrid.y][nextGrid.x] != 0 || !mp.get(nextGrid).getIsPassable()) continue;
                queue.add(nextGrid);
                smp[nextGrid.y][nextGrid.x] = smp[cur.y][cur.x]+1;
                lastMp[nextGrid.y][nextGrid.x] = k;
            }
        }
        // 首先躲避炸弹
        int[][] newMp = removeBombRange(smp, mp);
        int minSkip = 100000;
        Coordinate nearestP = curLoc;
        for(int i=0; i<GameMap.HEIGHT; ++i) 
            for(int j=0; j<GameMap.WIDTH; ++j) {
                if(newMp[i][j] > 0) {
                    if(newMp[i][j] < minSkip) {
                        minSkip = newMp[i][j];
                        nearestP = new Coordinate(j, i);
                    }
                }
            }
        if(newMp[curLoc.y][curLoc.x] == -1) { // 在炸弹范围内
            lastDir = findWay(curLoc, nearestP, lastMp);
            return lastDir;
        }
        else if(minSkip > 1) { //走一步的地方都被锁定，等炸弹
            lastDir = Indirect.STOP;
            stopTime = System.currentTimeMillis();
            return lastDir;
        }
        // 下面是向玩家前行的路
        Coordinate humanP = this.game.getInfoPlayer().getGridLoc();
        if(smp[humanP.y][humanP.x] != 0) {
            lastDir = findWay(curLoc, humanP, lastMp);
            return lastDir;
        }
        // 最后寻找一个可以放炸弹的地方
        if(curBombPlaceLoc == null) {
            Coordinate t = findBombPlace(smp, mp);
            lastDir = findWay(curLoc, t, lastMp);
        }
        else {
            lastDir = findWay(curLoc, curBombPlaceLoc, lastMp);
        }
        return lastDir;
    }

    public boolean decidePlaceBomb() {
        // 如果有炸弹数量的限制在这里加判断

        Coordinate curLoc = getGridLoc();
        // 如果当前位置已经有炸弹了, 不放炸弹
        if (game.getMap().get(curLoc) != null)
            return false;
        Coordinate humanLoc = this.game.getInfoPlayer().getGridLoc();
        if(Math.abs(humanLoc.x- curLoc.x) + Math.abs(humanLoc.y-curLoc.y) <= 1) {
            return true; // 在玩家身边放炸弹
        }
        if(curBombPlaceLoc != null && curLoc == curBombPlaceLoc) {
            curBombPlaceLoc = null;
            return true;
        }
        return false;
    }

    public Indirect decideMoveRandom() {
        int k = randChoice.nextInt(4);
        switch(k) {
            case UP:    return Indirect.UP;
            case DOWN:  return Indirect.DOWN;
            case LEFT:  return Indirect.LEFT;
            case RIGHT: return Indirect.RIGHT;
            default:
        }
        return Indirect.STOP;
    }

    // 用于划出炸弹爆炸的范围，用来躲避炸弹
    private int[][] removeBombRange(int[][] smp, GameMap mp) {
        int[][] newMp = new int[GameMap.HEIGHT][];
        for(int i=0; i<GameMap.HEIGHT; ++i) newMp[i] = new int[GameMap.WIDTH];
        for(int i=0; i<GameMap.HEIGHT; ++i) {
            for(int j=0; j<GameMap.WIDTH; ++j) {
                newMp[i][j] = smp[i][j];
                if(smp[i][j] == 0) continue;
                if(smp[i][j] == -1) {
                    int t = ((Bomb) mp.get(j, i)).getBombRange();
                    for(int x=j-t; x<=j+t; ++x) {
                        if (x < 0 || x >= GameMap.WIDTH) continue;
                        if (smp[i][x] <= 0) continue;
                        newMp[i][x] = -1;
                    }
                    for(int y=i-t; y<=i+t;++y) {
                        if(y<0 || y>=GameMap.HEIGHT) continue;
                        if(smp[y][j] <= 0) continue;
                        newMp[y][j] = -1;
                    }
                }
            }
        }
        return newMp;
    }
    // 用于生成一条从start到end的一条最短路，存在SaveDir中，之后一定步数内不再计算新路径
    private Indirect findWay(Coordinate start, Coordinate end, int[][] lastMp) {
        LinkedList<Indirect> SaveList = new LinkedList<>();
        if(end.equals(start)) {
            stopTime = System.currentTimeMillis();
            return Indirect.STOP;
        }
        int dircnt = 0;
        for(; end.equals(start); ) {
            int k = lastMp[end.y][end.x];
            end.x = end.x - directs[k][0];
            end.y = end.y - directs[k][1];
            switch(k) {
                case UP:    SaveList.addFirst(Indirect.UP); break;
                case DOWN:  SaveList.addFirst(Indirect.DOWN); break;
                case LEFT:  SaveList.addFirst(Indirect.LEFT); break;
                case RIGHT: SaveList.addFirst(Indirect.RIGHT); break;
                default: 
            }
            dircnt += 1;
            if(dircnt == maxSaveDir) break;
        }
        saveDir = SaveList;
        return saveDir.poll();
    }
    // 用于前期寻找能放炸弹的地方的函数，这个借用了宽搜的结果
    private Coordinate findBombPlace(int[][] smp, GameMap mp)
	{
        int maxBreakable = -1;
        Coordinate maxBreakableLoc = null;
        for(int y=0; y<GameMap.HEIGHT; ++y) {
            for(int x=0; x<GameMap.WIDTH; ++x) {
                if(smp[y][x] <= 0) continue;
                int tmp = 0;
                for(int k=0; k<4; ++k) {
                    int xNear = x + directs[k][0];
                    int yNear = y + directs[k][1];
                    tmp += (mp.get(xNear, yNear).getName() == "destroyable") ? 1 : 0;
                }
                if(tmp > maxBreakable) {
                    maxBreakable = tmp;
                    maxBreakableLoc = new Coordinate(x, y);
                }
            }
        }
        return maxBreakableLoc;
    }
}
