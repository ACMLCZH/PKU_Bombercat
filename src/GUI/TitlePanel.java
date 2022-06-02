package GUI;

import render.MainRenderer;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends MyPanel
{
	// private JPanel titleScene = new JPanel();
	private JLabel lblTitle = new JLabel("");
	// private JPanel gameScene = null;
	// private ArrayList<String> infoText = new ArrayList<>();
	private JButton btnInfo = new JButton();
	private JButton btnStart = new JButton();
	private JButton btnQuit = new JButton();
	public TitlePanel(MainRenderer mainWindow) {super(mainWindow);}
	public void toLayout() // Game g, GamePanel gameScene, InfoPanel infoScene
	{
		lblTitle.setBounds(100, 100, 100, 100);
		setButton(btnInfo, 50, 400, 80, "icon_start");
		btnStart.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				// mainWindow.getGameScene().setVisible(true);
				// Game g = new Game(this);
				mainWindow.getSelectPanel().setVisible(true);
			});
		});
		setButton(btnInfo, 150, 400, 80, "icon_info");
		btnInfo.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				mainWindow.getInfoScene().infoInitial();
				mainWindow.getInfoScene().setVisible(true);
			});
		});
		setButton(btnQuit, 250, 400, 80, "icon_quit");
		btnQuit.addActionListener((e) -> {System.exit(0);});
		addPanel(new Component[]{lblTitle, btnInfo, btnStart, btnQuit}, true);
	}	
}
