package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.item.ItemGIF;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.viaviapp.hdwallpaper.R;
import com.viaviapp.hdwallpaper.RequestActivity;
import com.viaviapp.hdwallpaper.SlideImageActivityGIF;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class AdapterGIF extends RecyclerView.Adapter{

    private DBHelper dbHelper;
    private ArrayList<ItemGIF> list;
    private Context context;
    private JsonUtils utils;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    Bundle bundle;
    private class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView_fav;
        private SimpleDraweeView imageView_gif;
        private TextView textView_totviews;

        private MyViewHolder(View view) {
            super(view);
            imageView_gif = (SimpleDraweeView) view.findViewById(R.id.imageView_gifitem);
            textView_totviews = (TextView) view.findViewById(R.id.textView_totviews);
            imageView_fav = (ImageView) view.findViewById(R.id.imageView_favourite);
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
    public static final String MyPREFERENCES = "MyPrefs" ;

    public AdapterGIF(Context context, ArrayList<ItemGIF> list) {
        this.list = list;
        this.context = context;
        dbHelper = new DBHelper(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        utils = new JsonUtils(context);

        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,Constant.GRID_PADDING, r.getDisplayMetrics());
        Constant.columnWidth = (int) ((utils.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_gif, parent, false);

            return new MyViewHolder(itemView);
//        } else {
//            View v = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.layout_progressbar, parent, false);
//
//            return new ProgressViewHolder(v);
//        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            FirstFav(position, ((MyViewHolder) holder).imageView_fav);

            ((MyViewHolder) holder).imageView_gif.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((MyViewHolder) holder).imageView_gif.setLayoutParams(new RelativeLayout.LayoutParams(Constant.columnWidth, Constant.columnWidth));
            ((MyViewHolder) holder).textView_totviews.setText(list.get(position).getTotalViews());

//            Picasso.with(context)
//                    .load(list.get(position).getImage().replace(" ", "%20"))
//                    .placeholder(R.mipmap.placeholder)
//                    .into(((MyViewHolder) holder).imageView_gif);
//            Uri uri = Uri.parse(Constant.arrayListGIF.get(Integer.parseInt(s)).getImage().replace(" ","%20"));
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(list.get(position).getImage().replace(" ", "%20"))
                    .setAutoPlayAnimations(true)
                    .build();
            ((MyViewHolder) holder).imageView_gif.setController(controller);


            ((MyViewHolder) holder).imageView_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String image_id = list.get(position).getId();

                    ArrayList<ItemGIF> pojolist = dbHelper.getFavRowGIF(image_id);
                    if (pojolist.size() == 0) {
                        AddtoFav(position);//if size is zero i.e means that record not in database show add to favorite
                        ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.mipmap.fav_ind_active));
                    } else {
                        if (pojolist.get(0).getId().equals(image_id)) {
                            RemoveFav(position);
                            ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.mipmap.fav_ind));
                        }
                    }
                }
            });


            ((MyViewHolder) holder).imageView_gif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    SharedPreferences pref = context.getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
//                    SharedPreferences.Editor editor = pref.edit();
                    Constant.arrayListGIF.clear();
                    Constant.arrayListGIF.addAll(list);
                    Intent intslider = new Intent(context, SlideImageActivityGIF.class);
                    intslider.putExtra("POSITION_ID", position);
                    if(readFromFile(context).equals("yay")) {
                        writeToFile2(Integer.toString(position), context,"gif_selected.txt");
                        RequestActivity requestActivity= new RequestActivity();
                        requestActivity.finish();
                        Intent iner = new Intent(context, RequestActivity.class);
                        context.startActivity(iner);
                    }
                    else {
                        context.startActivity(intslider);
                    }
                }
            });
        }
//        else {
//            if(getItemCount()==1) {
//                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
//                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
//            }
//        }
    }
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("pos.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void writeToFile2(String data,Context context, String file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("isStGIF.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public int getItemCount() {
//        if(Constant.isFav) {
            return list.size();
//        } else {
//            return list.size() + 1;
//        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void FirstFav(int pos, ImageView imageView)
    {
        ArrayList<ItemGIF> pojolist = dbHelper.getFavRowGIF(list.get(pos).getId());
        if(pojolist.size()==0) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.fav_ind));
        }
        else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.fav_ind_active));

        }
    }

    public void RemoveFav(int position)
    {
        dbHelper.removeFavGIF(list.get(position).getId());
        Toast.makeText(context, context.getResources().getString(R.string.removed_fav), Toast.LENGTH_SHORT).show();
    }

    public void AddtoFav(int position)
    {
        dbHelper.addtoFavoriteGIF(list.get(position));
        Toast.makeText(context, context.getResources().getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
    }

//    public void hideHeader() {
//        ProgressViewHolder.progressBar.setVisibility(View.GONE);
//    }

    public boolean isHeader(int position) {
        return position == list.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
        return VIEW_ITEM;
    }

    public int getSelectedPosition(int pos){
        return pos;
    }
}