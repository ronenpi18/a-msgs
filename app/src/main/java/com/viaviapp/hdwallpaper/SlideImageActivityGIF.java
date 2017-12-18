package com.viaviapp.hdwallpaper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.item.ItemGIF;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SlideImageActivityGIF extends AppCompatActivity implements SensorEventListener {

    DBHelper dbHelper;
    int position;
    ViewPager viewpager;
    int TOTAL_IMAGE;
    private SensorManager sensorManager;
    private boolean checkImage = false;
    private long lastUpdate;
    Handler handler;
    Runnable Update;
    FloatingActionsMenu rightLabels;
    FloatingActionButton fabshare,fabsave,fabfav;
    private AdView mAdView;
    JsonUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_image_gif);
        setTheme(Constant.theme);

        utils = new JsonUtils(this);
        utils.forceRTLIfSupported(getWindow());

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DBHelper(this);

        // Look up the AdView as a resource and load a request.
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());

        Intent i=getIntent();
        position=i.getIntExtra("POSITION_ID", 0);

        loadViewed(position);

        TOTAL_IMAGE=Constant.arrayListGIF.size()-1;
        viewpager=(ViewPager)findViewById(R.id.image_sliderGIF);
        handler=new Handler();

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(position);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        fabshare = (FloatingActionButton)findViewById(R.id.fab_share);
        fabfav = (FloatingActionButton)findViewById(R.id.fab_fav);
        fabsave = (FloatingActionButton)findViewById(R.id.fab_save);

        FirstFav();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub

                position=viewpager.getCurrentItem();

                ArrayList<ItemGIF> pojolist = dbHelper.getFavRowGIF(Constant.arrayListGIF.get(position).getId());
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

                (new ShareGIF(SlideImageActivityGIF.this)).execute(Constant.arrayListGIF.get(position).getImage().replace(" ", "%20"));


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

                String image_id = Constant.arrayListGIF.get(position).getId();

                ArrayList<ItemGIF> pojolist = dbHelper.getFavRowGIF(image_id);
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

                (new SaveTask(SlideImageActivityGIF.this)).execute(Constant.arrayListGIF.get(position).getImage().replace(" ", "%20"));

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
        dbHelper.addtoFavoriteGIF(Constant.arrayListGIF.get(position));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.added_fav), Toast.LENGTH_SHORT).show();
        fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav_hover));
    }

    public void RemoveFav(int position) {
        dbHelper.removeFavGIF(Constant.arrayListGIF.get(position).getId());
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.removed_fav), Toast.LENGTH_SHORT).show();
        fabfav.setIconDrawable(getResources().getDrawable(R.drawable.fav));
    }

    public void FirstFav()
    {
        int first= viewpager.getCurrentItem();

        ArrayList<ItemGIF> pojolist= dbHelper.getFavRowGIF(Constant.arrayListGIF.get(first).getId());
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
            return Constant.arrayListGIF.size();

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_itemgif, container, false);
            assert imageLayout != null;
//			TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
            final SimpleDraweeView imageView = (SimpleDraweeView) imageLayout.findViewById(R.id.imagegif);

            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            new AsyncTask<String, String, String>() {
                float aspect_ratio;
                @Override
                protected String doInBackground(String... strings) {
                    final Bitmap image;
                    try {
                        image = Picasso.with(SlideImageActivityGIF.this).load(Constant.arrayListGIF.get(Integer.parseInt(strings[0])).getImage().replace(" ","%20")).get();
                        float width = image.getWidth();
                        float height = image.getHeight();
                        aspect_ratio = width/height;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return strings[0];
                }

                @Override
                protected void onPostExecute(String s) {
                    if(aspect_ratio > 1) {
                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        imageView.setAspectRatio(aspect_ratio);
                    }else if(aspect_ratio < 1) {
                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        imageView.setAspectRatio(aspect_ratio);
                    }
                    Uri uri = Uri.parse(Constant.arrayListGIF.get(Integer.parseInt(s)).getImage().replace(" ","%20"));
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(uri)
                            .setAutoPlayAnimations(true)
                            .build();
                    imageView.setController(controller);
                    super.onPostExecute(s);
                }
            }.execute(String.valueOf(position));

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
        sensorManager.registerListener(SlideImageActivityGIF.this,
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
        URL myFileUrl;
        File file ;

        public SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage(getResources().getString(R.string.download_gif));
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
                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File (filepath.getAbsolutePath() + Constant.DOWNLOAD_SDCARD_FOLDER_PATH_WALLPAPER+"/GIFs/");
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);

                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int current = 0;

                while ((current = bis.read()) != -1) {
                    baos.write(current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());

                fos.flush();

                fos.close();
                is.close();

                MediaScannerConnection.scanFile(SlideImageActivityGIF.this, new String[] { file.getAbsolutePath()},
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
            Toast.makeText(SlideImageActivityGIF.this, getResources().getString(R.string.gif_save_success), Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

//    public class ShareTask extends AsyncTask<String , String , String>
//    {
//        private Context context;
//        private ProgressDialog pDialog;
//        String image_url;
//        URL myFileUrl;
//
//
//        String myFileUrl1;
//        Bitmap bmImg = null;
//        File file ;
//
//        public ShareTask(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//
//            super.onPreExecute();
//
//            pDialog = new ProgressDialog(context,AlertDialog.THEME_HOLO_LIGHT);
//            pDialog.setMessage("Please Wait ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//
//        }
//
//        @Override
//        protected String doInBackground(String... args) {
//            // TODO Auto-generated method stub
//
//            try {
//
//                myFileUrl = new URL(args[0]);
//                //myFileUrl1 = args[0];
//
//                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//                InputStream is = conn.getInputStream();
//                bmImg = BitmapFactory.decodeStream(is);
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//            try {
//
//                String path = myFileUrl.getPath();
//                String idStr = path.substring(path.lastIndexOf('/') + 1);
//                File filepath = Environment.getExternalStorageDirectory();
//                File dir = new File (filepath.getAbsolutePath() + Constant.DOWNLOAD_SDCARD_FOLDER_PATH_WALLPAPER);
//                dir.mkdirs();
//                String fileName = idStr;
//                file = new File(dir, fileName);
//                FileOutputStream fos = new FileOutputStream(file);
//                bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
//                fos.flush();
//                fos.close();
//
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(String args) {
//            // TODO Auto-generated method stub
//
//            Intent share = new Intent(Intent.ACTION_SEND);
//            share.setType("image/jpeg");
//            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
//            startActivity(Intent.createChooser(share, "Share Image"));
//            pDialog.dismiss();
//        }
//    }

    public class ShareGIF extends AsyncTask<String , String , String>
    {
        private Context context;
        private ProgressDialog pDialog;
        String image_url;
        URL myFileUrl;


        String myFileUrl1;
        Bitmap bmImg = null;
        File file ;

        public ShareGIF(Context context) {
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
                file = new File(context.getExternalCacheDir(), "shared_gif" + System.currentTimeMillis() + ".gif");

                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();

                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] img = new byte[1024];


                int current = 0;

                while ((current = bis.read()) != -1) {
                    baos.write(current);
                }

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());

                fos.flush();

                fos.close();
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/gif");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            startActivity(Intent.createChooser(share, getResources().getString(R.string.share_image)));
            pDialog.dismiss();
        }
    }

    private void loadViewed(int pos) {
        if (JsonUtils.isNetworkAvailable(this)) {
            new MyTask().execute(Constant.URL_GIF+Constant.arrayListGIF.get(pos).getId(),String.valueOf(pos));
            dbHelper.updateViewGIF(Constant.arrayListGIF.get(pos).getId(), Constant.arrayListGIF.get(pos).getTotalViews());
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
            int tot = Integer.parseInt(Constant.arrayListGIF.get(p).getTotalViews());
            Constant.arrayListGIF.get(p).setTotalViews(""+(tot+1));
        }
    }


}
