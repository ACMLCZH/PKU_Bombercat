import javax.swing.*;
import javax.swing.text.AbstractDocument.BranchElement;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.Base64;

class Map implements Serializable
{
	public static class OutOfMapIndexException extends Exception
	{
		public OutOfMapIndexException() {super("非法的地图元件编号！");}
	}
	static final int GROUND = 0;
	static final int DESTROYABLE = 1;
	static final int UNBREAKABLE = 2;
	private Image ground = null;
	private Image destroyable = null;
	private Image unbreakable = null;
	private int width, height;
	public int[][] mp;
	
	public Map(String type, int id)
	{
		try
		{
			ObjectInputStream objInput = new ObjectInputStream(new FileInputStream("./res/map/" + type + "_" + id + ".txt"));
			mp = (int[][])objInput.readObject();
			objInput.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		this.ground = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_ground.png");
		this.destroyable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_destroyable.png");
		this.unbreakable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_unbreakable.png");
	}
	public Image getGround() {return this.ground;}
	public Image getDestroyable() {return this.destroyable;}
	public Image getUnbreakable() {return this.unbreakable;}
	public int getWidth() {return this.width;}
	public int getHeight() {return this.height;}

}

public class Renderer extends JFrame
{
	// private String mapType = null;
	// private int mapID = -1;
	// private Map map = null;
	static final int BLOCK_UNIT = 25;

	private JPanel gameScene = new JPanel();
	
	public Renderer() {}
	
	public void render(Map mp, BasePlayer[] ps) 	// This array will be changed.
	{
		Arrays.<BasePlayer>sort(ps);
		Graphics g = gameScene.getGraphics();
		SwingUtilities.invokeLater(() -> {
			int nowx = BLOCK_UNIT;
			int curP = 0;
			for (int i = 0; i < mp.height; ++ i)
			{
				int nowy = BLOCK_UNIT;
				while (curP < ps.length && nowy >= ps[curP].getBottom())
				{
					Image pImg = ps[curP].getFigureImage();
					g.drawImage(pImg, ps[curP].getRight() - pImg.getWidth(this), ps[curP].getBottom() - pImg.getHeight(this), this);
					++ curP;
				}
				for (int j = 0; j < mp.width; ++ j)
				{
					switch (mp.mp[i][j])
					{
						case Map.GROUND: g.drawImage(mp.getGround(), nowx - BLOCK_UNIT, nowy - BLOCK_UNIT, this); break;
						case Map.DESTROYABLE:
							g.drawImage(mp.getDestroyable(), nowx - mp.getDestroyable().getWidth(this), nowy - mp.getDestroyable().getHeight(this), this);
							break;
						case Map.UNBREAKABLE:
							g.drawImage(mp.getUnbreakable(), nowx - mp.getUnbreakable().getWidth(this), nowy - mp.getUnbreakable().getHeight(this), this);
							break;
						default: throw new Map.OutOfMapIndexException();
					}
					nowy += BLOCK_UNIT;
				}
				nowx += BLOCK_UNIT;
			}
		});
	}
}