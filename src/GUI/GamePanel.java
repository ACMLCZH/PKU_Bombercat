package GUI;

import main.Game;
import BaseObject.GameMap;
import static render.Renderer.BLOCK_UNIT;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends MyPanel
{
	public static final int GAMEWIDTH = GameMap.WIDTH * BLOCK_UNIT;
	public static final int GAMEHEIGHT = GameMap.HEIGHT * BLOCK_UNIT;
	// private JPanel gameScene = new JPanel();
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	public GamePanel(JFrame mainWindow) {super(mainWindow);}
	
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
}
