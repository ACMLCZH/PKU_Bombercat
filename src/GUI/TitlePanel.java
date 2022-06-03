package GUI;

import render.MainRenderer;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends MyPanel
{
	// private JPanel titleScene = new JPanel();
	private JLabel lblTitle = new JLabel("PKU 泡泡堂");
	// private JPanel gameScene = null;
	// private ArrayList<String> infoText = new ArrayList<>();
	private JButton btnInfo = new JButton();
	private JButton btnStart = new JButton();
	private JButton btnQuit = new JButton();
	public TitlePanel(MainRenderer mainWindow) {super(mainWindow);}
	public void toLayout() // Game g, GamePanel gameScene, InfoPanel infoScene
	{
		lblTitle.setBounds(50, 100, 500, 100);
		lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 80));
		setButton(btnStart, 50, 400, 100, "icon_start");
		btnStart.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				// mainWindow.getGameScene().setVisible(true);
				// Game g = new Game(this);
				mainWindow.getSelectPanel().setVisible(true);
			});
		});
		setButton(btnInfo, 170, 400, 100, "icon_info");
		btnInfo.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				mainWindow.getInfoScene().infoInitial();
				mainWindow.getInfoScene().setVisible(true);
			});
		});
		setButton(btnQuit, 290, 400, 100, "icon_quit");
		btnQuit.addActionListener((e) -> {System.exit(0);});
		addPanel(new Component[]{lblTitle, btnInfo, btnStart, btnQuit}, true);
	}	
}
