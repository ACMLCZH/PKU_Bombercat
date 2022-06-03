package BaseObject;

class Coordinate implements Comparable<Coordinate>{
	public int x, y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int compareTo(Coordinate o) {
        int r = this.y - o.y;
        if (r == 0)
            return this.x - o.x;
		return r;
	}

	public boolean equals(Coordinate o) {return this.x == o.x && this.y == o.y;}
};

public class BaseObject implements Comparable<BaseObject> {
    static int objNums = 0;
    protected int id;               // use for sort
    protected boolean isPassable;   //
    protected boolean isBreakable;  //
    protected Coordinate loc;
	protected String name;

    public BaseObject(String name, int x, int y) {
		this.name = name;
		this.loc = new Coordinate(x, y);
        objNums += 1;
        id = objNums;
    }

    @Override
    public int compareTo(BaseObject o) {
        return this.loc.compareTo(o.loc);
    }

    public int getPosX() {return loc.x;}
    public int getPosY() {return loc.y;}
	public String getName() {return this.name;}
}
