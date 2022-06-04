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

public abstract class BaseObject implements Comparable<BaseObject> {
    static int objNums = 0;
    protected int id;               // maybe no use 
    protected boolean isPassable;   //
    protected boolean isBreakable;  //
    protected Coordinate loc;
	protected String name;

    public BaseObject(String name, int x, int y) {
		this.name = name;
		this.loc = new Coordinate(x, y);
        objNums += 1;
        id = objNums;
        //不用path的话，默认的是可通行的没有什么属性的空地？
        isPassable = true;
        isBreakable = false;
    }

    @Override
    public int compareTo(BaseObject o) {
        return this.loc.compareTo(o.loc);
    }

    public int getPosX() {return loc.x;}
    public int getPosY() {return loc.y;}
	public String getName() {return this.name;}
    // 其实也可以通过下面这个函数来判断是否可以通过，不过player以像素为单位的话map里需要加点计算来判断？
    public boolean getIsPassable() {return this.isPassable;}
    public void bombExplode() {}
    public abstract void interactWithBomb(Bomb b);
}
