package com.viaviapp.hdwallpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContactActivity extends AppCompatActivity {
    FragmentManager fm;
    Button send;
    Button chooseGif , chooseWall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        send = (Button)findViewById(R.id.button3);
        chooseGif = (Button)findViewById(R.id.button4);
        chooseWall = (Button)findViewById(R.id.button2);

    }
    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame_layout, f1, name);
        ft.commit();
    }


    public void onClick(View view) {
//        int id = view.getId();
        Bundle bundle = new Bundle();
        bundle.putString("choose", "started");
//// set MyFragment Arguments
        GIFFragment f1 = new GIFFragment();
//        Intent i = new Intent(this,MainActivity.class);
//        startActivity(i);
//        loadFrag(f1,"gif",fm);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, f1);
        fragmentTransaction.addToBackStack(f1.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        send.setVisibility(View.GONE);
        chooseWall.setVisibility(View.GONE); chooseGif.setVisibility(View.GONE);
        f1.setArguments(bundle);
        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("con", true);

    }

    public void onClickWall(View view) {
//        int id = view.getId();
        Bundle bundle = new Bundle();
        bundle.putString("choose", "started");
//// set MyFragment Arguments
        LatestFragment f1 = new LatestFragment();
//        Intent i = new Intent(this,MainActivity.class);
//        startActivity(i);
//        loadFrag(f1,"gif",fm);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, f1);
        fragmentTransaction.addToBackStack(f1.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        send.setVisibility(View.GONE);
        chooseWall.setVisibility(View.GONE); chooseGif.setVisibility(View.GONE);

        f1.setArguments(bundle);
        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("con", true);

    }


}

