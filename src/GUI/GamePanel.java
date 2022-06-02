package GUI;

import main.Game;
import BaseObject.Bomb;
import BaseObject.GameMap;
import BasePlayer.BasePlayer;
import render.MainRenderer;
import static render.MainRenderer.BLOCK_UNIT;
import render.RenderImage;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class GamePanel extends MyPanel
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	// private JPanel gameScene = new JPanel();
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	public GamePanel(MainRenderer mainWindow) {super(mainWindow);}
	
	private void putTexture(Graphics g, int rx, int ry, Image img)
	{
		g.drawImage(img, rx - img.getWidth(this), ry - img.getHeight(this), this);
	}

	public void readyAnimation() throws InterruptedException
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

	public void toLayout()
	{
		lblCountDown.setForeground(Color.ORANGE);
		lblCountDown.setFont(new Font("黑体", Font.BOLD, 60));
		lblCountDown.setSize(200, 200);
		pnAnimate.setBounds(0, 0, GAMEWIDTH, GAMEHEIGHT);
		pnAnimate.setVisible(false);
		pnAnimate.setBackground(new Color(255, 255, 255, 100));
		pnAnimate.setLayout(new BorderLayout());
		pnAnimate.add(BorderLayout.CENTER, lblCountDown);
		addPanel(new Component[]{pnAnimate}, false);
	}

	@Override
	public void paint(Graphics g)
	{
		// Graphics g = gameScene.getGraphics();
		SwingUtilities.invokeLater(() -> {
			BasePlayer[] ps = mainWindow.getGame().getPlayers();
			BasePlayer infoPlayer = mainWindow.getGame().getInfoPlayer();
			Bomb[] bs = mainWindow.getGame().getBombs();
			GameMap mp = mainWindow.getGame().getMap();
			Arrays.<BasePlayer>sort(ps);
			Arrays.<Bomb>sort(bs);
			g.drawImage(RenderImage.getImage(mp.getType() + "_" + GameMap.MapElement.Ground), 0, 0, this);
			int nowy = BLOCK_UNIT;
			int curP = 0, curB = 0;
			for (int i = 0; i < GameMap.HEIGHT; ++i)
			{
				int nowx = BLOCK_UNIT;
				while (curP < ps.length && nowy >= ps[curP].getBottom())
				{
					Image pImg = RenderImage.getImage(ps[curP].getName() + "_" + ps[curP].getDirection());
					putTexture(g, ps[curP].getRight(), ps[curP].getBottom(), pImg);
					// g.drawImage(pImg, - pImg.getWidth(this),  - pImg.getHeight(this), this);
					if (ps[curP] == infoPlayer)
						putTexture(g, ps[curP].getRight() - 15, ps[curP].getBottom() - 40, RenderImage.getImage("infoarrow"));
					++curP;
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
						default: throw new GameMap.OutOfMapIndexException();
					}
					nowx += BLOCK_UNIT;
				}
				while (curB < bs.length && nowy > bs[curB].getPosY() * BLOCK_UNIT)
				{
					Image bImg = RenderImage.getImage(bs[curB].getName() + "_" + bs[curB].getState());
					putTexture(g, (bs[curB].getPosX() + 1) * BLOCK_UNIT, (bs[curB].getPosY() + 1) * BLOCK_UNIT, bImg);
					// g.drawImage(bImg, bs[curB].getPosX() * BLOCK_UNIT, bs[curB].getPosY() * BLOCK_UNIT, this);
					++curB;
				}
				nowy += BLOCK_UNIT;
			}
		});
	}
}
