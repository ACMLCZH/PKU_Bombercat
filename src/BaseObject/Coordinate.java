package BaseObject;

public class Coordinate implements Comparable<Coordinate>
{
	public int x, y;		// width, height

	public Coordinate(int x, int y) {this.x = x; this.y = y;}

	@Override
	public int compareTo(Coordinate o)
	{
        int r = this.y - o.y;
        if (r == 0) return this.x - o.x;
		return r;
	}

	public boolean equals(Coordinate o) {return this.x == o.x && this.y == o.y;}
};