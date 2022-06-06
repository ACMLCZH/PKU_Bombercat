package GUI;

import static DEBUG.Dbg.msg;

import static GUI.MyPanel.*;
import render.MainRenderer;

import javax.swing.*;
import java.awt.*;

public class GamePane extends JLayeredPane
{
	// private JPanel gameScene = new JPanel();
	private JButton btnBack = new JButton();
	private JButton btnSound = new JButton();
	private GamePanel pnGame = null;
	private JPanel pnAnimate = new JPanel();
	private JLabel lblCountDown = new JLabel("", JLabel.CENTER);
	private Image gameImg = null;
	private long lastRendered = 0;
	// private int tt = 0;
	// private MainRenderer mainWindow;

	public GamePane(MainRenderer mainWindow) {super(); pnGame = new GamePanel(mainWindow);}
	
	@Override
	public void update(Graphics g)
	{
		if (gameImg == null) gameImg = this.createImage(SCENEWIDTH, SCENEHEIGHT);
		// btnBack.setVisible((++ tt) % 2 == 0);
		// btnBack.setVisible(true);
		paint(gameImg.getGraphics());
		g.drawImage(gameImg, 0, 0, this);
	}

	private void modifyCount(String s, boolean rep)
	{
		lblCountDown.setText(s);
		// if (rep) repaint();
	}

	public void readyAnimation()
	{
		try {
			pnAnimate.setVisible(true);
			// msg("?!!");
			Thread.sleep(1000);
			modifyCount("3", true);
			Thread.sleep(1000);
			modifyCount("2", true);
			Thread.sleep(1000);
			modifyCount("1", true);
			Thread.sleep(1000);
			modifyCount("Start!", true);
			Thread.sleep(1000);
			modifyCount("", false);
			pnAnimate.setVisible(false);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	public void successAnimation()
	{
		try {
			pnAnimate.setVisible(true);
			modifyCount("成功通关！", true);
			Thread.sleep(3000);
			modifyCount("", false);
			pnAnimate.setVisible(false);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}	
	public void failAnimation()
	{
		try {
			pnAnimate.setVisible(true);
			modifyCount("通关失败！", true);
			Thread.sleep(3000);
			modifyCount("", false);
			pnAnimate.setVisible(false);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void updateRender() {pnGame.updateRender();}

	public void toLayout()
	{
		pnGame.setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);

		setButton(btnSound, SCENEWIDTH - 110, 5, 50, "icon_sound_on");
		btnSound.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				pnGame.mainWindow.getGame().switchSound();
				if (pnGame.mainWindow.getGame().isSoundOn())
					setButton(btnSound, "icon_sound_on");
				else setButton(btnSound, "icon_sound_off");
			});
		});
		setButton(btnBack, SCENEWIDTH - 55, 5, 50, "icon_quit");
		btnBack.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				pnGame.mainWindow.getGame().commandQueue.add(() -> {
					pnGame.mainWindow.getGame().end();
					setVisible(false);
					pnGame.mainWindow.getTitleScene().setVisible(true);
				});
			});
		});

		lblCountDown.setForeground(new Color(255, 165, 0));
		lblCountDown.setFont(new Font("幼圆", Font.BOLD, 80));
		lblCountDown.setSize(400, 200);
		// pnAnimate.setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		pnAnimate.setBounds(0, GamePanel.GAMETOPBIAS, GamePanel.GAMEWIDTH, GamePanel.GAMEHEIGHT);
		pnAnimate.setVisible(false);
		// pnAnimate.setOpaque(false);
		pnAnimate.setBackground(new Color(150, 227, 255));
		pnAnimate.setLayout(new BorderLayout());
		pnAnimate.add(BorderLayout.CENTER, lblCountDown);

		setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		btnSound.setVisible(false);
		add(pnGame, 1);
		add(btnSound, 6);
		add(btnBack, 6);
		add(pnAnimate, 12);
		pnGame.mainWindow.getContentPane().add(this);
		setVisible(false);
	}
}
