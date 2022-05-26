import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

class Map implements Serializable {
	private int[][] mp;
}

public class Renderer {
	private String mapType = null;
	private int mapID = -1;
	private Map map = null;
	private Image ground = null;
	private Image destroyable = null;
	private Image unbreakable = null;
	
	public Renderer() {}
	
	public void loadMap(String type, int id) {
		try {
			ObjectInputStream objInput = new ObjectInputStream(new FileInputStream("./res/map/" + type + "_" + id + ".txt"));
			map = (Map)objInput.readObject();
			objInput.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		this.ground = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_ground.png");
		this.destroyable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_destroyable.png");
		this.unbreakable = Toolkit.getDefaultToolkit().getImage("./res/texture/" + type + "_unbreakable.png");
	}

	public void render() {
		
	}
}