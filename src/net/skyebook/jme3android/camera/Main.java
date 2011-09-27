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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;

public class Main extends AndroidHarness {
	private Camera mCamera;

	public Main(){
		appClass = "net.skyebook.jme3android.camera.Game";
		eglConfigType = ConfigType.BEST;
		exitDialogTitle = "Exit?";
		exitDialogMessage = "Press Yes";
		eglConfigVerboseLogging=false;
		screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		mouseEventsInvertX=true;
		mouseEventsInvertY=true;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		// Open the default i.e. the first rear facing camera.
		mCamera = Camera.open();
		
		// What camera formats are supported?
		for(Integer format : mCamera.getParameters().getSupportedPreviewFormats()){
			System.out.println("ImageFormat: " + format);
		}
		
		
		// What camera preview resolutions are supported?
		for(Size size : mCamera.getParameters().getSupportedPreviewSizes()){
			System.out.println("PreviewSize: " + size.width+","+size.height);
		}
		
		// camera parameters need to be reset before they will take effect
		Parameters params = mCamera.getParameters();
		//params.setPreviewFormat(ImageFormat.RGB_565);
		params.setPreviewSize(480, 320);
		mCamera.setParameters(params);
		
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				
				int width = camera.getParameters().getPreviewSize().width;
				int height = camera.getParameters().getPreviewSize().height;
				
				YuvImage yuv = new YuvImage(data, camera.getParameters().getPreviewFormat(), width, height, null);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Log.e("JME3", "ByteArrayOutputStream created");
				yuv.compressToJpeg(new Rect(0, 0, width, height), 50, bos);
				
				
				
				getGame().setTexture(camera.getParameters().getPreviewFormat(), width, height, bos.toByteArray());
				//Log.e("JME3", "Updating jME Texture");
				
				//Log.e("JME3", "Image compressed to " + bos.toByteArray().length + " bytes");

			}
		});
		Log.e("JME3", "Preview Callback Added");
		
		// "set" the preview display
		try {
			mCamera.setPreviewDisplay(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mCamera.startPreview();
		
		
		Log.e("JME3", "On Create Finished");
	}


	@Override
	protected void onResume() {
		super.onResume();

		// Open the default i.e. the first rear facing camera.
		//mCamera = Camera.open();
		//cameraCurrentlyLocked = defaultCameraId;
		//mPreview = new Preview(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Because the Camera object is a shared resource, it's very
		// important to release it when the activity is paused.
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private Game getGame(){
		return (Game)getJmeApplication();
	}
}