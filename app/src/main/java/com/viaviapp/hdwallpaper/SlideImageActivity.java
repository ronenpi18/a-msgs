package com.viaviapp.hdwallpaper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.item.ItemPhotos;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;
import com.example.util.TouchImageView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SlideImageActivity extends AppCompatActivity implements SensorEventListener {

	DBHelper dbHelper;
	int position;
	ViewPager viewpager;
	int TOTAL_IMAGE;
	private SensorManager sensorManager;
	private boolean checkImage = false;
	private long lastUpdate;
	Handler handler;
	Runnable Update;
	String image_id;
	FloatingActionsMenu rightLabels;
	FloatingActionButton fabshare,fabsetas,fabsave,fabfav;
	private AdView mAdView;
	JsonUtils utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stubv
		super.onCreate(savedInstanceState);
		setTheme(Constant.theme);
		setContentView(R.layout.fullimageslider);

		utils = new JsonUtils(this);
		utils.forceRTLIfSupported(getWindow());

		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle("");
		this.setSupportActionBar(toolbar);
//		toolbar.setBackgroundDrawable(getResources().getDrawable(Constant.drawable));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		dbHelper = new DBHelper(this);

		// Look up the AdView as a resource and load a request.
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(new AdRequest.Builder().build());

		Intent i=getIntent();
		position=i.getIntExtra("POSITION_ID", 0);

		loadViewed(position);

		TOTAL_IMAGE=Constant.arrayList.size()-1;
		viewpager=(ViewPager)findViewById(R.id.image_slider);
		handler=new Handler();

		ImagePagerAdapter adapter = new ImagePagerAdapter();
		viewpager.setAdapter(adapter);
		viewpager.setCurrentItem(position);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();

		rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
		fabshare = (FloatingActionButton)findViewById(R.id.fab_share);
		fabsetas = (FloatingActionButton)findViewById(R.id.fab_setas);
		fabfav = (FloatingActionButton)findViewById(R.id.fab_fav);
		fabsave = (FloatingActionButton)findViewById(R.id.fab_save);

		FirstFav();

		viewpager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

				position=viewpager.getCurrentItem();

				ArrayList<ItemPhotos> pojolist = dbHelper.getFavRow(Constant.arrayList.get(position).getId(),"fav");
				if(pojolist.size()==0) {
					fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav));
				}
				else
				{	
//					if(pojolist.get(0).getImageurl().equals(Image_Url))
//					{
					fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav_hover));
//					}

				}

				loadViewed(position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int position) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int position) {
				// TODO Auto-generated method stub

			}
		});


		fabshare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				position=viewpager.getCurrentItem();

				(new ShareTask(SlideImageActivity.this)).execute(Constant.arrayList.get(position).getImage().replace(" ", "%20"));


				if (rightLabels.isExpanded()) {

					Rect outRect = new Rect();
					rightLabels.getGlobalVisibleRect(outRect);

					if(!outRect.contains((int)v.getRight(), (int)v.getRight()))
						rightLabels.collapse();
				}
			}
		});

		fabsetas.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				position=viewpager.getCurrentItem();
				Intent intwall=new Intent(getApplicationContext(),SetAsWallpaperActivity.class);
				intwall.putExtra("POSITION_ID", position);
				startActivity(intwall);

				if (rightLabels.isExpanded()) {

					Rect outRect = new Rect();
					rightLabels.getGlobalVisibleRect(outRect);

					if(!outRect.contains((int)v.getRight(), (int)v.getRight()))
						rightLabels.collapse();
				}
			}
		});

		fabfav.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				position = viewpager.getCurrentItem();

				image_id = Constant.arrayList.get(position).getId();

				ArrayList<ItemPhotos> pojolist = dbHelper.getFavRow(image_id, "fav");
				if(pojolist.size()==0) {
					AddtoFav(position);//if size is zero i.e means that record not in database show add to favorite 
				}
				else {
					if(pojolist.get(0).getId().equals(image_id))
					{
						RemoveFav(position);
					}
				}

				if (rightLabels.isExpanded()) {

					Rect outRect = new Rect();
					rightLabels.getGlobalVisibleRect(outRect);

					if(!outRect.contains((int)v.getRight(), (int)v.getRight()))
						rightLabels.collapse();
				}
			}
		});

		fabsave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				position=viewpager.getCurrentItem();

				(new SaveTask(SlideImageActivity.this)).execute(Constant.arrayList.get(position).getImage().replace(" ", "%20"));

				if (rightLabels.isExpanded()) {

					Rect outRect = new Rect();
					rightLabels.getGlobalVisibleRect(outRect);

					if(!outRect.contains((int)v.getRight(), (int)v.getRight()))
						rightLabels.collapse();
				}
			}
		});

	}

	@Override public boolean dispatchTouchEvent(MotionEvent event){
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (rightLabels.isExpanded()) {

				Rect outRect = new Rect();
				rightLabels.getGlobalVisibleRect(outRect);

				if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
					rightLabels.collapse();
			}
		}

		return super.dispatchTouchEvent(event);
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

	public void AddtoFav(int position) {
		dbHelper.addtoFavorite(Constant.arrayList.get(position),"fav");
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
		fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav_hover));
	}

	public void RemoveFav(int position) {
		dbHelper.removeFav(Constant.arrayList.get(position).getId());
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.removed_fav), Toast.LENGTH_SHORT).show();
		fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav));
	}

	public void FirstFav()
	{
		int first= viewpager.getCurrentItem();
		String Image_id = Constant.arrayList.get(first).getImage();

		ArrayList<ItemPhotos> pojolist= dbHelper.getFavRow(Constant.arrayList.get(first).getId(),"fav");
		if(pojolist.size()==0) {
			fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav));
		}
		else {
//			if(pojolist.get(0).getImageurl().equals(Image_id))
//			{
			fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav_hover));
//			}

		}
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImagePagerAdapter() {
			// TODO Auto-generated constructor stub

			inflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return Constant.arrayList.size();

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.viewpager_item, container, false);
			assert imageLayout != null;
			TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			Picasso.with(SlideImageActivity.this)
					.load(Constant.arrayList.get(position).getImage().replace(" ", "%20"))
					.placeholder(R.mipmap.placeholder)
					.into(imageView, new Callback() {
						@Override
						public void onSuccess() {
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onError() {
							spinner.setVisibility(View.GONE);
						}
					});

			container.addView(imageLayout, 0);
			return imageLayout;

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}
	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 2) //
		{
			if (actualTime - lastUpdate < 200) {
				return;
			}
			lastUpdate = actualTime;
			//		      Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
			//		          .show();
			if (checkImage) {
				position=viewpager.getCurrentItem();
				viewpager.setCurrentItem(position);
			} else {
				position=viewpager.getCurrentItem();
				position++;
				if (position == TOTAL_IMAGE) {
					position = TOTAL_IMAGE;
				}
				viewpager.setCurrentItem(position);
			}
			checkImage = !checkImage;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		// register this class as a listener for the orientation and
		// accelerometer sensors

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();

		sensorManager.unregisterListener(this);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(Update);
		sensorManager.unregisterListener(this);
	}	

	public class SaveTask extends AsyncTask<String , String , String>
	{
		private Context context;
		private ProgressDialog pDialog;
		String image_url;
		URL myFileUrl;
		String myFileUrl1;
		Bitmap bmImg = null;
		File file ;

		public SaveTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();

			pDialog = new ProgressDialog(context,AlertDialog.THEME_HOLO_LIGHT);
			pDialog.setMessage(getResources().getString(R.string.download_image));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			try {  

				myFileUrl = new URL(args[0]);
				//myFileUrl1 = args[0];

				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
				conn.setDoInput(true);   
				conn.connect();     
				InputStream is = conn.getInputStream();
				bmImg = BitmapFactory.decodeStream(is); 
			}
			catch (IOException e)
			{       
				e.printStackTrace();  
			}
			try {       

				String path = myFileUrl.getPath();
				String idStr = path.substring(path.lastIndexOf('/') + 1);
				File filepath = Environment.getExternalStorageDirectory();
				File dir = new File (filepath.getAbsolutePath() + Constant.DOWNLOAD_SDCARD_FOLDER_PATH_WALLPAPER);
				dir.mkdirs();
				String fileName = idStr;
				file = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(file);
				bmImg.compress(CompressFormat.JPEG, 75, fos);   
				fos.flush();    
				fos.close();

				MediaScannerConnection.scanFile(SlideImageActivity.this, new String[] { file.getAbsolutePath()},
						null,
						new MediaScannerConnection.OnScanCompletedListener() {
							@Override
							public void onScanCompleted(String path, Uri uri) {

							}
						});

			}
			catch (Exception e)
			{
				e.printStackTrace();  
			}
			return null;   
		}


		@Override
		protected void onPostExecute(String args) {
			// TODO Auto-generated method stub
			Toast.makeText(SlideImageActivity.this, getResources().getString(R.string.image_save_success), Toast.LENGTH_SHORT).show();
			pDialog.dismiss();
		}
	}

	public class ShareTask extends AsyncTask<String , String , String>
	{
		private Context context;
		private ProgressDialog pDialog;
		String image_url;
		URL myFileUrl;


		String myFileUrl1;
		Bitmap bmImg = null;
		File file ;

		public ShareTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();

			pDialog = new ProgressDialog(context,AlertDialog.THEME_HOLO_LIGHT);
			pDialog.setMessage(getResources().getString(R.string.please_wait));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			try {  

				myFileUrl = new URL(args[0]);
				//myFileUrl1 = args[0];

				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
				conn.setDoInput(true);   
				conn.connect();     
				InputStream is = conn.getInputStream();
				bmImg = BitmapFactory.decodeStream(is); 
			}
			catch (IOException e)
			{       
				e.printStackTrace();  
			}
			try {       

				String path = myFileUrl.getPath();
				String idStr = path.substring(path.lastIndexOf('/') + 1);
				File filepath = Environment.getExternalStorageDirectory();
				File dir = new File (filepath.getAbsolutePath() + Constant.DOWNLOAD_SDCARD_FOLDER_PATH_WALLPAPER);
				dir.mkdirs();
				String fileName = idStr;
				file = new File(dir, fileName);
				FileOutputStream fos = new FileOutputStream(file);
				bmImg.compress(CompressFormat.JPEG, 75, fos);   
				fos.flush();    
				fos.close();    

			}
			catch (Exception e)
			{
				e.printStackTrace();  
			}
			return null;   
		}


		@Override
		protected void onPostExecute(String args) {
			// TODO Auto-generated method stub

			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
			startActivity(Intent.createChooser(share, getResources().getString(R.string.share_image)));
			pDialog.dismiss();
		}
	}

	private void loadViewed(int pos) {
		if (JsonUtils.isNetworkAvailable(this)) {
			new MyTask().execute(Constant.URL_WALLPAPER+Constant.arrayList.get(pos).getId(),String.valueOf(pos));
			dbHelper.updateView(Constant.arrayList.get(pos).getId(), Constant.arrayList.get(pos).getTotalViews());
		}
	}

	private	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			JsonUtils.getJSONString(params[0]);
			return params[1];
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			int p = Integer.parseInt(result);
			int tot = Integer.parseInt(Constant.arrayList.get(p).getTotalViews());
			Constant.arrayList.get(p).setTotalViews(""+(tot+1));
		}
	}
}
