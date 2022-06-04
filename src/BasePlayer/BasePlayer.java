package BasePlayer;

import main.Game;


public class BasePlayer {
    enum Indirect {					// 这个是必要的，最好别改
        UP("up"), DOWN("down"), LEFT("left"), RIGHT("right");
		private String s;
		private Indirect(String s) {this.s = s;}
		@Override
		public String toString() {return s;}
    };
    // private int coolingTime;
    protected int HP;
	protected Indirect dir;
	protected String name = null;
	protected int x1, y1, x2, y2;		// Bounding Box

	public BasePlayer(int HP, Indirect dir, String name)
	{
		this.HP = HP;
		this.dir = dir;
		this.name = name;
	}
    
	public Indirect getDirection() {return dir;}
	public int getBottom() {return y2;}
	public int getRight() {return x2;}
	public int getLeft() {return x1;}
	public String getName() {return name;}

    public boolean move(Game g, Indirect dir) {
        return false;
    }

    public boolean placeBomb(Game g) {
        return false;
    }

}
