package com.viaviapp.hdwallpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.AdapterGIF;
import com.example.item.ItemGIF;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.EndlessRecyclerViewScrollListener;
import com.example.util.JsonUtils;
import com.example.util.RecyclerItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GIFFragment extends Fragment {

	DBHelper dbHelper;
	GridLayoutManager lLayout;
	RecyclerView recyclerView;
	AdapterGIF adapterGIF;
	ArrayList<ItemGIF> arrayList;
	ProgressBar pbar;
	TextView txt_no;
	Boolean isOver = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_latest, container, false);

		dbHelper = new DBHelper(getActivity());
		pbar=(ProgressBar)rootView.findViewById(R.id.progressBar1);

		arrayList = new ArrayList<ItemGIF>();

		lLayout = new GridLayoutManager(getActivity(), 2);
		lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				return adapterGIF.isHeader(position) ? lLayout.getSpanCount() : 1;
			}
		});

		txt_no=(TextView)rootView.findViewById(R.id.textView1);
		txt_no.setText(getResources().getString(R.string.no_gif));

		recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_latest);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(lLayout);

		Constant.isFav = false;

//		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//			@Override
//			public void onItemClick(View view, int position) {
//				Constant.arrayList.clear();
//				Constant.arrayList.addAll(arrayList);
//				Intent intslider=new Intent(getActivity(),SlideImageActivity.class);
//				intslider.putExtra("POSITION_ID", position);
//
//				startActivity(intslider);
//			}
//		}));


		recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lLayout) {
			@Override
			public void onLoadMore(int p, int totalItemsCount) {
//				arrayList.add(null);
//				adapterGIF.notifyItemInserted(arrayList.size() - 1);
//				arrayList.remove(arrayList.size() - 1);
//				adapterGIF.notifyItemRemoved(arrayList.size());
				if(!isOver) {
//                    list_url.clear();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							new MyTask().execute(Constant.URL_GIFs_lIST);
						}
					}, 2000);
				} else {
//					adapterGIF.hideHeader();
					//Toast.makeText(getActivity(), getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
				}
			}
		});

		if (JsonUtils.isNetworkAvailable(getActivity())) {
			new MyTask().execute(Constant.URL_GIFs_lIST);
		} else {
//			arrayList = dbHelper.getAllDataGIF();
//			if(arrayList.size()==0) {
				Toast.makeText(getActivity(), getResources().getString(R.string.connect_net_load_gif), Toast.LENGTH_SHORT).show();
//			} else {
//				adapterGIF = new AdapterGIF(getActivity(),arrayList);
//				recyclerView.setAdapter(adapterGIF);
//			}
		}

		return rootView;
	}

	private	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if(arrayList.size() == 0) {
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

			if(arrayList.size() == 0) {
				pbar.setVisibility(View.INVISIBLE);
			}

			if (null == result || result.length() == 0) {
				showToast(getResources().getString(R.string.no_data_found));

			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

					if(jsonArray.length()<=arrayList.size()+10) {
						isOver = true;
					}

					int a = arrayList.size();
					int b;
					b=10;


					JSONObject objJson = null;
					for (int i = a; i < a+b; i++) {
						objJson = jsonArray.getJSONObject(i);

						String id = objJson.getString(Constant.TAG_WALL_ID);
						String img = objJson.getString(Constant.TAG_GIF_IMAGE);
						String views = objJson.getString(Constant.TAG_WALL_VIEWS);

						ItemGIF objItem = new ItemGIF(id,img,views);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//						dbHelper.addtoFavorite(objItem,"latest");
						arrayList.add(objItem);
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
		if(arrayList.size()<11) {
//			DraweeController controller = Fresco.newDraweeControllerBuilder()
//					.setAutoPlayAnimations(uri)
//					.setAutoPlayAnimations(true)
//					.build();
//			imageView.setController(controller);
			adapterGIF = new AdapterGIF(getActivity(),arrayList);
			recyclerView.setAdapter(adapterGIF);
		} else {
			adapterGIF.notifyDataSetChanged();
		}
		setEmptyText();
	}

	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	private void setEmptyText() {
		if(adapterGIF.getItemCount()==0) {
			txt_no.setVisibility(View.VISIBLE);
		} else{
			txt_no.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		if(adapterGIF!=null && adapterGIF.getItemCount() > 0) {
			adapterGIF.notifyDataSetChanged();
			setEmptyText();
		}
		super.onResume();
	}

    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

}
