package GUI;

import BaseObject.BaseObject;
import BaseObject.GameMap;
import BaseObject.Coordinate;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import render.MainRenderer;
import static render.MainRenderer.BLOCK_UNIT;
import static GUI.MyPanel.*;
import render.RenderImage;

import javax.swing.*;

import java.awt.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;

class DrawTask
{
	public Image img;
	public Coordinate loc;
	public DrawTask(Image img, int x, int y)
	{
		this.loc = new Coordinate(x, y);
		this.img = img;
	}
}

public class GamePanel extends JLayeredPane
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	public static final int GAMETOPBIAS = 60;
	// private JPanel gameScene = new JPanel();
	private JButton btnBack = new JButton();
	private JButton btnSound = new JButton();
	private JPanel pnGame = new JPanel();
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	private MainRenderer mainWindow;
	private java.util.List<DrawTask> drawList = new Vector<>();

	public GamePanel(MainRenderer mainWindow) {super(); this.mainWindow = mainWindow;}
	
	private void putTexture(Image img, int x, int y, boolean yBottom, boolean addTop)
	{
		drawList.add(new DrawTask(img, x, y - (yBottom ? img.getHeight(this) : 0) + (addTop ? GAMETOPBIAS : 0)));
	}

	public void readyAnimation()
	{
		try {
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
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void toLayout()
	{
		pnGame.setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);

		setButton(btnSound, SCENEWIDTH - 70, 5, 30, "icon_sound_on");
		btnSound.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				mainWindow.getGame().switchSound();
				if (mainWindow.getGame().isSoundOn())
					setButton(btnSound, "icon_sound_on");
				else setButton(btnSound, "icon_sound_off");
			});
		});
		setButton(btnBack, SCENEWIDTH - 35, 5, 30, "icon_quit");
		btnBack.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				mainWindow.getTitleScene().setVisible(true);
				mainWindow.getGame().commandQueue.add(() -> {mainWindow.getGame().end();});
			});
		});

		lblCountDown.setForeground(Color.ORANGE);
		lblCountDown.setFont(new Font("黑体", Font.BOLD, 60));
		lblCountDown.setSize(200, 200);
		pnAnimate.setBounds(0, GAMETOPBIAS, GAMEWIDTH, GAMEHEIGHT);
		pnAnimate.setVisible(false);
		pnAnimate.setBackground(new Color(255, 255, 255, 100));
		pnAnimate.setLayout(new BorderLayout());
		pnAnimate.add(BorderLayout.CENTER, lblCountDown);

		setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		add(pnGame, 1);
		add(btnSound, 2);
		add(btnBack, 2);
		add(pnAnimate, 3);
		setVisible(false);
	}

	public void updateRender()
	{
		drawList.clear();
		HumanPlayer infoPlayer = mainWindow.getGame().getInfoPlayer();
		Set<BasePlayer> ps = mainWindow.getGame().getPlayers();
		GameMap mp = mainWindow.getGame().getMap();
		putTexture(RenderImage.getImage("background"), 0, 0, false, false);
		putTexture(RenderImage.getImage(mp.getType() + "_ground"), 0, 0, false, true);
		int nowy = 0;
		Iterator<BasePlayer> pIt = ps.iterator();
		BasePlayer curP = pIt.hasNext() ? pIt.next() : null;
		for (int i = 0; i < GameMap.HEIGHT; ++i)
		{
			while (curP != null && nowy >= curP.getBottom())	// 加载人物
			{
				Image pImg = RenderImage.getImage(curP.toString());
				putTexture(pImg, curP.getLeft(), curP.getBottom(), true, true);
				if (curP.getInvincible())
					putTexture(RenderImage.getImage("invincible"), curP.getLeft(), curP.getBottom(), true, true);
				if (curP == infoPlayer)
					putTexture(RenderImage.getImage("infoarrow"), curP.getLeft() + 15, curP.getBottom() - BLOCK_UNIT, true, true);
				curP = pIt.hasNext() ? pIt.next() : null;
			}
			for (int j = 0; j < GameMap.WIDTH; ++j)				// 加载场景
			{
				BaseObject obj = mp.get(j, i);
				if (obj == null) continue;
				Image cImg = null;
				switch (obj.getName())
				{
					case "destroyable": case "unbreakable":
						cImg = RenderImage.getImage(mp.getType() + "_" + obj);
						putTexture(cImg, j * BLOCK_UNIT, nowy + BLOCK_UNIT, true, true); break;
					case "bomb": case "vertflow": case "horiflow": case "crossflow":
					case "bombitem": case "flowitem": case "speeditem":
						cImg = RenderImage.getImage(obj.toString());
						putTexture(cImg, j * BLOCK_UNIT, nowy + BLOCK_UNIT, true, true); break;
					default:
						(new GameMap.OutOfMapIndexException()).printStackTrace();
						System.exit(0);
				}
			}
			nowy += BLOCK_UNIT;
		}
	}
	
	@Override
	public void paint(Graphics gr)
	{
		super.paint(gr);
		SwingUtilities.invokeLater(() -> {
			for (DrawTask dt: drawList)
			{
				Graphics g = pnGame.getGraphics();
				g.drawImage(dt.img, dt.loc.x, dt.loc.y, this);
			}
		});
	}
}
