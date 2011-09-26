package net.skyebook.jme3android.camera;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.jme3.app.AndroidHarness;
import com.jme3.system.android.AndroidConfigChooser.ConfigType;
import com.tomgibara.YUVRGBConverter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Main extends AndroidHarness {
	private Preview mPreview;
	private Camera mCamera;
	private byte[] bytes;
	private ByteBuffer tData;
	

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
		bytes = new byte[((480*320)*2)];
		tData = ByteBuffer.allocateDirect(((480*320)*2));
		mCamera.setParameters(params);
		
		mPreview = new Preview(this);
		//mCamera.setPreviewDisplay(mPreview);
		
		
		//mCamera = Camera.open();
		// Get the buffer
		mCamera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				
				// we're getting the data back in NV21, which is similar to yuv420 (u and v are flipped)
				
				int width = camera.getParameters().getPreviewSize().width;
				int height = camera.getParameters().getPreviewSize().height;
				
				YuvImage yuv = new YuvImage(data, camera.getParameters().getPreviewFormat(), width, height, null);
				//FileOutputStream fos = new FileOutputStream("assets/test.jpg");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Log.e("JME3", "ByteArrayOutputStream created");
				yuv.compressToJpeg(new Rect(0, 0, width, height), 50, bos);
				
				
				
				//YUVRGBConverter.toRGB565(data, width, height, bytes);
				//tData.clear();
				//tData.put(bytes);
				getGame().setTexture(camera.getParameters().getPreviewFormat(), width, height, bos.toByteArray());
				//Log.e("JME3", "Updating jME Texture");
				
				Log.e("JME3", "Image compressed to " + bos.toByteArray().length + " bytes");

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
			
			
			
			mCamera = Camera.open();
			// Get the buffer
			mCamera.setPreviewCallback(new PreviewCallback() {

				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {

					int width = camera.getParameters().getPictureSize().width;
					int height = camera.getParameters().getPictureSize().height;
					Log.e("JME3", "Updating jME Texture");
					YUVRGBConverter.toRGB565(data, width, height, bytes);
					tData.put(bytes);
					getGame().setTexture(camera.getParameters().getPreviewFormat(), width, height, tData);
					Log.e("JME3", "Updating jME Texture");

				}
			});
			Log.e("JME3", "Preview Callback Added");
			
			
			
			
			
			// The Surface has been created, acquire the camera and tell it where
			// to draw.
			/*
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
			*/
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