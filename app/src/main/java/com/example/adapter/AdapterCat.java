package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.item.ItemCategory;
import com.example.item.ItemPhotos;
import com.example.util.Constant;
import com.squareup.picasso.Picasso;
import com.viaviapp.hdwallpaper.R;

import java.util.ArrayList;


public class AdapterCat extends RecyclerView.Adapter{

    private ArrayList<ItemCategory> list;
    private Context context;
    private final int VIEW_ITEM = 1;
//    private final int VIEW_PROG = 0;

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView_cat,textView_totwall;

        private MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_category);
            textView_cat = (TextView) view.findViewById(R.id.txt_allphotos_categty);
            textView_totwall = (TextView) view.findViewById(R.id.textView_totwall);
        }
    }

//    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
//        private static ProgressBar progressBar;
//
//        private ProgressViewHolder(View v) {
//            super(v);
//            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//        }
//    }

    public AdapterCat(Context context, ArrayList<ItemCategory> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.allphotos_lsv_item, parent, false);

            return new MyViewHolder(itemView);
//        }
//        else {
//            View v = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.layout_progressbar, parent, false);
//
//            return new ProgressViewHolder(v);
//        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textView_cat.setText(list.get(position).getName());
            ((MyViewHolder) holder).textView_totwall.setText("(" + list.get(position).getTotalWallpaper() + ")");
            Picasso.with(context)
                    .load(list.get(position).getImage().replace(" ", "%20"))
                    .placeholder(R.mipmap.placeholder)
                    .into(((MyViewHolder) holder).imageView);
        }
//        else {
//            if(getItemCount()==1) {
//                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
//                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public void hideHeader() {
//        ProgressViewHolder.progressBar.setVisibility(View.GONE);
//    }

//    public boolean isHeader(int position) {
//        return position == list.size();
//    }

    @Override
    public int getItemViewType(int position) {
//        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
        return VIEW_ITEM;
    }
}