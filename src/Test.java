import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

class Map //implements Serializable
{
	public static class OutOfMapIndexException extends Exception
	{
		public OutOfMapIndexException() {super("非法的地图元件编号！");}
	}
	public static class FailureReadMapException extends Exception
	{
		public FailureReadMapException(Throwable cause) {super("读取地图失败！", cause);}
	}
	static final int GROUND = 0, DESTROYABLE = 1, UNBREAKABLE = 2;
	static final int BOMB = 3, HORIFLOW = 4, VERTFLOW = 5, CROSSFLOW = 6;
	static final int BOMBITEM = 7, FLOWITEM = 8, SPEEDITEM = 9;
	static final int WIDTH = 22;			// 实际可活动范围为 20 * 20
	static final int HEIGHT = 22;
	private Image[] textures = new Image[3];
	// private Image ground = null;
	// private Image destroyable = null;
	// private Image unbreakable = null;
	public int[][] mp;
	
	public Map(String type, int id) throws FailureReadMapException
	{
		try
		{
			Scanner mapInput = new Scanner(new FileInputStream("./res/map/" + type + "_" + id + ".txt"));
			mp = new int[HEIGHT][];
			for (int i = 0; i < HEIGHT; ++i) mp[i] = new int[WIDTH];
			for (int i = 1; i < HEIGHT - 1; ++i)
				for (int j = 1; j < WIDTH - 1; ++j)
					mp[i][j] = mapInput.nextInt();
			mapInput.close();
		}
		catch (Exception ex)
		{
			// ex.printStackTrace();
			throw new FailureReadMapException(ex);
		}
		
		this.textures[GROUND] = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_ground.png");
		this.textures[DESTROYABLE] = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_destroyable.png");
		this.textures[UNBREAKABLE] = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_unbreakable.png");
		// this.ground = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_ground.png");
		// this.destroyable = 
		// this.unbreakable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_unbreakable.png");
	}
	public Image getTexture(int idx) {return textures[idx];}

}

public class Test extends JFrame
{
	// private String mapType = null;
	// private int mapID = -1;
	// private Map map = null;
	static final int BLOCK_UNIT = 25;
	static final int WINWIDTH = 17 * 25 + 200;
	static final int WINHEIGHT = 22 * 25 + 100;
	static final int GAMEWIDTH = 22 * 25;
	static final int GAMEHEIGHT = 22 * 25;
	static final int INFOPERPAGE = 10;
	public Test()
	{
		setBounds(100, 100, 680 + 14, 680 + 37);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Graphics g = getContentPane().getGraphics();
		
		System.out.println(new File("./res/texture/forest_ground.png").exists());
		Image img = new ImageIcon("./res/texture/forest_ground.png").getImage();
		// Image bimg = Toolkit.getDefaultToolkit().getImage("./res/texture/bomb1.png");
		Image bimg = new ImageIcon("./res/texture/bomb1.png").getImage();
		Image kimg = new ImageIcon("./res/texture/forest_destroyable.png").getImage();
		System.out.println(img.getWidth(this));
		SwingUtilities.invokeLater(() -> {
			g.drawImage(img, 0, 0, this);
			g.drawImage(kimg, 0, 0, this);
			g.drawImage(kimg, 40, -5, this);
			g.drawImage(kimg, 80, -5, this);
			g.drawImage(kimg, 40, 35, this);
			g.drawImage(kimg, 80, 35, this);
			g.drawImage(kimg, 40, 75, this);
			g.drawImage(kimg, 80, 75, this);
		});
	}
	public static void main(String[] args)
	{
		new Test();
	}
}