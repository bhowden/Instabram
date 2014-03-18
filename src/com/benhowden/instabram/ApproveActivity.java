package com.benhowden.instabram;

import java.io.ByteArrayOutputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ApproveActivity extends Activity {
	
	private static Bitmap faceMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.i(ApproveActivity.class.getName(), "onCreate");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve);
		
		Intent intent = getIntent();
		byte[] bytesImage = intent.getExtras().getByteArray("BitmapBytes");
		
		faceMap = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
		
		Log.i(ApproveActivity.class.getName(), Integer.valueOf(bytesImage.length).toString()+" bytes");
		Log.i(ApproveActivity.class.getName(), "Generating Approve Screen....");
		Log.i(ApproveActivity.class.getName(), "images is "+Integer.valueOf(bytesImage.length).toString()+" bytes");
		
		ImageView mImg = (ImageView) findViewById(R.id.imageView1);
        mImg.setImageBitmap(Bitmap.createScaledBitmap(faceMap, 1000, 1000, false));
        mImg.refreshDrawableState();
        
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.approve, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	public void tryAgain(View view) {
		Log.i(ApproveActivity.class.getName(), "tryAgain(View view)");
	}
	
	public void bramIt(View view) {
		Log.i(ApproveActivity.class.getName(), "bramIt(View view)");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		faceMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		Intent intent = new Intent(this, BramItActivity.class);
		intent.putExtra("BitmapBytes", byteArray);
		startActivity(intent);
		finish();
	}
	
}
