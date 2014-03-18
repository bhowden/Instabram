package com.benhowden.instabram;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.benhowden.instabram.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainainActivity extends Activity {

	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private SystemUiHider bramUiHider;
	private static Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mainain);

		final View controlsView = findViewById(R.id.instabram_controls);
		final View contentView = findViewById(R.id.instabram_main);

		bramUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		bramUiHider.setup();
		bramUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
					public void onVisibilityChange(boolean visible) {

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

							if (mControlsHeight == 0) {

								mControlsHeight = controlsView.getHeight();

							}

							if (mShortAnimTime == 0) {

								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);

							}

							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);

						} else {

							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);

						}

						if (visible && AUTO_HIDE) {

							delayedHide(AUTO_HIDE_DELAY_MILLIS);

						}
					}
				});

		contentView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (TOGGLE_ON_CLICK) {

					bramUiHider.toggle();

				} else {

					bramUiHider.show();

				}

			}
		});

		findViewById(R.id.exit_button).setOnTouchListener(
				mDelayHideTouchListener);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		delayedHide(100);

	}

	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;

		}

	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {

		@Override
		public void run() {
			bramUiHider.hide();
		}

	};

	private void delayedHide(int delayMillis) {

		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			Intent intent = new Intent(this, FaceSelectorActivity.class);
			intent.putExtra("uri", imageUri);
			startActivity(intent);
		}

	}

	public void openBramIt(View view) {

		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		imageUri = Uri.fromFile(getOutputMediaFile());
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(cameraIntent, 100);

	}

	public void openMyBrams(View view) {

		Intent intent = new Intent(this, ViewBramsActivity.class);
		startActivity(intent);

	}

	public void shareToFB(View view) {
		// TODO:
	}

	public void exit(View view) {

		this.finish();

	}

	private static File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Instabram");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
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
