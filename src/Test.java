// package GUI;

import main.Game;
import render.MainRenderer;
import render.RenderImage;
import static render.MainRenderer.*;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

class SPanel extends GUI.MyPanel
{
	JFrame jf = null;
	private static final Map<String, String> toScene = new HashMap<>() {
		{put("草地", "forest"); put("雪地", "snowfield");}
	};
	private static final Map<String, Integer> toMode = new HashMap<>() {
		{put("道具模式", Game.PVP); put("挑战模式", Game.PVE);}
	};

	private JRadioButton btnChar1 = new JRadioButton("碎月");
	private JRadioButton btnChar2 = new JRadioButton("仙草");
	private JRadioButton btnChar3 = new JRadioButton("薏米");
	private JRadioButton btnScene1 = new JRadioButton("草地");
	private JRadioButton btnScene2 = new JRadioButton("雪地");
	private JRadioButton btnMode1 = new JRadioButton("道具模式");
	private JRadioButton btnMode2 = new JRadioButton("挑战模式");
	private JButton btnOK = new JButton();
	private ButtonGroup btnGrpChar = new ButtonGroup() {{add(btnChar1); add(btnChar2); add(btnChar3);}};
	private ButtonGroup btnGrpScene = new ButtonGroup() {{add(btnScene1); add(btnScene2);}};
	private ButtonGroup btnGrpMode = new ButtonGroup() {{add(btnMode1); add(btnMode2);}};
	// private ButtonGroup btnGrpChar = 
	// private ButtonGroup btnGrpScene = 

	public SPanel(JFrame jf) {super(null); this.jf = jf;}

	private JPanel wrapButton(JRadioButton btn, String name)
	{
		btn.setVisible(true);
		ImageIcon icon = RenderImage.getIcon(name);
		icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
		JLabel lbl = new JLabel(icon);
		lbl.setSize(icon.getIconWidth(), icon.getIconHeight());
		JPanel pn = new JPanel();
		pn.setLayout(new BorderLayout());
		pn.add(lbl, BorderLayout.CENTER);
		pn.add(btn, BorderLayout.SOUTH);
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
	private JPanel setSelectLine(String title, Component[] comps)
	{
		JLabel lbl = new JLabel(title);
		lbl.setFont(new Font("黑体", Font.PLAIN, 22));
		JPanel pnSel = setFlowPanel(comps);
		JPanel pnLine = new JPanel();
		pnLine.setLayout(new BorderLayout());
		pnLine.add(lbl, BorderLayout.WEST);
		pnLine.add(pnSel, BorderLayout.CENTER);
		pnLine.setVisible(true);
		return pnLine;
	}

	protected void addPanel(Component[] comps, boolean vis)
	{
		setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		for (Component comp: comps) add(comp);
		jf.getContentPane().add(this);
		setVisible(vis);
	}

	public void toLayout()
	{
		btnChar1.setSelected(true);
		btnScene1.setSelected(true);
		JPanel line1 = setSelectLine("人物选择", new Component[]{
			wrapButton(btnChar1, "碎月_left"),
			wrapButton(btnChar2, "仙草_left"),
			wrapButton(btnChar3, "薏米_left")
		});
		JPanel line2 = setSelectLine("场景选择", new Component[]{btnScene1, btnScene2});
		JPanel line3 = setSelectLine("模式选择", new Component[]{btnMode1, btnMode2});
		setButton(btnOK, 80, "icon_start");
		btnOK.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				setVisible(false);
				String selChar = ((JRadioButton)btnGrpChar.getSelection()).getText();
				String selScene = toScene.get(((JRadioButton)btnGrpScene.getSelection()).getText());
				int selMode = toMode.get(((JRadioButton)btnGrpMode.getSelection()).getText());
				mainWindow.getGameScene().setVisible(true);
				mainWindow.getGame().commandQueue.add(() -> {
					mainWindow.getGame().start(selChar, selScene, selMode);
					mainWindow.getGameScene().readyAnimation();
				});
			});
		});
		JPanel line4 = setFlowPanel(new Component[]{btnOK});
		setLayout(new GridLayout(4, 2));
		// setBackground(Color.blue);
		// System.out.println("OK!!!!!!!!!!!!!!!");
		// addPanel(new Component[]{btnChar1}, true);
		addPanel(new Component[]{line1, line2, line3, line4}, true);
	}
}

public class Test extends JFrame
{
	SPanel sp = new SPanel(this);
	public Test()
	{
		setTitle("泡泡堂 in PKU");
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, WINWIDTH, WINHEIGHT);
		SwingUtilities.invokeLater(() -> {
			sp.toLayout();
		});
		setVisible(true);
	}
	public static void main(String[] args)
	{
		new Test();
		ImageIcon ic = new ImageIcon("./res/texture/icon/start.png");
		
	}
}
