package com.viaviapp.hdwallpaper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.AdapterImage;
import com.example.item.ItemPhotos;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryItem extends AppCompatActivity {

	DBHelper dbHelper;
	GridLayoutManager lLayout;
	RecyclerView recyclerView;
	AdapterImage adapterImage;
	ProgressBar pbar;
	ArrayList<ItemPhotos> arrayOfCategoryImage;
	private AdView mAdView;
	TextView txt_no;
	Boolean isOver = false;
	JsonUtils utils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(Constant.theme);
 		setContentView(R.layout.category_item_grid);

		utils = new JsonUtils(this);
		utils.forceRTLIfSupported(getWindow());

		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(Constant.CATEGORY_TITLE);
		this.setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		toolbar.setBackgroundColor(Constant.color);

		dbHelper = new DBHelper(this);
		txt_no=(TextView)findViewById(R.id.textView1);

		arrayOfCategoryImage = new ArrayList<ItemPhotos>();

		// Look up the AdView as a resource and load a request.
		mAdView = (AdView) findViewById(R.id.adView);
		mAdView.loadAd(new AdRequest.Builder().build());

		pbar=(ProgressBar)findViewById(R.id.progressBar1);
		arrayOfCategoryImage=new ArrayList<ItemPhotos>();

		lLayout = new GridLayoutManager(this, 2);
		lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return adapterImage.isHeader(position) ? lLayout.getSpanCount() : 1;
			}
		});

		recyclerView = (RecyclerView)findViewById(R.id.recyclerView_cat_image);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(lLayout);

		Constant.isFav = false;

//		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//			@Override
//			public void onItemClick(View view, int position) {
//				Constant.arrayList.clear();
//				Constant.arrayList.addAll(arrayOfCategoryImage);
//				Intent intslider=new Intent(getApplicationContext(),SlideImageActivity.class);
//				intslider.putExtra("POSITION_ID", position);
//
//				startActivity(intslider);
//			}
//		}));

		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lLayout) {
			@Override
			public void onLoadMore(int p, int totalItemsCount) {
//				arrayOfCategoryImage.add(null);
//				adapterImage.notifyItemInserted(arrayOfCategoryImage.size() - 1);
//				arrayOfCategoryImage.remove(arrayOfCategoryImage.size() - 1);
//				adapterImage.notifyItemRemoved(arrayOfCategoryImage.size());
				if(!isOver) {
					//                    list_url.clear();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							new MyTask().execute(Constant.CATEGORY_ITEM_URL+Constant.CATEGORY_ID);
						}
					}, 2000);
				} else {
//					adapterImage.hideHeader();
					Toast.makeText(CategoryItem.this, getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (JsonUtils.isNetworkAvailable(CategoryItem.this)) {
			new MyTask().execute(Constant.CATEGORY_ITEM_URL+Constant.CATEGORY_ID);
		} 
		else
		{
			arrayOfCategoryImage = dbHelper.getCatList(Constant.CATEGORY_ID,"catlist");
			if(arrayOfCategoryImage.size()==0) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.first_load_internet), Toast.LENGTH_SHORT).show();
			}
			adapterImage = new AdapterImage(this,arrayOfCategoryImage);
			setAdapterToListview();

		}

	}

	private	class MyTask extends AsyncTask<String, Void, String> {


		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if(arrayOfCategoryImage.size() == 0) {
				pbar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			return JsonUtils.getJSONString(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(arrayOfCategoryImage.size() == 0) {
				pbar.setVisibility(View.INVISIBLE);
			}


			if (null == result || result.length() == 0) {
				showToast(getResources().getString(R.string.no_data_found));

			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

					if(jsonArray.length()<=arrayOfCategoryImage.size()+10) {
						isOver = true;
					}

					int a = arrayOfCategoryImage.size();
					int b=10;

					JSONObject objJson = null;
					for (int i = a; i < a+b; i++) {
						objJson = jsonArray.getJSONObject(i);

						String id = objJson.getString(Constant.TAG_WALL_ID);
						String cid = objJson.getString(Constant.TAG_CAT_ID);
						String img = objJson.getString(Constant.TAG_WALL_IMAGE);
						String img_thumb = objJson.getString(Constant.TAG_WALL_IMAGE_THUMB);
						String cat_name = objJson.getString(Constant.TAG_CAT_NAME);
						String views = objJson.getString(Constant.TAG_WALL_VIEWS);

						ItemPhotos objItem = new ItemPhotos(id,cid,img,img_thumb,cat_name,views);

						dbHelper.addtoFavorite(objItem,"catlist");

						arrayOfCategoryImage.add(objItem);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					isOver = true;
				}
				setAdapterToListview();
			}
		}
	}

	public void setAdapterToListview() {
		if(arrayOfCategoryImage.size()<11) {
			adapterImage = new AdapterImage(this,arrayOfCategoryImage);
			recyclerView.setAdapter(adapterImage);
		} else {
			adapterImage.notifyDataSetChanged();
		}

		setEmptyText();
	}

	public void showToast(String msg) {
		Toast.makeText(CategoryItem.this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{       
		switch (menuItem.getItemId()) 
		{
		case android.R.id.home: 
			onBackPressed();
			break;

		default:
			return super.onOptionsItemSelected(menuItem);
		}
		return true;
	}

	private void setEmptyText() {
		if(adapterImage.getItemCount()==0) {
			txt_no.setVisibility(View.VISIBLE);
		} else {
			txt_no.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		if(adapterImage!=null && adapterImage.getItemCount() > 0) {
			adapterImage.notifyDataSetChanged();
			setEmptyText();
		}
		super.onResume();
	}
}
