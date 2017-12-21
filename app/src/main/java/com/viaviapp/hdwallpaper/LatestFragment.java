package com.viaviapp.hdwallpaper;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.AdapterImage;
import com.example.item.ItemPhotos;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LatestFragment extends Fragment {

	DBHelper dbHelper;
	GridLayoutManager lLayout;
	RecyclerView recyclerView;
	AdapterImage adapterImage;
	ArrayList<ItemPhotos> arrayOfLatestImage;
	ProgressBar pbar;
	TextView txt_no;
	Boolean isOver = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_latest, container, false);
		pbar=(ProgressBar)rootView.findViewById(R.id.progressBar1);

		dbHelper = new DBHelper(getActivity());

		arrayOfLatestImage=new ArrayList<ItemPhotos>();
		for(int i=0;i<arrayOfLatestImage.size();i++){
			if(arrayOfLatestImage.get(i).getImage().substring(arrayOfLatestImage.size()-3).equals("gif")){
				arrayOfLatestImage.remove(i);
			}
		}
		lLayout = new GridLayoutManager(getActivity(), 2);
		lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return adapterImage.isHeader(position) ? lLayout.getSpanCount() : 1;
			}
		});

		txt_no=(TextView)rootView.findViewById(R.id.textView1);
		txt_no.setText(getResources().getString(R.string.no_latest));

		recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_latest);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(lLayout);

		Constant.isFav = false;

//		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//			@Override
//			public void onItemClick(View view, int position) {
//				Constant.arrayList.clear();
//				Constant.arrayList.addAll(arrayOfLatestImage);
//				Intent intslider=new Intent(getActivity(),SlideImageActivity.class);
//				intslider.putExtra("POSITION_ID", position);
//
//				startActivity(intslider);
//			}
//		}));

		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lLayout) {
			@Override
			public void onLoadMore(int p, int totalItemsCount) {
//                arrayOfLatestImage.add(null);
//                adapterImage.notifyItemInserted(arrayOfLatestImage.size() - 1);
//				arrayOfLatestImage.remove(arrayOfLatestImage.size() - 1);
//				adapterImage.notifyItemRemoved(arrayOfLatestImage.size());
				if(!isOver) {
					//                    list_url.clear();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							new MyTask().execute(Constant.LATEST_URL);
						}
					}, 2000);
				} else {
//					adapterImage.hideHeader();
					Toast.makeText(getActivity(), getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
				}
			}
		});


		if (JsonUtils.isNetworkAvailable(getActivity())) {
			new MyTask().execute(Constant.LATEST_URL);
		} else {
			arrayOfLatestImage = dbHelper.getAllData("latest");
			if(arrayOfLatestImage.size()==0) {
				for(int i=0;i<arrayOfLatestImage.size();i++){
					if(arrayOfLatestImage.get(i).getImage().substring(arrayOfLatestImage.size()-3).equals("gif")){
						arrayOfLatestImage.remove(i);
					}
				}
				Toast.makeText(getActivity(), getResources().getString(R.string.first_load_internet), Toast.LENGTH_SHORT).show();
			} else {
				for(int i=0;i<arrayOfLatestImage.size();i++){
					if(arrayOfLatestImage.get(i).getImage().substring(arrayOfLatestImage.size()-3).equals("gif")){
						arrayOfLatestImage.remove(i);
					}
				}
				adapterImage = new AdapterImage(getActivity(),arrayOfLatestImage);
				recyclerView.setAdapter(adapterImage);
			}
		}
		return rootView;
	}

	private	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if(arrayOfLatestImage.size() == 0) {
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

			if(arrayOfLatestImage.size() == 0) {
				pbar.setVisibility(View.INVISIBLE);
			}

			if (null == result || result.length() == 0) {
				showToast(getResources().getString(R.string.no_data_found));
			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

					if(jsonArray.length()<=arrayOfLatestImage.size()+10) {
						isOver = true;
					}

					int a = arrayOfLatestImage.size();
					int b;
					b=10;

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

						dbHelper.addtoFavorite(objItem,"latest");
						if(!img.substring(img.length()-3).equals("gif")){
							arrayOfLatestImage.add(objItem);

						}
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

		if(arrayOfLatestImage.size()<11) {
			adapterImage = new AdapterImage(getActivity(),arrayOfLatestImage);
			recyclerView.setAdapter(adapterImage);
		} else {
			adapterImage.notifyDataSetChanged();
		}

		setExmptTextView();
	}

	private void setExmptTextView() {
		if(adapterImage.getItemCount()==0) {
			txt_no.setVisibility(View.VISIBLE);
		} else{
			txt_no.setVisibility(View.INVISIBLE);
		}
	}

	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResume() {
		if(adapterImage!=null && adapterImage.getItemCount() > 0) {
			adapterImage.notifyDataSetChanged();
			setExmptTextView();
		}
		super.onResume();
	}
}
