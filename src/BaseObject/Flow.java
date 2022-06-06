package BaseObject;

import main.Game;

public class Flow extends BaseObject
{
    private static final int flowTime = 500; 	// 假设flow的寿命是0.5秒
	private int atk;
	private boolean drop;
    private long lastUpdated;
	private Game game;

    public Flow(Game game, String name, int x, int y, int atk, boolean drop)
    {
        super(name, x, y);
		this.game = game;
		this.atk = atk;
		this.drop = drop;
        isBreakable = false;
        isPassable = true;
        lastUpdated = System.currentTimeMillis();
    }

	@Override
	public String toString() {return this.name;}
	@Override
    public void interactWithBomb(Bomb bomb)
    {
        if (bomb.getPosX() == loc.x && bomb.getPosY() == loc.y)
            name = "crossflow";
        else if (bomb.getPosX() == loc.x)
            name = "vertflow";
        else if (bomb.getPosY() == loc.y)
            name = "horiflow";
        lastUpdated = System.currentTimeMillis();
    }

	public int getAtk() {return this.atk;}
	// public int 
	public void crash()
	{
		Prop prop = null;
		if (this.drop)
            prop = new Prop(Prop.toItemName.get((int)(Math.random() * 3)), loc.x, loc.y);
		game.getMap().set(loc, prop);
	}

    public boolean countDown()
    {
        long current = System.currentTimeMillis();
        return current - lastUpdated >= flowTime;
    }

}
