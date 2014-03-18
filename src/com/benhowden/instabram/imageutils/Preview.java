package com.benhowden.instabram.imageutils;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Preview extends SurfaceView implements SurfaceHolder.Callback, PictureCallback  {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private RawCallback mRawCallback;

    public Preview(Context context) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mRawCallback = new RawCallback();
            
        setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				mCamera.takePicture(mRawCallback, mRawCallback, null, 
                        Preview.this);
			}
        });
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
        
        configure(mCamera);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            closeCamera();
        }
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width, height);
        mCamera.setParameters(parameters);
                
        mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		  closeCamera();
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
	
	private void configure(Camera camera) {
        Camera.Parameters params = camera.getParameters();

        // Configure image format. RGB_565 is the most common format.
        List<Integer> formats = params.getSupportedPictureFormats();
        if (formats.contains(PixelFormat.RGB_565))
            params.setPictureFormat(PixelFormat.RGB_565);
        else
            params.setPictureFormat(PixelFormat.RGB_565);

        // Choose the biggest picture size supported by the hardware
        List<Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(sizes.size()-1);
        params.setPictureSize(size.width, size.height);

        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes.size() > 0)
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

        // Action mode take pictures of fast moving objects
        List<String> sceneModes = params.getSupportedSceneModes();
        if (sceneModes.contains(Camera.Parameters.SCENE_MODE_ACTION))
            params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
        else
            params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);

        // if you choose FOCUS_MODE_AUTO remember to call autoFocus() on
        // the Camera object before taking a picture 
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        
        camera.setParameters(params);
    }
	
	private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
	
	class RawCallback implements ShutterCallback, PictureCallback {

	    @Override
	    public void onShutter() {
	        // notify the user, normally with a sound, that the picture has 
	        // been taken
	    }

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	        // manipulate uncompressed image data
	    }       
	}

}