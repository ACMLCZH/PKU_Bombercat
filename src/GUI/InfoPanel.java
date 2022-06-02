package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class InfoPanel extends MyPanel		/* 说明文档 */
{
	static final int INFOPERPAGE = 10;
	// private JPanel infoScene = new JPanel();
	private JButton btnInfoLeft = new JButton();
	private JButton btnInfoRight = new JButton();
	private JButton btnInfoBack = new JButton();
	private JTextArea txtInfo = new JTextArea();
	private int curInfoPage = 0;
	private int infoPages = 0;
	private int infoNumLine = 0;
	private ArrayList<StringBuffer> infoText = new ArrayList<>();

	public InfoPanel(JFrame mainWindow) {super(mainWindow);}
	public void infoShow() {txtInfo.setText(infoText.get(curInfoPage).toString());}
	public void infoInitial() {curInfoPage = 0; infoShow();}
	public void toLayout(TitlePanel titleScene) throws IOException
	{
		BufferedReader infoInput = new BufferedReader(new FileReader("./res/text/info.txt"));
		String line = null;
		while ((line = infoInput.readLine()) != null)
		{
			if (infoNumLine == 0) {++infoPages; infoText.add(new StringBuffer(""));}
			infoText.get(infoPages - 1).append(line + "\n");
			if (++infoNumLine == INFOPERPAGE) infoNumLine = 0;
		}
		infoInput.close();
		txtInfo.setBounds(50, 50, 500, 300);
		txtInfo.setEditable(false);
		txtInfo.setBackground(new Color(0, 0, 0, 0));
		setButton(btnInfoLeft, 400, 400, 50, "icon_left");
		btnInfoLeft.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				if (curInfoPage > 0) {--curInfoPage; infoShow();}
			});
		});
		setButton(btnInfoRight, 500, 400, 50, "icon_right");
		btnInfoRight.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				if (curInfoPage < infoPages - 1) {++curInfoPage; infoShow();}
			});
		});
		setButton(btnInfoRight, 450, 400, 50, "icon_quit");
		btnInfoBack.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				titleScene.setVisible(true);
			});
		});
		addPanel(new Component[]{btnInfoLeft, btnInfoRight, btnInfoBack, txtInfo}, false);
	}
}
