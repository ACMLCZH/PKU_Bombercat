package GUI;

import BaseObject.BaseObject;
import BaseObject.GameMap;
import BaseObject.Coordinate;
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import render.MainRenderer;
import static render.MainRenderer.BLOCK_UNIT;
import static BasePlayer.BasePlayer.PLAYER_UNIT;
import render.RenderImage;

import java.awt.*;
import java.awt.image.BufferedImage;
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

public class GamePanel extends MyPanel
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	public static final int GAMETOPBIAS = 60;
	private static final Font HPFont = new Font("Times New Roman", Font.PLAIN, 12);
	private static final int HPWIDTH = 50;
	private static final int HPHEIGHT = 15;
	private java.util.List<DrawTask> drawList = new Vector<>();
	private HumanPlayer infoPlayer = null;

	public GamePanel(MainRenderer mainWindow) {super(mainWindow);}

	@Override
	public void toLayout() {}
	@Override
	public void paint(Graphics g)
	{
		synchronized (drawList) {
			for (DrawTask dt: drawList)
				g.drawImage(dt.img, dt.loc.x, dt.loc.y, this);
		}
	}

	private void putTexture(Image img, int x, int y, boolean yBottom, boolean addTop)
	{
		drawList.add(new DrawTask(img, x, y - (yBottom ? img.getHeight(this) : 0) + (addTop ? GAMETOPBIAS : 0)));
	}
	private void showPlayer(BasePlayer curP)
	{
		Image pImg = RenderImage.getImage(curP.toString());
		putTexture(pImg, curP.getLeft(), curP.getUp(), false, true);
		if (curP == infoPlayer)
			putTexture(RenderImage.getImage("infoarrow"), curP.getLeft() + (PLAYER_UNIT - 10) / 2, curP.getUp(), true, true);
		if (curP.isInvincible())
		{
			pImg = RenderImage.getImage(curP.toString());
			putTexture(RenderImage.getImage("invincible"), curP.getLeft(), curP.getUp(), false, true);
			BufferedImage sImg = new BufferedImage(HPWIDTH, HPHEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D sg = sImg.createGraphics();
			sImg = sg.getDeviceConfiguration().createCompatibleImage(HPWIDTH, HPHEIGHT, Transparency.TRANSLUCENT);
			sg = sImg.createGraphics();
			sg.setFont(HPFont);
			sg.setColor(Color.BLUE);
			sg.drawString("HP-" + curP.getDmg(), 0, HPHEIGHT - 1);
			putTexture(sImg, curP.getLeft() - (HPWIDTH - PLAYER_UNIT) / 2, curP.getUp(), true, true);
		}
	}

	public void updateRender()
	{
		infoPlayer = mainWindow.getGame().getInfoPlayer();
		synchronized (drawList) {
			drawList.clear();
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
					showPlayer(curP);
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
			while (curP != null) {showPlayer(curP); curP = pIt.hasNext() ? pIt.next() : null;}
		}
	}
}
