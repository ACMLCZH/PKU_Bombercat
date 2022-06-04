package GUI;

import javax.swing.*;

import render.MainRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class InfoPanel extends MyPanel		/* 说明文档 */
{
	public static final int INFOPERPAGE = 20;
	public static final Font infoFont = new Font("幼圆", Font.PLAIN, 16);
	// private JPanel infoScene = new JPanel();
	private JButton btnInfoLeft = new JButton();
	private JButton btnInfoRight = new JButton();
	private JButton btnInfoBack = new JButton();
	private JTextArea txtInfo = new JTextArea();
	private int curInfoPage = 0;
	private int infoPages = 0;
	private int infoNumLine = 0;
	private ArrayList<StringBuffer> infoText = new ArrayList<>();

	public InfoPanel(MainRenderer mainWindow) {super(mainWindow);}
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		txtInfo.setText(infoText.get(curInfoPage).toString());
	}
	// public void infoShow() {}
	public void infoInitial() {curInfoPage = 0;}
	public void toLayout()
	{
		BufferedReader infoInput;
		try {
			infoInput = new BufferedReader(new InputStreamReader(new FileInputStream("./res/text/info.txt"), "UTF-8"));
			String line = null;
			while ((line = infoInput.readLine()) != null)
			{
				// System.out.print(line + "\n");
				if (infoNumLine == 0) {++infoPages; infoText.add(new StringBuffer(""));}
				infoText.get(infoPages - 1).append(line + "\n");
				if (++infoNumLine == INFOPERPAGE) infoNumLine = 0;
			}
			infoInput.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		txtInfo.setBounds(50, 50, SCENEWIDTH - 100, 400);
		txtInfo.setFont(infoFont);
		txtInfo.setEditable(false);
		txtInfo.setBackground(new Color(0, 0, 0, 0));
		setButton(btnInfoLeft, 400, 500, 50, "icon_left");
		btnInfoLeft.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				if (curInfoPage > 0) {--curInfoPage; repaint();} //infoShow();}
			});
		});
		setButton(btnInfoRight, 500, 500, 50, "icon_right");
		btnInfoRight.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				if (curInfoPage < infoPages - 1) {++curInfoPage; repaint();}
			});
		});
		setButton(btnInfoBack, 450, 500, 50, "icon_quit");
		btnInfoBack.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				mainWindow.getTitleScene().setVisible(true);
			});
		});
		addPanel(new Component[]{btnInfoLeft, btnInfoRight, btnInfoBack, txtInfo}, false, null);
	}
}
