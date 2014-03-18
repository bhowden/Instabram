package com.benhowden.instabram;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class FaceSelectorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Uri imageUri = getIntent().getExtras().getParcelable("uri");
		Log.i(FaceSelectorActivity.class.getName(),
				"xuri = " + imageUri.toString());

		new GetBitmapTask().execute(this.getContentResolver(), imageUri);

	}


	public class GetBitmapTask extends AsyncTask<Object, Integer, Object[]> {

		@Override
		protected Object[] doInBackground(Object... params) {
			
			Bitmap imageBitmap;
			Bitmap resizedBitmap = null;
			Bitmap maskBitmap = null;
			int count = 0;
			FaceDetector.Face[] faces = null;
			Uri uri = (Uri) params[1];
			
			try {
				imageBitmap = Media.getBitmap((ContentResolver) params[0], uri);
				
				File imageFile = new File(uri.getPath());
				imageFile.delete();

				Log.i(GetBitmapTask.class.getName(), "converting to RGB 565");
				maskBitmap = Bitmap.createBitmap((imageBitmap.getWidth()),
						imageBitmap.getHeight(), Bitmap.Config.RGB_565);
				Canvas c = new Canvas();
				c.setBitmap(maskBitmap);
				Paint p = new Paint();
				// p.setFilterBitmap(true);
				c.drawBitmap(imageBitmap, 0, 0, p);
				imageBitmap.recycle();
				
				resizedBitmap = Bitmap.createScaledBitmap(maskBitmap, maskBitmap.getWidth()/2, maskBitmap.getWidth()/2, false);
				
				maskBitmap.recycle();
				
				FaceDetector faceDetector = new FaceDetector(resizedBitmap.getWidth(), resizedBitmap.getHeight(), 10);
				faces = new FaceDetector.Face[10];
				int faceCount = faceDetector.findFaces(resizedBitmap, faces);
				count = faceCount;
				Log.i(GetBitmapTask.class.getName(), "# of faces:"
						+ Integer.valueOf(faceCount).toString());
				if(count==0) { finish(); }

			} catch (Exception e) {
				Log.e(FaceSelectorActivity.class.getName(), e.getMessage());
			}

			Object[] rs = { resizedBitmap, Integer.valueOf(count), faces };
			return rs;
			
		}

		@Override
		protected void onPostExecute(Object[] rs) {

			new CircleCutterTask().execute(rs);

		}

	}

	class CircleCutterTask extends AsyncTask<Object, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {
			
			Log.i(CircleCutterTask.class.getName(), "Starting circle crop");
			Bitmap map = (Bitmap) params[0];
			FaceDetector.Face[] faces = (FaceDetector.Face[]) params[2];
			Face face = faces[0];
			PointF point = new PointF();
			face.getMidPoint(point);
			float eyesDistance = face.eyesDistance();
			Log.i(CircleCutterTask.class.getName() , "eyesdistance = "+Float.valueOf(eyesDistance).toString());
			int diameter = Float.valueOf(eyesDistance).intValue();
			Bitmap headShot = getBitmapClippedCircle(map, diameter*2, diameter*2, point.x, point.y);
			map.recycle();
			return headShot;
			
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			
			Log.i(CircleCutterTask.class.getName(), "Opening view activity");
			openViewer(bitmap);

		}

	}
	
	public void openViewer(Bitmap bitmap) {
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		
		Intent intent = new Intent(this, ApproveActivity.class);
		intent.putExtra("BitmapBytes", byteArray);
		startActivity(intent);
		finish();
	
	}
	
	class BramTask extends AsyncTask<Bitmap, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			return bramIt(params[0]);
		} 
		
	}

	public static Bitmap getBitmapClippedCircle(Bitmap bitmap,
			int sectionWidth, int sectionHeight, float midpointX,
			float midpointY) {
		
		Log.i(GetBitmapTask.class.getName(), "converting to ARGB_8888");
		Bitmap maskBitmap = Bitmap.createBitmap((bitmap.getWidth()),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas();
		c.setBitmap(maskBitmap);
		Paint p = new Paint();
		// p.setFilterBitmap(true);
		c.drawBitmap(bitmap, 0, 0, p);
		bitmap.recycle();

		final int width = maskBitmap.getWidth();
		final int height = maskBitmap.getHeight();
		final Bitmap outputBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);

		final Path path = new Path();
		path.addCircle((float) (midpointX), (float) (midpointY),
				(float) sectionHeight, Path.Direction.CCW);

		final Canvas canvas = new Canvas(outputBitmap);
		canvas.drawColor(0, Mode.CLEAR);
		canvas.clipPath(path);
		canvas.drawBitmap(maskBitmap, 0, 0, null);
		return outputBitmap;
	}
	
	public static Bitmap bramIt(Bitmap face) {
		
		
		return null;
		
	}

}