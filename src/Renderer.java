import javax.security.auth.Destroyable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AbstractDocument.BranchElement;

import BaseObject.Bomb;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

class GameMap //implements Serializable
{
	public static class OutOfMapIndexException extends Exception
	{
		public OutOfMapIndexException() {super("非法的地图元件编号！");}
	}
	public static class FailureReadMapException extends Exception
	{
		public FailureReadMapException(Throwable cause) {super("读取地图失败！", cause);}
	}
	enum MapElement {
		Ground("ground", 0), Destroyable("destroyable", 1), Unbreakable("unbreakable", 2),
		Bomb("bomb", 3), Horiflow("horiflow", 4), Vertflow("vertflow", 5), Crossflow("crossflow", 6),
		Bombitem("bombitem", 7), flowitem("flowitem", 8), Speeditem("speeditem", 9);
		private String s;
		private int num;
		private MapElement(String s, int num) {this.s = s; this.num = num;}
		public String toString() {return this.s;}
		public boolean equals(int x) {return this.num == x;}
	}
	static final int GROUND = 0, DESTROYABLE = 1, UNBREAKABLE = 2;
	static final int BOMB = 3, HORIFLOW = 4, VERTFLOW = 5, CROSSFLOW = 6;
	static final int BOMBITEM = 7, FLOWITEM = 8, SPEEDITEM = 9;
	static final int WIDTH = 15;			// 实际可活动范围为 15 * 15
	static final int HEIGHT = 15;
	private Image[] textures = new Image[3];
	// private Image ground = null;
	// private Image destroyable = null;
	// private Image unbreakable = null;
	public int[][] mp;
	
	public GameMap(String type, int id) throws FailureReadMapException
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
	// public Image getGround() {return this.ground;}
	// public Image getDestroyable() {return this.destroyable;}
	// public Image getUnbreakable() {return this.unbreakable;}
	// public int getWidth() {return this.width;}
	// public int getHeight() {return this.height;}

}

public class Renderer extends JFrame
{
	// private String mapType = null;
	// private int mapID = -1;
	// private Map map = null;
	static final int BLOCK_UNIT = 40;
	static final int WINWIDTH = 17 * BLOCK_UNIT + 200;
	static final int WINHEIGHT = 17 * BLOCK_UNIT + 100;
	static final int GAMEWIDTH = 17 * BLOCK_UNIT;
	static final int GAMEHEIGHT = 17 * BLOCK_UNIT;
	static final int INFOPERPAGE = 10;

	private JPanel gameScene = new JPanel();
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	private Image[] textures = new Image[10];

	private JPanel titleScene = new JPanel();
	private JLabel lblTitle = new JLabel("");
	private int curInfoPage = 0;
	private int infoPages = 0;
	private int infoNumLine = 0;
	// private ArrayList<String> infoText = new ArrayList<>();
	private ArrayList<StringBuffer> infoText = new ArrayList<>();
	private JButton btnInfo = new JButton();
	private JButton btnStart = new JButton();
	private JButton btnQuit = new JButton();

	private JPanel infoScene = new JPanel();
	private JButton btnInfoLeft = new JButton();
	private JButton btnInfoRight = new JButton();
	private JButton btnInfoBack = new JButton();
	private JTextArea txtInfo = new JTextArea();

	private Game g = null;
	
	public void render(GameMap mp, BasePlayer[] ps, Bomb[] bs, BasePlayer infoPlayer)
	{
		Arrays.<BasePlayer>sort(ps); 		// This array will be changed.
		Arrays.<Bomb>sort(bs); 				// This array will be changed.
		Graphics g = gameScene.getGraphics();
		SwingUtilities.invokeLater(() -> {
			g.drawImage(mp.getTexture(Map.GROUND), 0, 0, this);
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
				// while (curB < bs.length && nowy > bs[curB].getPosY() * BLOCK_UNIT)
				// 	g.drawImage(bs[curB++].getFigureImage(), nowx - BLOCK_UNIT, nowy - BLOCK_UNIT, this);
				for (int j = 0; j < Map.WIDTH; ++j)
				{
					switch (mp.mp[i][j])
					{
						case Map.GROUND: case Map.BOMB: break;
						case Map.DESTROYABLE: case Map.UNBREAKABLE:
							putTexture(g, nowx, nowy, mp.getTexture(mp.mp[i][j])); break;
						case Map.VERTFLOW: case Map.HORIFLOW: case Map.CROSSFLOW:
						case Map.BOMBITEM: case Map.FLOWITEM: case Map.SPEEDITEM:
							putTexture(g, nowx, nowy, textures[mp.mp[i][j]]); break;
						default: throw new Map.OutOfMapIndexException();
					}
					nowy += BLOCK_UNIT;
				}
				nowx += BLOCK_UNIT;
			}
		});
	}

	private void putTexture(Graphics g, int rx, int ry, Image img)
	{
		g.drawImage(img, rx - img.getWidth(this), ry - img.getHeight(this), this);
	}
	private void addPanel(JPanel scene, Component[] comps, boolean vis)
	{
		scene.setVisible(vis);
		scene.setLayout(null);
		scene.setBounds(0, 0, WINWIDTH, WINHEIGHT);
		for (Component comp: comps) scene.add(comp);
		getContentPane().add(scene);
	}

	private void readyAnimation()
	{
		pnAnimate.setVisible(true);
		lblCountDown.setText("3");
		Thread.sleep(1000);
		lblCountDown.setText("2");
		Thread.sleep(1000);
		lblCountDown.setText("1");
		Thread.sleep(1000);
		lblCountDown.setText("Start!");
		Thread.sleep(1000);
		lblCountDown.setText("");
		pnAnimate.setVisible(false);
	}
	private void gameEnd()
	{
		SwingUtilities.invokeLater(() -> {
			gameScene.setVisible(false);
			titleScene.setVisible(true);
		});
	}

	private void infoShow() {txtInfo.setText(infoText.get(curInfoPage).toString());}
	private void infoInitial()
	{
		curInfoPage = 0;
		infoShow();
	}
	
	public Renderer(Game g)
	{
		this.g = g;
		SwingUtilities.invokeLater(() -> {
			setTitle("PP堂");
			setLayout(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setResizable(false);

			lblTitle.setBounds(100, 100, 100, 100);
			btnInfo.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					titleScene.setVisible(false);
					infoInitial();
					infoScene.setVisible(true);
				});
			});
			btnStart.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					titleScene.setVisible(false);
					gameScene.setVisible(true);
					Game g = new Game(this);
					readyAnimation();
					g.start();
				});
			});
			btnQuit.addActionListener((e) -> {System.exit(0);});
			addPanel(titleScene, new Component[] {lblTitle, btnInfo, btnStart, btnQuit}, true);

			lblCountDown.setForeground(Color.ORANGE);
			lblCountDown.setFont(new Font("黑体", Font.BOLD, 60));
			lblCountDown.setSize(200, 200);
			pnAnimate.setBounds(0, 0, GAMEWIDTH, GAMEHEIGHT);
			pnAnimate.setVisible(false);
			pnAnimate.setBackground(new Color(255, 255, 255, 100));
			pnAnimate.setLayout(new BorderLayout());
			pnAnimate.add(BorderLayout.CENTER, lblCountDown);
			addPanel(gameScene, new Component[] {pnAnimate}, false);

			BufferedReader infoInput = new BufferedReader(new FileReader("./res/text/info.txt"));
			String line = null;
			while ((line = infoInput.readLine()) != null)
			{
				if (infoNumLine == 0) {++infoPages; infoText.add(new StringBuffer(""));}
				infoText.get(infoPages - 1).append(line + "\n");
				if (++infoNumLine == INFOPERPAGE) infoNumLine = 0;
			}
			// infoPages = infoNumLine / INFOPERPAGE;
			infoInput.close();
			// btnInfoLeft.setBounds(x, y, );
			btnInfoLeft.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					if (curInfoPage > 0) {--curInfoPage; infoShow();}
				});
			});
			btnInfoRight.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					if (curInfoPage < infoPages - 1) {++curInfoPage; infoShow();}
				});
			});
			btnInfoBack.addActionListener((e) -> {
				SwingUtilities.invokeLater(() -> {
					infoScene.setVisible(false);
					titleScene.setVisible(true);
				});
			});
			addPanel(infoScene, new Component[] {btnInfoLeft, btnInfoRight, btnInfoBack, txtInfo}, false);
		});
	}
}