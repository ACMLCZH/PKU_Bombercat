package DEBUG;

public class Dbg
{
	public static void msg(Object o)
	{
		System.out.println(o);
	}
	public static void msg(Object[] o)
	{
		for (Object ob : o) System.out.print(ob + " ");
		System.out.println("");
	}
}
