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

	public Coordinate toGrid()
	{
		return new Coordinate(
			this.x / BLOCK_UNIT + (this.x >= 0 ? 0 : (this.x % BLOCK_UNIT == 0 ? 0 : -1)), 
			this.y / BLOCK_UNIT + (this.y >= 0 ? 0 : (this.y % BLOCK_UNIT == 0 ? 0 : -1))
		);
	}
	public Coordinate toPixel() {return new Coordinate(this.x * BLOCK_UNIT, this.y * BLOCK_UNIT);}

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
	public void step(Indirect dir, int stride)
	{
		switch (dir)
		{
			case UP: y -= stride; break;
			case DOWN: y += stride; break;
			case LEFT: x -= stride; break;
			case RIGHT: x += stride; break;
			default:
		}
	}
};