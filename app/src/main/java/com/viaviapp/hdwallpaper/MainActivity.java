package com.viaviapp.hdwallpaper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.item.ItemAbout;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	DBHelper dbHelper;
	Toolbar toolbar;
	private AdView mAdView;
	FragmentManager fm;
	DrawerLayout drawer;
	ActionBarDrawerToggle toggle;
	NavigationView navigationView;
	TextView textView_latest,textView_cat,textView_fav,textView_rate,textView_more,textView_about,textView_privacy,textView_setting, textView_gif, textView_developedby;
	LinearLayout ll_latest,ll_cat,ll_fav,ll_gif;
	ProgressDialog pbar;
	final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
	JsonUtils utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(Locale.getDefault().getDisplayLanguage().equals("he")){

		}
		OneSignal.startInit(this).init();
		setTheme(Constant.theme);
		setContentView(R.layout.activity_main);

		utils = new JsonUtils(this);
		utils.forceRTLIfSupported(getWindow());

		toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(getString(R.string.app_name));
//		toolbar.setBackgroundDrawable(getResources().getDrawable(Constant.color));
		this.setSupportActionBar(toolbar);

		dbHelper = new DBHelper(this);
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}

		pbar = new ProgressDialog(this);
		pbar.setMessage(getResources().getString(R.string.loading));
		pbar.setCancelable(false);

		// Look up the AdView as a resource and load a request.
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(new AdRequest.Builder().build());

		fm = getSupportFragmentManager();
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		checkPer();
//		writeToFile("",getApplicationContext(),"email_data.txt");
//		writeToFile("",getApplicationContext(),"name_data.txt");
//		writeToFile("",getApplicationContext(),"phone_data.txt");
//		writeToFile("",getApplicationContext(),"details_data.txt");
//		writeToFile("",getApplicationContext(),"gif_selected.txt");
//		writeToFile("",getApplicationContext(),"wall_selected.txt");
//		writeToFile("",getApplicationContext(),"isStGIF.txt");
//		writeToFile("",getApplicationContext(),"isStWall.txt");

		navigationView = (NavigationView) findViewById(R.id.nav_view);
		findViewById(R.id.ll_nav).setBackgroundDrawable(getResources().getDrawable(Constant.drawable));

		textView_latest = (TextView)findViewById(R.id.textView_latest);
		textView_cat = (TextView)findViewById(R.id.textView_cat);
		textView_fav= (TextView)findViewById(R.id.textView_fav);
		textView_rate = (TextView)findViewById(R.id.textView_rate);
		textView_more = (TextView)findViewById(R.id.textView_more);
		textView_about = (TextView)findViewById(R.id.textView_about);
		textView_privacy = (TextView)findViewById(R.id.textView_privacy);
		textView_setting = (TextView)findViewById(R.id.textView_setting);
		textView_gif = (TextView)findViewById(R.id.textView_gif);
		textView_developedby = (TextView)findViewById(R.id.textView_developedby);

		ll_latest = (LinearLayout)findViewById(R.id.ll_latest);
		ll_cat = (LinearLayout)findViewById(R.id.ll_cat);
		ll_fav = (LinearLayout)findViewById(R.id.ll_fav);
		ll_gif = (LinearLayout)findViewById(R.id.ll_gif);

		textView_latest.setOnClickListener(this);
		textView_cat.setOnClickListener(this);
		textView_fav.setOnClickListener(this);
		textView_rate.setOnClickListener(this);
		textView_more.setOnClickListener(this);
		textView_about.setOnClickListener(this);
		textView_privacy.setOnClickListener(this);
		textView_setting.setOnClickListener(this);
		textView_gif.setOnClickListener(this);

		LatestFragment f1 = new LatestFragment();
		loadFrag(f1,getResources().getString(R.string.latest),fm);
//		toolbar.setTitle(getResources().getString(R.string.latest));
		if(Locale.getDefault().getDisplayLanguage().equals("ru")){
			toolbar.setTitle("последний");
//			changeNavItemBG("последний");

		}
		if(Locale.getDefault().getDisplayLanguage().equals("he")){
			toolbar.setTitle("אחרונים");
//			changeNavItemBG("אחרונים");
		}
		else {
			toolbar.setTitle(getResources().getString(R.string.latest));

//			changeNavItemBG("latest");
		}

		Typeface tf = Typeface.createFromAsset(getAssets(),"lator.ttf");
		textView_latest.setTypeface(tf);
		textView_cat.setTypeface(tf);
		textView_fav.setTypeface(tf);
		textView_rate.setTypeface(tf);
		textView_more.setTypeface(tf);
		textView_about.setTypeface(tf);
		textView_privacy.setTypeface(tf);
		textView_setting.setTypeface(tf);
		textView_gif.setTypeface(tf);

		if (JsonUtils.isNetworkAvailable(this)) {
			new MyTask().execute(Constant.URL_ABOUT_US);
		} else {

			if(!dbHelper.getAbout()) {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.first_load_internet), Toast.LENGTH_SHORT).show();
			} else {
				setDevelopedBy();
			}
		}
	}
	private void writeToFile(String data, Context context, String name) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	@Override
	public void onClick(View view) {
		int id = view.getId();

		if (id == R.id.textView_latest) {
			LatestFragment f1 = new LatestFragment();
			loadFrag(f1,"latest",fm);
			if(Locale.getDefault().getDisplayLanguage().toLowerCase().equals("ru")){
				toolbar.setTitle("последний");
				changeNavItemBG("последний");

			}
			if(Locale.getDefault().getDisplayLanguage().toLowerCase().equals("he")){
				toolbar.setTitle("אחרונים");
				changeNavItemBG("אחרונים");
			}
			else {
				toolbar.setTitle(getResources().getString(R.string.latest));

				changeNavItemBG("latest");
			}
		} else if (id == R.id.textView_cat) {
			AllPhotosFragment f1 = new AllPhotosFragment();
			loadFrag(f1,"cat",fm);
			toolbar.setTitle(getResources().getString(R.string.category));

			changeNavItemBG("cat");

		} else if (id == R.id.textView_fav) {
			FavoriteFragment f1 = new FavoriteFragment();
			loadFrag(f1,"author",fm);
			toolbar.setTitle(getResources().getString(R.string.fav));

			changeNavItemBG("fav");

		} else if(id == R.id.textView_gif) {
			GIFFragment f1 = new GIFFragment();
			loadFrag(f1,"gif",fm);
			toolbar.setTitle(getResources().getString(R.string.gifs));

			changeNavItemBG("gif");

		} else if(id == R.id.textView_more) {
			//startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.play_more_apps))));
			Intent i = new Intent(this,RequestActivity.class);
			startActivity(i);
		} else if(id == R.id.textView_rate) {
			final String appName = getPackageName();//your application package name i.e play store application url
			Log.e("package:", appName);
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id="
								+ appName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ appName)));
			}
		} else if(id == R.id.textView_about) {
			Intent intent = new Intent(MainActivity.this,AboutActivity.class);
			startActivity(intent);
		} else if(id == R.id.textView_privacy) {
			openPrivacyDialog();
		} else if(id == R.id.textView_setting) {
			Intent intent = new Intent(MainActivity.this,Settings.class);
			startActivity(intent);
		}
		drawer.closeDrawer(GravityCompat.START);
	}

	public void loadFrag(Fragment f1, String name, FragmentManager fm) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(R.id.frame_layout, f1, name);
		ft.commit();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void changeNavItemBG(String abc) {
		if(abc.equals("latest")) {
			ll_latest.setBackground(getResources().getDrawable(R.drawable.bg_nav_text_white));
			ll_cat.setBackground(null);
			ll_fav.setBackground(null);
			ll_gif.setBackground(null);
		} else if(abc.equals("cat")) {
			ll_cat.setBackground(getResources().getDrawable(R.drawable.bg_nav_text_white));
			ll_latest.setBackground(null);
			ll_fav.setBackground(null);
			ll_gif.setBackground(null);
		} else if(abc.equals("fav")) {
			ll_fav.setBackground(getResources().getDrawable(R.drawable.bg_nav_text_white));
			ll_latest.setBackground(null);
			ll_cat.setBackground(null);
			ll_gif.setBackground(null);
		} else if(abc.equals("gif")) {
			ll_gif.setBackground(getResources().getDrawable(R.drawable.bg_nav_text_white));
			ll_latest.setBackground(null);
			ll_cat.setBackground(null);
			ll_fav.setBackground(null);
		}
	}

	public void openPrivacyDialog() {
		Dialog dialog;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			dialog = new Dialog(MainActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);
		} else {
			dialog = new Dialog(MainActivity.this);
		}

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.layout_privacy);

		WebView webview = (WebView)dialog.findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
//		webview.loadUrl("file:///android_asset/privacy.html");
		String mimeType = "text/html;charset=UTF-8";
		String encoding = "utf-8";

		if(Constant.itemAbout != null) {
			String text = "<html><head>"
					+ "<style> body{color: #000 !important;text-align:left}"
					+ "</style></head>"
					+ "<body>"
					+ Constant.itemAbout.getPrivacy()
					+ "</body></html>";

			webview.loadData(text, mimeType, encoding);
		}

		dialog.show();
		Window window = dialog.getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
	}

	private	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pbar.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return JsonUtils.getJSONString(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			pbar.dismiss();

			if (null == result || result.length() == 0) {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
					JSONObject  c = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						c = jsonArray.getJSONObject(i);

						String appname = c.getString("app_name");
						String applogo = c.getString("app_logo");
						String desc = c.getString("app_description");
						String appversion = c.getString("app_version");
						String appauthor = c.getString("app_author");
						String appcontact = c.getString("app_contact");
						String email = c.getString("app_email");
						String website = c.getString("app_website");
						String privacy = c.getString("app_privacy_policy");
						String developedby = c.getString("app_developed_by");

						Constant.itemAbout = new ItemAbout(appname,applogo,desc,appversion,appauthor,appcontact,email,website,privacy,developedby);
						dbHelper.addtoAbout();

						setDevelopedBy();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setDevelopedBy() {
		textView_developedby.setText("Developed By: "+Constant.itemAbout.getDevelopedby());
	}

	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	public void checkPer()
	{
		if ((ContextCompat.checkSelfPermission(MainActivity.this,"android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
						MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		boolean canUseExternalStorage = false;

		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					canUseExternalStorage = true;
				}

				if (!canUseExternalStorage) {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_use_save), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Toast.makeText(appContext, "BAck", Toast.LENGTH_LONG).show();
			AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.Dialog));
			alert.setTitle(getString(R.string.app_name));
			alert.setIcon(R.drawable.app_icon);
			alert.setMessage(getResources().getString(R.string.sure_quit));

			alert.setPositiveButton(getResources().getString(R.string.yes),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
											int whichButton) {
							finish();
						}
					});

			alert.setNegativeButton(getResources().getString(R.string.rateapp),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							final String appName = getPackageName();//your application package name i.e play store application url
							try {
								startActivity(new Intent(Intent.ACTION_VIEW,
										Uri.parse("market://details?id="
												+ appName)));
							} catch (android.content.ActivityNotFoundException anfe) {
								startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ appName)));
							}

						}
					});
			alert.show();
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}
}

