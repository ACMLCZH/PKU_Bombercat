package GUI;

import BaseObject.Bomb;
import BaseObject.GameMap;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import render.MainRenderer;
import static render.MainRenderer.BLOCK_UNIT;
import static GUI.MyPanel.*;
import render.RenderImage;

import javax.swing.*;
import java.awt.*;
// import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class GamePanel extends JLayeredPane
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	public static final int GAMETOPBIAS = 40;
	// private JPanel gameScene = new JPanel();
	private JButton btnBack = new JButton();
	private JButton btnSound = new JButton();
	private JPanel pnGame = new JPanel();
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	private MainRenderer mainWindow;

	public GamePanel(MainRenderer mainWindow) {super(); this.mainWindow = mainWindow;}
	
	private void putTexture(Graphics g, int rx, int ry, Image img)
	{
		g.drawImage(img, rx - img.getWidth(this), ry - img.getHeight(this), this);
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
				try {
					mainWindow.getGame().commandQueue.put(() -> {mainWindow.getGame().end();});
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
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

	@Override
	public void paint(Graphics gr)
	{
		super.paint(gr);
		SwingUtilities.invokeLater(() -> {
			Graphics g = pnGame.getGraphics();
			Set<BasePlayer> ps = mainWindow.getGame().getPlayers();
			HumanPlayer infoPlayer = mainWindow.getGame().getInfoPlayer();
			Set<Bomb> bs = mainWindow.getGame().getBombs();
			GameMap mp = mainWindow.getGame().getMap();
			// ps.sort(null);
			// bs.sort(null);
			// Arrays.<BasePlayer>sort(ps);
			// Arrays.<Bomb>sort(bs);
			g.drawImage(RenderImage.getImage(mp.getType() + "_" + GameMap.MapElement.Ground), 0, 0, this);
			int nowy = BLOCK_UNIT;
			Iterator<BasePlayer> pIt = ps.iterator();
			Iterator<Bomb> bIt = bs.iterator();
			BasePlayer curP = pIt.hasNext() ? pIt.next() : null;
			Bomb curB = bIt.hasNext() ? bIt.next() : null;
			// int curP = 0, curB = 0;
			for (int i = 0; i < GameMap.HEIGHT; ++i)
			{
				int nowx = BLOCK_UNIT;
				while (curP != null && nowy >= curP.getBottom())
				{
					Image pImg = RenderImage.getImage(curP.getName() + "_" + curP.getDirection());
					putTexture(g, curP.getRight(), curP.getBottom(), pImg);
					// g.drawImage(pImg, - pImg.getWidth(this),  - pImg.getHeight(this), this);
					if (curP == infoPlayer)
						putTexture(g, curP.getRight() - 15, curP.getBottom() - 40, RenderImage.getImage("infoarrow"));
					curP = pIt.hasNext() ? pIt.next() : null;
				}
				for (int j = 0; j < GameMap.WIDTH; ++j)
				{
					Image cImg = null;
					switch (GameMap.toElement[mp.mp[i][j]])
					{
						case Ground: case Bomb: break;
						case Destroyable: case Unbreakable:
							cImg = RenderImage.getImage(mp.getType() + "_" + GameMap.toElement[mp.mp[i][j]]);
							putTexture(g, nowx, nowy, cImg); break;
						case Vertflow: case Horiflow: case Crossflow:
						case Bombitem: case Flowitem: case Speeditem:
							cImg = RenderImage.getImage(GameMap.toElement[mp.mp[i][j]].toString());
							putTexture(g, nowx, nowy, cImg); break;
						default:
							(new GameMap.OutOfMapIndexException()).printStackTrace();
							System.exit(0);
					}
					nowx += BLOCK_UNIT;
				}
				while (curB != null && nowy > curB.getPosY() * BLOCK_UNIT)
				{
					Image bImg = RenderImage.getImage(curB.getName() + "_" + curB.getState());
					putTexture(g, (curB.getPosX() + 1) * BLOCK_UNIT, (curB.getPosY() + 1) * BLOCK_UNIT, bImg);
					// g.drawImage(bImg, bs[curB].getPosX() * BLOCK_UNIT, bs[curB].getPosY() * BLOCK_UNIT, this);
					curB = bIt.hasNext() ? bIt.next() : null;
				}
				nowy += BLOCK_UNIT;
			}
		});
	}
}
