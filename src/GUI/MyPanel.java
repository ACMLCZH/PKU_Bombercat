package GUI;

import render.RenderImage;
import render.MainRenderer;
import static GUI.GamePanel.*;
// import static GUI.GamePanel.GAMEHEIGHT;

import javax.swing.*;
import java.awt.*;

public abstract class MyPanel extends JPanel
{
	static final int SCENEWIDTH = GAMEWIDTH;
	static final int SCENEHEIGHT = GAMEHEIGHT + GAMETOPBIAS;
	protected MainRenderer mainWindow;
	
	public MyPanel(MainRenderer mainWindow) {super(); this.mainWindow = mainWindow;}
	protected void addPanel(Component[] comps, boolean vis)
	{
		setVisible(vis);
		setLayout(null);
		setBounds(0, 0, SCENEWIDTH, SCENEHEIGHT);
		for (Component comp: comps) add(comp);
		this.mainWindow.getContentPane().add(this);
	}
	protected static void setButton(JButton btn, String iconName)
	{
		ImageIcon icon = RenderImage.getIcon(iconName);
		icon.setImage(icon.getImage().getScaledInstance(btn.getHeight(), btn.getWidth(), Image.SCALE_DEFAULT));
		btn.setIcon(icon);
	}
	protected static void setButton(JButton btn, int len, String iconName)
	{
		btn.setSize(len, len);
		setButton(btn, iconName);
		btn.setMargin(new Insets(0, 0, 0, 0));	// 将边框外的上下左右空间设置为0
		btn.setIconTextGap(0);		// 将标签中显示的文本和图标之间的间隔量设置为0
		btn.setBorderPainted(false);			// 不打印边框
		btn.setBorder(null);			// 除去边框
		btn.setFocusPainted(false);			// 除去焦点的框
		btn.setContentAreaFilled(false);		// 除去默认的背景填充
	}
	protected static void setButton(JButton btn, int x, int y, int len, String iconName)
	{
		setButton(btn, len, iconName);
		btn.setLocation(x, y);
	}
	public abstract void toLayout();
}
