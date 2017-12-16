package com.viaviapp.hdwallpaper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

public class Settings extends AppCompatActivity {

    Switch switch_noti;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button button;
    JsonUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Constant.theme);
        setContentView(R.layout.activity_settings);

        utils = new JsonUtils(this);
        utils.forceRTLIfSupported(getWindow());

        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar_setting);
        toolbar.setTitle(getResources().getString(R.string.action_settings));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Constant.color);

        switch_noti = (Switch)findViewById(R.id.switch_noti);
        button = (Button)findViewById(R.id.button_color);
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        colorize();

        switch_noti.setChecked(Constant.isToggle);

        switch_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_noti.isChecked()) {
                    changeToggle(true);
                } else {
                    changeToggle(false);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorChooserDialog dialog = new ColorChooserDialog(Settings.this);
                dialog.setTitle(getResources().getString(R.string.select));
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        //do whatever you want to with the values
//                        imageView.setBackgroundColor(color);
                        colorize();
                        Constant.color = color;
//                        Constant.theme = R.style.AppTheme_red;
                        setColorTheme();
                        editor.putInt("color",color);
                        editor.putInt("theme",Constant.theme);
                        editor.putInt("draw",Constant.drawable);
                        editor.apply();

                        Intent intent = new Intent(Settings.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                //customize the dialog however you want
                dialog.show();
            }
        });

    }

    private void changeToggle(Boolean togg) {
        switch_noti.setChecked(togg);
        Constant.isToggle = togg;
        editor.putBoolean("noti",togg);
        editor.commit();
        Log.e("base",""+Constant.isToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void colorize() {

        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setBounds(58, 58, 58, 58);
//        button.setVisibility(View.INVISIBLE);

        d.getPaint().setStyle(Paint.Style.FILL);
        d.getPaint().setColor(Constant.color);

        button.setBackground(d);
    }

    public void setColorTheme() {
        switch (Constant.color) {
            case 0xffFF9801:
                Constant.theme = R.style.AppTheme_orange;
                Constant.drawable = R.drawable.bg_nav_theme_orange;
                break;
            case 0xffC035E1:
                Constant.theme = R.style.AppTheme_violet;
                Constant.drawable = R.drawable.bg_nav_theme_violet;
                break;
            case 0xff1E88E5:
                Constant.theme = R.style.AppTheme_blue;
                Constant.drawable = R.drawable.bg_nav_theme_blue;
                break;
            case 0xff45971B:
                Constant.theme = R.style.AppTheme_green;
                Constant.drawable = R.drawable.bg_nav_theme_green;
                break;
            case 0xff37A5AF:
                Constant.theme = R.style.AppTheme;
                Constant.drawable = R.drawable.bg_nav_theme1;
                break;
            default:
                Constant.theme = R.style.AppTheme_blue;
                Constant.drawable = R.drawable.bg_nav_theme_blue;
                break;
        }
    }
}
