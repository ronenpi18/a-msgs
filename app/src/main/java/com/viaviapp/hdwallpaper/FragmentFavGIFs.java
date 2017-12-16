package com.viaviapp.hdwallpaper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adapter.AdapterGIF;
import com.example.item.ItemGIF;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;

import java.util.ArrayList;

public class FragmentFavGIFs extends Fragment {

    DBHelper dbHelper;
    GridLayoutManager lLayout;
    RecyclerView recyclerView;
    AdapterGIF adapterGIF;
    ArrayList<ItemGIF> arrayList;
    TextView txt_no;
    JsonUtils util;

    public FragmentFavGIFs newInstance() {
        FragmentFavGIFs fragment = new FragmentFavGIFs();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav_img,container,false);

        dbHelper = new DBHelper(getActivity());

        txt_no=(TextView)rootView.findViewById(R.id.textView1);
        util=new JsonUtils(getActivity());

        lLayout = new GridLayoutManager(getActivity(), 2);
        lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapterGIF.isHeader(position) ? lLayout.getSpanCount() : 1;
            }
        });

        arrayList = dbHelper.getAllDataGIF();

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_fav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        adapterGIF = new AdapterGIF(getActivity(),arrayList);

        Constant.isFav = true;
        recyclerView.setAdapter(adapterGIF);


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

        if(arrayList.size()==0)
        {
            txt_no.setVisibility(View.VISIBLE);
        }
        else
        {
            txt_no.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        if(arrayList != null && adapterGIF.getItemCount() > 0) {
            arrayList = dbHelper.getAllDataGIF();
            adapterGIF = new AdapterGIF(getActivity(), arrayList);
            recyclerView.setAdapter(adapterGIF);
            if (arrayList.size() == 0) {
                txt_no.setVisibility(View.VISIBLE);
            } else {
                txt_no.setVisibility(View.INVISIBLE);
            }
        }
        super.onResume();
    }
}
