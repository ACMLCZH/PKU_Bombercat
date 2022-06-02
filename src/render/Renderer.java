package render;
import javax.security.auth.Destroyable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AbstractDocument.BranchElement;

// import Map;
import BaseObject.Bomb;
import BasePlayer.BasePlayer;
import BaseObject.GameMap;
// import GameMap.MapElement;
import GUI.*;
import main.Game;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;


public class Renderer extends JFrame
{
	// private String mapType = null;
	// private int mapID = -1;
	// private Map map = null;
	public static final int BLOCK_UNIT = 40;
	// static final int SCENEWIDTH = GAMEWIDTH;
	// static final int SCENEHEIGHT = GAMEHEIGHT + 20;
	// static final int WINWIDTH = SCENEWIDTH + 14;
	// static final int WINHEIGHT = SCENEHEIGHT + 37;

	private GamePanel gameScene = new GamePanel(this);
	private TitlePanel titleScene = new TitlePanel(this);
	private InfoPanel infoScene = new InfoPanel(this);
	private JPanel selectScene = new JPanel();

	private Game game = null;
	
	public void render(GameMap mp, BasePlayer[] ps, Bomb[] bs, BasePlayer infoPlayer)
	{
		Arrays.<BasePlayer>sort(ps); 		// This array will be changed.
		Arrays.<Bomb>sort(bs); 				// This array will be changed.
		Graphics g = gameScene.getGraphics();
		SwingUtilities.invokeLater(() -> {
			g.drawImage(RenderImage.getImage(mp.getType() + "_" + GameMap.MapElement.Ground), 0, 0, this);
			int nowy = BLOCK_UNIT;
			int curP = 0, curB = 0;
			for (int i = 0; i < Map.HEIGHT; ++i)
			{
				int nowx = BLOCK_UNIT;
				while (curP < ps.length && nowy >= ps[curP].getBottom())
				{
					Image pImg = RenderImage.getImage(ps[curP].getName() + "_" + ps[curP].getDirection());
					putTexture(g, ps[curP].getRight(), ps[curP].getBottom(), pImg);
					// g.drawImage(pImg, - pImg.getWidth(this),  - pImg.getHeight(this), this);
					if (ps[curP] == infoPlayer)
						putTexture(g, ps[curP].getRight() - 15, ps[curP].getBottom() - 40, RenderImage.getImage.get("infoarrow"));
					++curP;
				}
				for (int j = 0; j < Map.WIDTH; ++j)
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
						default: throw new Map.OutOfMapIndexException();
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

	private void putTexture(Graphics g, int rx, int ry, Image img)
	{
		g.drawImage(img, rx - img.getWidth(this), ry - img.getHeight(this), this);
	}
	private void addPanel(JPanel scene, Component[] comps, boolean vis)
	{
		scene.setVisible(vis);
		scene.setLayout(null);
		scene.setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		for (Component comp: comps) scene.add(comp);
		getContentPane().add(scene);
	}
	private void setButton(JButton btn, int x, int y, int len, String iconName)
	{
		ImageIcon icon = RenderImage.getIcon(iconName);
		icon.setImage(icon.getImage().getScaledInstance(len, len, Image.SCALE_DEFAULT));
		btn.setIcon(icon);
		btn.setMargin(new Insets(0, 0, 0, 0));	// 将边框外的上下左右空间设置为0
		btn.setIconTextGap(0);		// 将标签中显示的文本和图标之间的间隔量设置为0
		btn.setBorderPainted(false);			// 不打印边框
		btn.setBorder(null);			// 除去边框
		btn.setFocusPainted(false);			// 除去焦点的框
		btn.setContentAreaFilled(false);		// 除去默认的背景填充
		btn.setBounds(x, y, len, len);
	}

	private void gameEnd()
	{
		SwingUtilities.invokeLater(() -> {
			gameScene.setVisible(false);
			titleScene.setVisible(true);
		});
	}
	
	public Renderer(Game game)
	{
		this.game = game;
		SwingUtilities.invokeLater(() -> {
			setTitle("泡泡堂 in PKU");
			setLayout(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setResizable(false);

			titleScene.toLayout(game, gameScene, infoScene);
			gameScene.toLayout();
			infoScene.toLayout(titleScene);
		});
	}
}