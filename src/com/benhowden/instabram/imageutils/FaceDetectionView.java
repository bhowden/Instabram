package com.benhowden.instabram.imageutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.util.Log;
import android.view.View;

public class FaceDetectionView extends View {
	private Bitmap bitmap;
	private FaceDetector.Face[] faces;
	private int faceCount;
	private PointF tmp_point = new PointF();
	private Paint tmp_paint = new Paint();

	public FaceDetectionView(Context context, Bitmap bitmap, FaceDetector.Face[] faces, int faceCount) {
		super(context);
		this.bitmap = bitmap;
		this.faces = faces;
		this.faceCount = faceCount;
		updateImage(bitmap);
	}

	public void updateImage(Bitmap bitmap) {
		Log.d("Face_Detection", "Face Count: " + String.valueOf(faceCount));
	}

	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, null);
		for (int i = 0; i < faceCount; i++) {
			FaceDetector.Face face = faces[i];
			tmp_paint.setColor(Color.RED);
			tmp_paint.setAlpha(100);
			face.getMidPoint(tmp_point);
			canvas.drawCircle(tmp_point.x, tmp_point.y, face.eyesDistance(),
					tmp_paint);
		}
	}
}
