import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
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
	static final int GROUND = 0;
	static final int DESTROYABLE = 1;
	static final int UNBREAKABLE = 2;
	static final int WIDTH = 22;			// 实际可活动范围为 20 * 20
	static final int HEIGHT = 22;
	private Image ground = null;
	private Image destroyable = null;
	private Image unbreakable = null;
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
		
		this.ground = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_ground.png");
		this.destroyable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_destroyable.png");
		this.unbreakable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_unbreakable.png");
	}
	public Image getGround() {return this.ground;}
	public Image getDestroyable() {return this.destroyable;}
	public Image getUnbreakable() {return this.unbreakable;}
	// public int getWidth() {return this.width;}
	// public int getHeight() {return this.height;}

}

public class Renderer extends JFrame
{
	// private String mapType = null;
	// private int mapID = -1;
	// private Map map = null;
	static final int BLOCK_UNIT = 25;
	static final int WINWIDTH = 22 * 25 + 200;
	static final int WINHEIGHT = 22 * 25 + 100;

	private JPanel gameScene = new JPanel();

	private JPanel titleScene = new JPanel();
	private JLabel lblTitle = new JLabel("");
	private JButton btnInfo = new JButton();
	private JButton btnStart = new JButton();
	private JButton btnQuit = new JButton();

	private JPanel infoScene = new JPanel();
	private JButton btnInfoLeft = new JButton();
	private JButton btnInfoRight = new JButton();
	private JButton btnInfoBack = new JButton();
	private JTextArea txtInfo = new JTextArea();
	
	public void render(Map mp, BasePlayer[] ps, Bomb[] bs) 	// This array will be changed.
	{
		Arrays.<BasePlayer>sort(ps);
		Arrays.<Bomb>sort(bs);
		Graphics g = gameScene.getGraphics();
		SwingUtilities.invokeLater(() -> {
			g.drawImage(mp.getGround(), 0, 0, this);
			int nowx = BLOCK_UNIT;
			int curP = 0, curB = 0;
			for (int i = 0; i < Map.HEIGHT; ++i)
			{
				int nowy = BLOCK_UNIT;
				while (curP < ps.length && nowy >= ps[curP].getBottom())
				{
					Image pImg = ps[curP].getFigureImage();
					g.drawImage(pImg, ps[curP].getRight() - pImg.getWidth(this), ps[curP].getBottom() - pImg.getHeight(this), this);
					++ curP;
				}
				while (curB < bs.length && nowy > bs[curB].getPosY() * BLOCK_UNIT)
					g.drawImage(bs[curB++].getFigureImage(), nowx - BLOCK_UNIT, nowy - BLOCK_UNIT, this);
				for (int j = 0; j < Map.WIDTH; ++j)
				{
					switch (mp.mp[i][j])
					{
						case Map.GROUND:  break;
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

	private void addPanel(JPanel scene, Component[] comps, boolean vis)
	{
		scene.setVisible(vis);
		scene.setBounds(0, 0, WINWIDTH, WINHEIGHT);
		for (Component comp: comps) scene.add(comp);
		getContentPane().add(scene);
	}

	private void gameEnd()
	{
		SwingUtilities.invokeLater(() -> {
			gameScene.setVisible(false);
			titleScene.setVisible(true);
		});
	}
	
	public Renderer()
	{
		SwingUtilities.invokeLater(() -> {
			setTitle("PP堂");
			setLayout(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);

			lblTitle.setBounds(100, 100, 100, 100);
			btnInfo.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					titleScene.setVisible(false);
					infoScene.setVisible(true);
				});
			});
			btnStart.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					titleScene.setVisible(false);
					gameScene.setVisible(true);
					Game g = new Game();
					readyAnimation();
					g.start();
				});
			});
			btnQuit.addActionListener((e) -> {System.exit(0);});
			addPanel(titleScene, new Component[] {lblTitle, btnInfo, btnStart, btnQuit}, true);

			addPanel(gameScene, new Component[] {}, false);
			addPanel(infoScene, new Component[] {btnInfoLeft, btnInfoRight, btnInfoBack, txtInfo}, false);
		});
	}
}