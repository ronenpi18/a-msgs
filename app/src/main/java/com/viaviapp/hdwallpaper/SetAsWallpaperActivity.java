package com.viaviapp.hdwallpaper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.IOException;

public class SetAsWallpaperActivity extends AppCompatActivity {

	private CropImageView mCropImageView;
	int position;
	JsonUtils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(Constant.theme);
		setContentView(R.layout.set_as_wallpaper_activity);

		utils = new JsonUtils(this);
		utils.forceRTLIfSupported(getWindow());

		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(getResources().getString(R.string.setaswallpaper));
		this.setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		Intent i=getIntent();
		position=i.getIntExtra("POSITION_ID", 0);
		mCropImageView = (CropImageView)findViewById(R.id.CropImageView);

		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		ImageLoader.getInstance().loadImage(Constant.arrayList.get(position).getImage().replace(" ", "%20"), new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				mCropImageView.setImageBitmap(arg2);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void setAsWallpaper(View view) throws IOException {
		(new SetWallpaperTask(SetAsWallpaperActivity.this)).execute("");
	}

	public class SetWallpaperTask extends AsyncTask<String , String , String>
	{
		private Context context;
		private ProgressDialog pDialog;
		Bitmap bmImg = null;

		public SetWallpaperTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = new ProgressDialog(context,AlertDialog.THEME_HOLO_LIGHT);
			pDialog.setMessage(getResources().getString(R.string.setting_wallpaper));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			return null;   
		}


		@Override
		protected void onPostExecute(String args) {
			// TODO Auto-generated method stub
			bmImg=mCropImageView.getCroppedImage();

			WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext()); // --The method context() is undefined for the type SetWallpaperTask
			try {
				wpm.setBitmap(bmImg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pDialog.dismiss();
			Toast.makeText(SetAsWallpaperActivity.this, getResources().getString(R.string.wallpaper_set), Toast.LENGTH_SHORT).show();
			
			finish();
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{       
		switch (menuItem.getItemId()) 
		{
		case android.R.id.home: 
			onBackPressed();
			return true;

		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}
}
