package net.skyebook.jme3android.camera;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnCreateContextMenuListener;

public class Main extends AndroidHarness {
	private Preview mPreview;
	Camera mCamera;
	int numberOfCameras;
	int cameraCurrentlyLocked;

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
		cameraCurrentlyLocked = defaultCameraId;
		mPreview = new Preview(this);
		Log.e("JME3", "On Create Finished");
	}


	// The first rear facing camera
	int defaultCameraId;

	@Override
	protected void onResume() {
		super.onResume();

		// Open the default i.e. the first rear facing camera.
		//mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mPreview = new Preview(this);
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





	class Preview extends SurfaceView implements SurfaceHolder.Callback {
		SurfaceHolder mHolder;
		Camera mCamera;

		Preview(Context context) {
			super(context);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, acquire the camera and tell it where
			// to draw.
			mCamera = Camera.open();
			try {
				mCamera.setPreviewDisplay(holder);



				// Get the buffer
				mCamera.setPreviewCallback(new PreviewCallback() {

					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {

						ByteBuffer tData = ByteBuffer.allocateDirect(data.length);
						tData.put(data);
						int width = camera.getParameters().getPictureSize().width;
						int height = camera.getParameters().getPictureSize().height;
						Log.e("JME3", "Updating jME Texture");
						getGame().setTexture(camera.getParameters().getPreviewFormat(), width, height, tData);
						Log.e("JME3", "Updating jME Texture");

					}
				});

				Log.e("JME3", "Preview Callback Added");


			} catch (IOException exception) {
				mCamera.release();
				mCamera = null;
				// TODO: add more exception handling logic here
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// Surface will be destroyed when we return, so stop the preview.
			// Because the CameraDevice object is not a shared resource, it's very
			// important to release it when the activity is paused.
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			// Now that the size is known, set up the camera parameters and begin
			// the preview.
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(w, h);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}

	}
}