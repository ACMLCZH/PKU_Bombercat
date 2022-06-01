import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import java.awt.Image;

public class RenderID
{
	public static final String sceneDir = "./res/texture/scene/";
	public static final String mapDir = "./res/texture/map/";
	public static final String charDir = "./res/texture/character/";
	public static final Map<String, Image> getID = new HashMap<>(){{
		put("bomb_1", new ImageIcon(sceneDir + "bomb1.png").getImage());
		put("bomb_2", new ImageIcon(sceneDir + "bomb2.png").getImage());
		put("bomb_3", new ImageIcon(sceneDir + "bomb3.png").getImage());
		put("bomb_4", new ImageIcon(sceneDir + "bomb2.png").getImage());
		put("bomb_5", new ImageIcon(sceneDir + ".bomb1.png").getImage());
		put("horiflow", new ImageIcon(sceneDir + "horiflow.png").getImage());
		put("vertflow", new ImageIcon(sceneDir + "vertflow.png").getImage());
		put("crossflow", new ImageIcon(sceneDir + "crossflow.png").getImage());
		put("bombitem", new ImageIcon(sceneDir + "bombitem.png").getImage());
		put("flowitem", new ImageIcon(sceneDir + "flowitem.png").getImage());
		put("speeditem", new ImageIcon(sceneDir + "speeditem.png").getImage());

		put("碎月_up", new ImageIcon(charDir + "char1_up.png").getImage());
		put("碎月_down", new ImageIcon(charDir + "char1_down.png").getImage());
		put("碎月_left", new ImageIcon(charDir + "char1_left.png").getImage());
		put("碎月_right", new ImageIcon(charDir + "char1_right.png").getImage());
		put("仙草_up", new ImageIcon(charDir + "char2_up.png").getImage());
		put("仙草_down", new ImageIcon(charDir + "char2_down.png").getImage());
		put("仙草_left", new ImageIcon(charDir + "char2_left.png").getImage());
		put("仙草_right", new ImageIcon(charDir + "char2_right.png").getImage());
		put("薏米_up", new ImageIcon(charDir + "char3_up.png").getImage());
		put("薏米_down", new ImageIcon(charDir + "char3_down.png").getImage());
		put("薏米_left", new ImageIcon(charDir + "char3_left.png").getImage());
		put("薏米_right", new ImageIcon(charDir + "char3_right.png").getImage());
		put("enemy1_up", new ImageIcon(charDir + "enemy1_up.png").getImage());
		put("enemy1_down", new ImageIcon(charDir + "enemy1_down.png").getImage());
		put("enemy1_left", new ImageIcon(charDir + "enemy1_left.png").getImage());
		put("enemy1_right", new ImageIcon(charDir + "enemy1_right.png").getImage());

		put("forest_ground", new ImageIcon(mapDir + "forest_ground.png").getImage());
		put("forest_unbreakable", new ImageIcon(mapDir + "forest_unbreakable.png").getImage());
		put("forest_destroyable", new ImageIcon(mapDir + "forest_destroyable.png").getImage());
	}};
}