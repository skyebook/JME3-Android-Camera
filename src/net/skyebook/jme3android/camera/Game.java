/**  
 *  jME3 Android Camera - A demo for integrating the Android camera with jMonkeyEngine
 *  Copyright (C) 2011 Skye Book
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.skyebook.jme3android.camera;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AndroidImageLoader;

/**
 * @author Skye Book
 *
 */
public class Game extends SimpleApplication {
	
	private Box box;
	private Material material;
	private boolean sceneInitialized = false;
	private AndroidImageLoader ail;
	private Texture2D cameraTexture;

	/**
	 * 
	 */
	public Game() {
		ail = new AndroidImageLoader();
	}

	/* (non-Javadoc)
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		box = new Box(Vector3f.ZERO, 3, 2, 1);
		Geometry geometry = new Geometry("box", box);
		
		material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		//material.setColor("Color", ColorRGBA.Blue);
		geometry.setMaterial(material);
		
		rootNode.attachChild(geometry);
		
		sceneInitialized = true;
		
		cameraTexture = new Texture2D();
	}
	
	public void setTexture(int format, int width, int height, byte[] data){
		
		// Only proceed if the scene has already been setup
		if(!sceneInitialized) return;
		
		try {
			Image image = (Image)ail.load(new ByteArrayInfo(data));
			cameraTexture.setImage(image);
		} catch (IOException e) {
			System.out.println("IMAGE LOAD FAILED");
		}
		
		material.setTexture("ColorMap", cameraTexture);
	}
	
	public void setTexture(int format, int width, int height, ByteBuffer data){
		
		// Only proceed if the scene has already been setup
		if(!sceneInitialized) return;
		
		Texture t = assetManager.loadTexture("assets/test.jpg");
		material.setTexture("ColorMap", t);
		
		
		//Image image = new Image(Image.Format.RGB565, width, height, data);
		
		//System.out.println("---------------------Preview Format: " + format);
		
		//Texture2D texture = new Texture2D(image);
		//material.setTexture("ColorMap", texture);
	}
	
	private class ByteArrayInfo extends AssetInfo{
		
		private byte[] data;

		/**
		 * @param manager
		 * @param key
		 */
		public ByteArrayInfo(byte[] data) {
			super(assetManager, new TextureKey("ByteArray", true));
			this.data=data;
		}

		/* (non-Javadoc)
		 * @see com.jme3.asset.AssetInfo#openStream()
		 */
		@Override
		public InputStream openStream() {
			return new ByteArrayInputStream(data);
		}
		
	}

}
