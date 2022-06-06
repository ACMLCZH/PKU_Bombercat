package BaseObject;

import BasePlayer.Indirect;
import static render.MainRenderer.BLOCK_UNIT;
import static BasePlayer.BasePlayer.STRIDE;

public class Coordinate implements Comparable<Coordinate>
{
	public int x, y;		// width, height

	public Coordinate(int x, int y) {this.x = x; this.y = y;}
	public Coordinate(Coordinate p) {this.x = p.x; this.y = p.y;}

	@Override
	public String toString() {return x + " " + y;}
	@Override
	public int compareTo(Coordinate o)
	{
        int r = this.y - o.y;
        if (r == 0) return this.x - o.x;
		return r;
	}
	public boolean equals(Coordinate o) {return this.x == o.x && this.y == o.y;}

	public Coordinate toGrid() {return new Coordinate(x / BLOCK_UNIT, y / BLOCK_UNIT);}

	public void step(Indirect dir)
	{
		switch (dir)
		{
			case UP: y -= STRIDE; break;
			case DOWN: y += STRIDE; break;
			case LEFT: x -= STRIDE; break;
			case RIGHT: x += STRIDE; break;
			default:
		}
	}
};