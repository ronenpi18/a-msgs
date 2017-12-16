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

import com.example.adapter.AdapterImage;
import com.example.item.ItemPhotos;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;

import java.util.ArrayList;

public class FragmentFavImages extends Fragment {

    DBHelper dbHelper;
    GridLayoutManager lLayout;
    RecyclerView recyclerView;
    AdapterImage adapterImage;
    ArrayList<ItemPhotos> arrayList;
    TextView txt_no;
    JsonUtils util;

    public FragmentFavImages newInstance() {
        FragmentFavImages fragment = new FragmentFavImages();
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
                return adapterImage.isHeader(position) ? lLayout.getSpanCount() : 1;
            }
        });

        arrayList = dbHelper.getAllData("fav");

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_fav);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lLayout);
        adapterImage = new AdapterImage(getActivity(),arrayList);

        Constant.isFav = true;
        recyclerView.setAdapter(adapterImage);


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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser) {
            if(arrayList != null) {
                arrayList = dbHelper.getAllData("fav");
                adapterImage = new AdapterImage(getActivity(), arrayList);
                recyclerView.setAdapter(adapterImage);
                if (arrayList.size() == 0) {
                    txt_no.setVisibility(View.VISIBLE);
                } else {
                    txt_no.setVisibility(View.INVISIBLE);
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        if(arrayList != null && adapterImage.getItemCount() > 0) {
            arrayList = dbHelper.getAllData("fav");
            adapterImage = new AdapterImage(getActivity(), arrayList);
            recyclerView.setAdapter(adapterImage);
            if (arrayList.size() == 0) {
                txt_no.setVisibility(View.VISIBLE);
            } else {
                txt_no.setVisibility(View.INVISIBLE);
            }
        }
        super.onResume();
    }
}
