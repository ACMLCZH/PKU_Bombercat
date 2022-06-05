package render;

import static DEBUG.Dbg.msg;

import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import java.awt.Image;

public class RenderImage
{
	private static final String sceneDir = "./res/texture/scene/";
	private static final String mapDir = "./res/texture/map/";
	private static final String charDir = "./res/texture/character/";
	private static final String iconDir = "./res/texture/icon/";
	private static final Map<String, ImageIcon> toIcon = new HashMap<>(){{
		put("bomb_1", new ImageIcon(sceneDir + "bomb1.png"));
		put("bomb_2", new ImageIcon(sceneDir + "bomb2.png"));
		put("bomb_3", new ImageIcon(sceneDir + "bomb3.png"));
		put("bomb_4", new ImageIcon(sceneDir + "bomb2.png"));
		put("bomb_5", new ImageIcon(sceneDir + ".bomb1.png"));
		put("horiflow", new ImageIcon(sceneDir + "horiflow.png"));
		put("vertflow", new ImageIcon(sceneDir + "vertflow.png"));
		put("crossflow", new ImageIcon(sceneDir + "crossflow.png"));
		put("bombitem", new ImageIcon(sceneDir + "bombitem.png"));
		put("flowitem", new ImageIcon(sceneDir + "flowitem.png"));
		put("speeditem", new ImageIcon(sceneDir + "speeditem.png"));
		put("infoarrow", new ImageIcon(sceneDir + "infoarrow.png"));
		put("invincible", new ImageIcon(sceneDir + "invincible.png"));

		put("碎月_up", new ImageIcon(charDir + "char1_up.png"));
		put("碎月_down", new ImageIcon(charDir + "char1_down.png"));
		put("碎月_left", new ImageIcon(charDir + "char1_left.png"));
		put("碎月_right", new ImageIcon(charDir + "char1_right.png"));
		put("仙草_up", new ImageIcon(charDir + "char2_up.png"));
		put("仙草_down", new ImageIcon(charDir + "char2_down.png"));
		put("仙草_left", new ImageIcon(charDir + "char2_left.png"));
		put("仙草_right", new ImageIcon(charDir + "char2_right.png"));
		put("薏米_up", new ImageIcon(charDir + "char3_up.png"));
		put("薏米_down", new ImageIcon(charDir + "char3_down.png"));
		put("薏米_left", new ImageIcon(charDir + "char3_left.png"));
		put("薏米_right", new ImageIcon(charDir + "char3_right.png"));
		put("enemy1_up", new ImageIcon(charDir + "enemy1_up.png"));
		put("enemy1_down", new ImageIcon(charDir + "enemy1_down.png"));
		put("enemy1_left", new ImageIcon(charDir + "enemy1_left.png"));
		put("enemy1_right", new ImageIcon(charDir + "enemy1_right.png"));

		put("background", new ImageIcon(mapDir + "background.png"));
		put("forest_ground", new ImageIcon(mapDir + "forest_ground.png"));
		put("forest_unbreakable", new ImageIcon(mapDir + "forest_unbreakable.png"));
		put("forest_destroyable", new ImageIcon(mapDir + "forest_destroyable.png"));
		put("snowfield_ground", new ImageIcon(mapDir + "snowfield_ground.png"));
		put("snowfield_unbreakable", new ImageIcon(mapDir + "snowfield_unbreakable.png"));
		put("snowfield_destroyable", new ImageIcon(mapDir + "snowfield_destroyable.png"));

		put("window_background", new ImageIcon(iconDir + "title.png"));
		put("icon_start", new ImageIcon(iconDir + "start.png"));
		put("icon_info", new ImageIcon(iconDir + "info.png"));
		put("icon_quit", new ImageIcon(iconDir + "quit.png"));
		put("icon_left", new ImageIcon(iconDir + "left.png"));
		put("icon_right", new ImageIcon(iconDir + "right.png"));
		put("icon_sound_on", new ImageIcon(iconDir + "sound_on.png"));
		put("icon_sound_off", new ImageIcon(iconDir + "sound_off.png"));
	}};
	public static Image getImage(String name) {return toIcon.get(name).getImage();}
	public static ImageIcon getIcon(String name) {return toIcon.get(name);}
}