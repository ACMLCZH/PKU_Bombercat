package GUI;

import main.Game;
import render.MainRenderer;
import render.RenderImage;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

class MyRadioButton extends JRadioButton
{
	private static final Font selectFont = new Font("黑体", Font.PLAIN, 16);
	public MyRadioButton(String text)
	{
		super(text);
		setActionCommand(text);
		setFont(selectFont);
		setOpaque(false);
		setHorizontalAlignment(SwingConstants.CENTER);
	}
}

public class SelectPanel extends MyPanel
{
	private static final Map<String, String> toScene = new HashMap<>() {
		{put("草地", "forest"); put("雪地", "snowfield");}
	};
	private static final Map<String, Integer> toMode = new HashMap<>() {
		{put("道具模式", Game.PVP); put("挑战模式", Game.PVE);}
	};

	private MyRadioButton btnChar1 = new MyRadioButton("碎月");
	private MyRadioButton btnChar2 = new MyRadioButton("仙草");
	private MyRadioButton btnChar3 = new MyRadioButton("薏米");
	private MyRadioButton btnScene1 = new MyRadioButton("草地");
	private MyRadioButton btnScene2 = new MyRadioButton("雪地");
	private MyRadioButton btnMode1 = new MyRadioButton("道具模式");
	private MyRadioButton btnMode2 = new MyRadioButton("挑战模式");
	private JButton btnOK = new JButton();
	private ButtonGroup btnGrpChar = new ButtonGroup() {{add(btnChar1); add(btnChar2); add(btnChar3);}};
	private ButtonGroup btnGrpScene = new ButtonGroup() {{add(btnScene1); add(btnScene2);}};
	private ButtonGroup btnGrpMode = new ButtonGroup() {{add(btnMode1); add(btnMode2);}};
	private static final Font titleFont = new Font("黑体", Font.PLAIN, 22);
	// private ButtonGroup btnGrpChar = 
	// private ButtonGroup btnGrpScene = 

	public SelectPanel(MainRenderer mainWindow) {super(mainWindow);}

	private JPanel wrapButton(JRadioButton btn, int len, String name)
	{
		ImageIcon icon = new ImageIcon();
		icon.setImage(RenderImage.getImage(name).getScaledInstance(len, len, Image.SCALE_DEFAULT));
		JLabel lbl = new JLabel(icon);
		lbl.setSize(icon.getIconWidth(), icon.getIconHeight());
		// System.out.println(icon.getIconWidth() + " " + icon.getIconHeight());
		JPanel pn = new JPanel();
		pn.setLayout(new BorderLayout());
		pn.add(lbl, BorderLayout.CENTER);
		pn.add(btn, BorderLayout.SOUTH);
		pn.setOpaque(false);
		return pn;
	}
	private JPanel setFlowPanel(Component[] comps)
	{
		JPanel pn = new JPanel();
		pn.setLayout(new FlowLayout());
		for (Component c: comps) pn.add(c);
		pn.setVisible(true);
		return pn;
	}
	private JPanel setRowPanel(Component[] comps)
	{
		JPanel pn = new JPanel();
		pn.setLayout(new GridLayout(1, comps.length));
		for (Component c: comps) pn.add(c);
		pn.setOpaque(false);
		return pn;
	}
	private JPanel setSelectLine(String title, Component[] comps)
	{
		JLabel lbl = new JLabel(title);
		lbl.setFont(titleFont);
		JPanel pnSel = setRowPanel(comps);
		JPanel pnLine = new JPanel();
		pnLine.setLayout(new BorderLayout());
		pnLine.add(lbl, BorderLayout.WEST);
		pnLine.add(pnSel, BorderLayout.CENTER);
		pnLine.setVisible(true);
		pnLine.setOpaque(false);
		return pnLine;
	}

	public void toLayout()
	{
		btnChar1.setSelected(true);
		btnScene1.setSelected(true);
		btnMode1.setSelected(true);
		JPanel line1 = setSelectLine("人物选择", new Component[]{
			wrapButton(btnChar1, 80, "碎月_left"),
			wrapButton(btnChar2, 80, "仙草_left"),
			wrapButton(btnChar3, 80, "薏米_left")
		});
		JPanel line2 = setSelectLine("场景选择", new Component[]{btnScene1, btnScene2});
		JPanel line3 = setSelectLine("模式选择", new Component[]{btnMode1, btnMode2});
		setButton(btnOK, 100, "icon_start");
		btnOK.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				String selChar = btnGrpChar.getSelection().getActionCommand();
				String selScene = toScene.get(btnGrpScene.getSelection().getActionCommand());
				int selMode = toMode.get(btnGrpMode.getSelection().getActionCommand());
				mainWindow.getGameScene().setVisible(true);
				mainWindow.getGame().commandQueue.add(() -> {
					mainWindow.getGame().start(selChar, selScene, selMode);
					mainWindow.getGameScene().readyAnimation();
				});
			});
		});
		JPanel line4 = setRowPanel(new Component[]{btnOK});
		// setBackground(Color.red);
		// System.out.println("OK!!!!!!!!!!!!!!!");
		setBorder(BorderFactory.createEmptyBorder(50,50,50,50));
		addPanel(new Component[]{line1, line2, line3, line4}, false, new GridLayout(4, 1));
	}
}
