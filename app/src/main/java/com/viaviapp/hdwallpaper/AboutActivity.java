package com.viaviapp.hdwallpaper;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.item.ItemAbout;
import com.example.util.Constant;
import com.example.util.DBHelper;
import com.example.util.JsonUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    WebView webView;
    TextView textView_appname,textView_email,textView_website, textView_company, textView_contact, textView_version;
    ImageView imageView_logo;
    LinearLayout ll_email, ll_website, ll_company, ll_contact;
    String website,email, desc, applogo, appname, appversion, appauthor, appcontact, privacy, developedby;
    DBHelper dbHelper;
    ProgressDialog pbar;
    JsonUtils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_about);

        dbHelper = new DBHelper(this);
        utils = new JsonUtils(this);
        utils.forceRTLIfSupported(getWindow());

        toolbar = (Toolbar) this.findViewById(R.id.toolbar_about);
        toolbar.setTitle(getString(R.string.menu_about));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Constant.color);

        pbar = new ProgressDialog(this);
        pbar.setMessage(getResources().getString(R.string.loading));
        pbar.setCancelable(false);

        webView = (WebView) findViewById(R.id.webView);
        textView_appname = (TextView)findViewById(R.id.textView_about_appname);
        textView_email = (TextView)findViewById(R.id.textView_about_email);
        textView_website = (TextView)findViewById(R.id.textView_about_site);
        textView_company = (TextView)findViewById(R.id.textView_about_company);
        textView_contact = (TextView)findViewById(R.id.textView_about_contact);
        textView_version = (TextView)findViewById(R.id.textView_about_appversion);
        imageView_logo = (ImageView)findViewById(R.id.imageView_about_logo);

        ll_email = (LinearLayout)findViewById(R.id.ll_email);
        ll_website = (LinearLayout)findViewById(R.id.ll_website);
        ll_contact = (LinearLayout)findViewById(R.id.ll_contact);
        ll_company = (LinearLayout)findViewById(R.id.ll_company);

//		textView_appname.setText(getResources().getString(R.string.app_name));
//        textView_email.setText(getResources().getString(R.string.aboutus_email));
//        ll_email.setVisibility(View.VISIBLE);
////        textView_website.setText(getResources().getString(R.string.aboutus_website));
//        textView_desc.setText(getResources().getString(R.string.aboutus_desc));
//		imageView_logo.setImageDrawable(getResources().getDrawable(R.drawable.about_logo));

        if(Constant.itemAbout == null) {
            if (JsonUtils.isNetworkAvailable(AboutActivity.this)) {
                new MyTask().execute(Constant.URL_ABOUT_US);
            } else {
                if(!dbHelper.getAbout()) {
                    Toast.makeText(AboutActivity.this, getResources().getString(R.string.first_load_internet), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            appname = Constant.itemAbout.getAppName();
            applogo = Constant.itemAbout.getAppLogo();
            desc = Constant.itemAbout.getAppDesc();
            appversion = Constant.itemAbout.getAppVersion();
            appauthor = Constant.itemAbout.getAuthor();
            appcontact = Constant.itemAbout.getContact();
            email = Constant.itemAbout.getEmail();
            website = Constant.itemAbout.getWebsite();
            privacy = Constant.itemAbout.getPrivacy();
            developedby = Constant.itemAbout.getDevelopedby();
            setVariables();
        }
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

    public void showToast(String msg) {
        Toast.makeText(AboutActivity.this, msg, Toast.LENGTH_LONG).show();
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
                Toast.makeText(AboutActivity.this, getResources().getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject  c = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        c = jsonArray.getJSONObject(i);

                        appname = c.getString("app_name");
                        applogo = c.getString("app_logo");
                        desc = c.getString("app_description");
                        appversion = c.getString("app_version");
                        appauthor = c.getString("app_author");
                        appcontact = c.getString("app_contact");
                        email = c.getString("app_email");
                        website = c.getString("app_website");
                        privacy = c.getString("app_privacy_policy");
                        developedby = c.getString("app_developed_by");

                        Constant.itemAbout = new ItemAbout(appname,applogo,desc,appversion,appauthor,appcontact,email,website,privacy,developedby);
                        dbHelper.addtoAbout();
                    }

                    setVariables();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setVariables() {
        textView_appname.setText(appname);
        if(!email.trim().isEmpty()) {
            ll_email.setVisibility(View.VISIBLE);
            textView_email.setText(email);
        }

        if(!website.trim().isEmpty()) {
            ll_website.setVisibility(View.VISIBLE);
            textView_website.setText(website);
        }

        if(!appauthor.trim().isEmpty()) {
            ll_company.setVisibility(View.VISIBLE);
            textView_company.setText(appauthor);
        }

        if(!appcontact.trim().isEmpty()) {
            ll_contact.setVisibility(View.VISIBLE);
            textView_contact.setText(appcontact);
        }

        if(!appversion.trim().isEmpty()) {
            textView_version.setText(appversion);
        }
//		textView_desc.setText(desc);
        if(applogo.trim().isEmpty()) {
            imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .with(AboutActivity.this)
                    .load(Constant.URL_ABOUT_US_LOGO+applogo)
                    .into(imageView_logo);
        }

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text = "<html><head>"
                + "<style> body{color: #000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + desc
                + "</body></html>";

        webView.loadData(text, mimeType, encoding);
    }

    @Override
    protected void onResume() {
        EasyTracker.setResourcePackageName("AboutActivity");
        super.onResume();
    }
}
