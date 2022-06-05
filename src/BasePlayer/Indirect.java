package BasePlayer;

public enum Indirect {					// 这个是必要的，最好别改
    UP("up"), DOWN("down"), LEFT("left"), RIGHT("right"), STOP("stop");
	private String s;
	private Indirect(String s) {this.s = s;}
	@Override
	public String toString() {return s;}
	public static Indirect codeToIndirect(int code) {
        switch (code) {
            case 37:
            case 65:
                return Indirect.LEFT;
            case 38:
            case 87:
                return Indirect.UP;
            case 39:
            case 68:
                return Indirect.RIGHT;
            case 40:
            case 83:
                return Indirect.DOWN;
            default:
                return Indirect.STOP;
        }
}
};