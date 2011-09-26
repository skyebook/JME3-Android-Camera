/**
 * 
 */
package net.skyebook.jme3android.camera;

import java.nio.ByteBuffer;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

/**
 * @author Skye Book
 *
 */
public class Game extends SimpleApplication {
	
	private Box box;
	private Material material;

	/**
	 * 
	 */
	public Game() {
		
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		box = new Box(Vector3f.ZERO, 1, 1, 1);
		Geometry geometry = new Geometry("box", box);
		
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", ColorRGBA.Blue);
		geometry.setMaterial(material);
		
		rootNode.attachChild(geometry);
	}
	
	public void setTexture(int format, int width, int height, ByteBuffer data){
		Image image = new Image(Image.Format.RGB8, width, height, data);
		Texture2D texture = new Texture2D(image);
		material.setTexture("ColorMap", texture);
	}

}
