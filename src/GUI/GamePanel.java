package GUI;

import static DEBUG.Dbg.msg;

import BaseObject.BaseObject;
import BaseObject.GameMap;
import BaseObject.Coordinate;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import render.MainRenderer;
import static render.MainRenderer.BLOCK_UNIT;
import render.RenderImage;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
// import java.util.ArrayList;
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

public class GamePanel extends MyPanel
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	public static final int GAMETOPBIAS = 60;
	private java.util.List<DrawTask> drawList = new Vector<>();
	// private Image gameImg = null;

	public GamePanel(MainRenderer mainWindow) {super(mainWindow);}

	@Override
	public void toLayout() {}
	// @Override
	// public void update(Graphics g)
	// {
	// 	if (gameImg == null) gameImg = this.createImage(SCENEWIDTH, SCENEHEIGHT);
	// 	paint(gameImg.getGraphics());
	// 	g.drawImage(gameImg, 0, 0, this);
	// }
	@Override
	public void paintComponent(Graphics g)
	{
		// super.paint(gr);
		synchronized (drawList) {
			// msg(drawList.size());
			for (DrawTask dt: drawList)
				g.drawImage(dt.img, dt.loc.x, dt.loc.y, this);
		}
	}

	private void putTexture(Image img, int x, int y, boolean yBottom, boolean addTop)
	{
		drawList.add(new DrawTask(img, x, y - (yBottom ? img.getHeight(this) : 0) + (addTop ? GAMETOPBIAS : 0)));
	}
	
	public void updateRender()
	{
		synchronized (drawList) {
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
					if (curP.isInvincible())
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
	}
}
