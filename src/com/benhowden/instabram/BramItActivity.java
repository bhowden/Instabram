package com.benhowden.instabram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class BramItActivity extends Activity {
	
	private static Bitmap instabram;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bram_it);
		Intent intent = getIntent();
		byte[] bytesImage = intent.getExtras().getByteArray("BitmapBytes");
		Bitmap face = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
		Bitmap bram = BitmapFactory.decodeResource(getResources(), R.drawable.bram_stoker);
		int size = face.getHeight();
		Log.i(BramItActivity.class.getName(), "bramstamp size "+Integer.valueOf(size).toString());
		Bitmap littleFace = Bitmap.createScaledBitmap(face, 350, 350, false);
		face.recycle();
		instabram = overlay(bram, littleFace);
		ImageView mImg = (ImageView) findViewById(R.id.bramImage);
        mImg.setImageBitmap(instabram);
        mImg.refreshDrawableState();
        
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_bram_it,
					container, false);
			return rootView;
		}
	}
	
	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Matrix(), null);
        return bmOverlay;
    }
	
	public void save(View view) {
		storeImage(instabram);
		Context context = getApplicationContext();
		CharSequence text = "Instabram saved!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		finish();
	}
	
	public void cancel(View view) {
		finish();
	}
	
	private void storeImage(Bitmap image) {
	    File pictureFile = getOutputMediaFile();
	    if (pictureFile == null) {
	        Log.d(BramItActivity.class.getName(),
	                "Error creating media file, check storage permissions: ");// e.getMessage());
	        return;
	    } 
	    try {
	        FileOutputStream fos = new FileOutputStream(pictureFile);
	        image.compress(Bitmap.CompressFormat.PNG, 90, fos);
	        fos.close();
	    } catch (FileNotFoundException e) {
	        Log.d(BramItActivity.class.getName(), "File not found: " + e.getMessage());
	    } catch (IOException e) {
	        Log.d(BramItActivity.class.getName(), "Error accessing file: " + e.getMessage());
	    }  
	}
	
	private static File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Instabram");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(BramItActivity.class.getName(), "failed to create directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".png");

		return mediaFile;

	}

}
