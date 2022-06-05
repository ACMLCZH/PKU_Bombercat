package render;
import javax.swing.*;
// import java.awt.*;

// import Map;
// import BaseObject.Bomb;
// import BasePlayer.BasePlayer;
// import BaseObject.GameMap;
// import GameMap.MapElement;
import GUI.*;
import main.Game;
import static GUI.MyPanel.*;

public class MainRenderer extends JFrame
{
	public static final int BLOCK_UNIT = 40;
	public static final int WINWIDTH = SCENEWIDTH + 14;
	public static final int WINHEIGHT = SCENEHEIGHT + 37;

	private GamePane gameScene = new GamePane(this);
	private TitlePanel titleScene = new TitlePanel(this);
	private InfoPanel infoScene = new InfoPanel(this);
	private SelectPanel selectScene = new SelectPanel(this);

	private Game game = null;
	
	public GamePane getGameScene() {return this.gameScene;}
	public TitlePanel getTitleScene() {return this.titleScene;}
	public InfoPanel getInfoScene() {return this.infoScene;}
	public SelectPanel getSelectPanel() {return this.selectScene;}
	public Game getGame() {return game;}

	public void updateRender() {gameScene.updateRender();}
	// public void render(GameMap mp, BasePlayer[] ps, Bomb[] bs, BasePlayer infoPlayer)
	// {
	// }

	// private void addPanel(JPanel scene, Component[] comps, boolean vis)
	// {
	// 	scene.setVisible(vis);
	// 	scene.setLayout(null);
	// 	scene.setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
	// 	for (Component comp: comps) scene.add(comp);
	// 	getContentPane().add(scene);
	// }
	// private void setButton(JButton btn, int x, int y, int len, String iconName)
	// {
	// 	ImageIcon icon = RenderImage.getIcon(iconName);
	// 	icon.setImage(icon.getImage().getScaledInstance(len, len, Image.SCALE_DEFAULT));
	// 	btn.setIcon(icon);
	// 	btn.setMargin(new Insets(0, 0, 0, 0));	// 将边框外的上下左右空间设置为0
	// 	btn.setIconTextGap(0);		// 将标签中显示的文本和图标之间的间隔量设置为0
	// 	btn.setBorderPainted(false);			// 不打印边框
	// 	btn.setBorder(null);			// 除去边框
	// 	btn.setFocusPainted(false);			// 除去焦点的框
	// 	btn.setContentAreaFilled(false);		// 除去默认的背景填充
	// 	btn.setBounds(x, y, len, len);
	// }

	// private void gameEnd()
	// {
	// 	SwingUtilities.invokeLater(() -> {
	// 		gameScene.setVisible(false);
	// 		titleScene.setVisible(true);
	// 	});
	// }
	
	public MainRenderer(Game game)
	{
		this.game = game;
		SwingUtilities.invokeLater(() -> {
			setTitle("PKU 泡泡堂");
			setLayout(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setResizable(false);
			setBounds(100, 100, WINWIDTH, WINHEIGHT);
			setFocusable(true);

			titleScene.toLayout();
			// System.out.print("\n");
			gameScene.toLayout();
			// System.out.print("\n");
			infoScene.toLayout();
			// System.out.print("\n");
			selectScene.toLayout();
			setVisible(true);
		});
	}
}