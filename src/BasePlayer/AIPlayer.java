package BasePlayer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.random.*;

import BaseObject.Coordinate;
import BaseObject.GameMap;
import BaseObject.Bomb;
import main.Game;

public class AIPlayer extends BasePlayer
{
    public static int INIT_HP = 8000;
    private Indirect lastDir;
    private int maxSaveDir;          // 保存路径的最大数，即移动步数过多后要重算
    private Queue<Indirect> saveDir; // 简单的保存确定的一系列行动
    private final int[][] directs = {{0,-1},{0,1},{-1,0},{1,0}}; //姑且这样存着
    private long stopTime;

    public AIPlayer(int HP, Coordinate spawn, Indirect dir, String name, Game g) {
        super(HP, spawn,  dir, name, g);
        // thread 
    }

    void mainloop() {
        
    }

    public Indirect decideMove() {
        GameMap mp = this.game.getMap();
        Indirect nextdir;
        // 计算中心所在的格点
        Coordinate center = getGridLoc();
        // 不在中心时沿用前一步的移动
        if((p1.x+p2.x)/2 != center.x*BasePlayer.pixelsPerBlock || (p1.y+p2.y)/2 != center.y*pixelsPerBlock) {
            return lastDir;
        }
        // 每次会完成保存的路径
        if(!saveDir.isEmpty()) {
            lastDir = saveDir.poll();
            return lastDir;
        }
        if(lastDir == Indirect.STOP) {
            long current = System.currentTimeMillis();
            if(current - stopTime >= 1000) return lastDir;
        }
        
        // 宽搜
        int[][] smp = new int[GameMap.HEIGHT][];
        int[][] lastMp = new int[GameMap.HEIGHT][];
        for(int i=0; i<GameMap.HEIGHT; ++i) {
            smp[i] = new int[GameMap.WIDTH]; 
            lastMp[i] = new int[GameMap.WIDTH];
        }
        Queue<Coordinate> queue = new LinkedList<Coordinate>();
        queue.add(center);
        smp[center.y][center.x] = 1;
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
        Coordinate nearestP = center;
        for(int i=0; i<GameMap.HEIGHT; ++i) 
            for(int j=0; j<GameMap.WIDTH; ++j) {
                if(newMp[i][j] > 0) {
                    if(newMp[i][j] < minSkip) {
                        minSkip = newMp[i][j];
                        nearestP = new Coordinate(j, i);
                    }
                }
            }
        if(newMp[center.y][center.x] == -1) { // 在炸弹范围内
            lastDir = findWay(center, nearestP, lastMp);
            return lastDir;
        }
        else if(minSkip > 1) { //走一步的地方都被锁定，等炸弹
            lastDir = Indirect.STOP;
            stopTime = System.currentTimeMillis();
            return lastDir;
        }
        // 下面是向玩家前行的路。
        Coordinate humanP = this.game.getInfoPlayer().getGridLoc();
        if(smp[humanP.y][humanP.x] != 0) {
            lastDir = findWay(center, humanP, lastMp);
        }
        return lastDir;
    }

    public boolean decidePlaceBomb() {
        
    }

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
                        if(x<0 || x>=GameMap.WIDTH) continue;
                        if(smp[i][x] <= 0) continue;
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
            switch(k) {   //这里是反推，所以对应的上下左右是反的
                case 0: SaveList.addFirst(Indirect.DOWN); break;
                case 1: SaveList.addFirst(Indirect.UP); break;
                case 2: SaveList.addFirst(Indirect.RIGHT); break;
                case 3: SaveList.addFirst(Indirect.LEFT); break;
                default: 
            }
            dircnt += 1;
            if(dircnt == maxSaveDir) break;
        }
        saveDir = SaveList;
        return saveDir.poll();
    }
}
