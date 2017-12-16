package com.viaviapp.hdwallpaper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.AdapterCat;
import com.example.item.ItemCategory;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.JsonUtils;
import com.example.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllPhotosFragment extends Fragment {

	DBHelper dbHelper;
	RecyclerView recyclerView;
	LinearLayoutManager lLayout;
	AdapterCat adapterCat;
	ArrayList<ItemCategory> arrayOfAllphotos;
	private ItemCategory itemCategory;
	ProgressBar pbar;
	TextView txt_no;
	Boolean isOver = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_allphotos, container, false);

		dbHelper = new DBHelper(getActivity());
		txt_no=(TextView)rootView.findViewById(R.id.textView1);

		pbar=(ProgressBar)rootView.findViewById(R.id.progressBar1);
		arrayOfAllphotos=new ArrayList<ItemCategory>();

		lLayout = new LinearLayoutManager(getActivity());
		recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_cat);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(lLayout);

		Constant.isFav = false;

		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				itemCategory=arrayOfAllphotos.get(position);
				String Catid=itemCategory.getId();
				Constant.CATEGORY_ID=itemCategory.getId();
				Log.e("cat_id",""+Catid);

				Constant.CATEGORY_TITLE=itemCategory.getName();

				Intent intcat=new Intent(getActivity(),CategoryItem.class);
				startActivity(intcat);
			}
		}));

		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lLayout) {
			@Override
			public void onLoadMore(int p, int totalItemsCount) {
//				arrayOfAllphotos.add(null);
//				adapterCat.notifyItemInserted(arrayOfAllphotos.size() - 1);
//				arrayOfAllphotos.remove(arrayOfAllphotos.size() - 1);
//				adapterCat.notifyItemRemoved(arrayOfAllphotos.size());
				if(!isOver) {
					//                    list_url.clear();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							new MyTask().execute(Constant.CATEGORY_URL);
						}
					}, 2000);
				} else {
//					adapterCat.hideHeader();
					Toast.makeText(getActivity(), getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (JsonUtils.isNetworkAvailable(getActivity())) {
			new MyTask().execute(Constant.CATEGORY_URL);
		} else {

			arrayOfAllphotos = dbHelper.getAllDataCat("cat");
			if(arrayOfAllphotos.size()==0)
			{
				Toast.makeText(getActivity(), getResources().getString(R.string.first_load_internet), Toast.LENGTH_SHORT).show();
			}
			setAdapterToListview();
		}

		return rootView;
	}
	private	class MyTask extends AsyncTask<String, Void, String> {


		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (arrayOfAllphotos.size() == 0) {
				pbar.setVisibility(View.VISIBLE);
			}
			recyclerView.setVisibility(View.GONE);
		}

		@Override
		protected String doInBackground(String... params) {
			return JsonUtils.getJSONString(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (arrayOfAllphotos.size() == 0) {
				pbar.setVisibility(View.INVISIBLE);
			}
			recyclerView.setVisibility(View.VISIBLE);


			if (null == result || result.length() == 0) {
				showToast(getResources().getString(R.string.no_more_data));

			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

					if(jsonArray.length()<=arrayOfAllphotos.size()+10) {
						isOver = true;
					}

					int a = arrayOfAllphotos.size();
					int b;
					b=10;

					JSONObject objJson = null;
					for (int i = a; i < a+b; i++) {
						objJson = jsonArray.getJSONObject(i);

						String id = objJson.getString(Constant.TAG_CAT_ID);
						String name = objJson.getString(Constant.TAG_CAT_NAME);
						String img = objJson.getString(Constant.TAG_CAT_IMAGE);
						String img_thumb = objJson.getString(Constant.TAG_CAT_IMAGE_THUMB);
						String tot_wall = objJson.getString(Constant.TAG_TOTAL_WALL);

						ItemCategory itemCategory = new ItemCategory(id, name, img, img_thumb, tot_wall);
						arrayOfAllphotos.add(itemCategory);

						dbHelper.addtoCatList(itemCategory,"cat");
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
		if(arrayOfAllphotos.size()<11) {
			adapterCat = new AdapterCat(getActivity(),arrayOfAllphotos);
			recyclerView.setAdapter(adapterCat);
		} else {
			adapterCat.notifyDataSetChanged();
		}
		setEmptyText();
	}

	private void setEmptyText() {
		if(adapterCat.getItemCount()==0) {
			txt_no.setVisibility(View.VISIBLE);
		} else {
			txt_no.setVisibility(View.INVISIBLE);
		}
	}

	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
	 
}