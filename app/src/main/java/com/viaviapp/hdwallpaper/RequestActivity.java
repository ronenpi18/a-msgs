package com.viaviapp.hdwallpaper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.GMailSender;
import com.example.util.Mail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class RequestActivity extends AppCompatActivity {

    EditText email, name, phone, details;
    Button  wallpaper, gifs,send;
    TextView text, textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        text = (TextView)findViewById(R.id.texta);
        textView = (TextView)findViewById(R.id.text);
        email = (EditText)findViewById(R.id.email);
        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        details = (EditText)findViewById(R.id.details);
        gifs = (Button)findViewById(R.id.gifC);
        send = (Button)findViewById(R.id.Send);
        wallpaper = (Button)findViewById(R.id.wallC);


        if(!readFromFile(getApplicationContext(),"email_data.txt").equals("") ||
                !readFromFile(getApplicationContext(),"name_data.txt").equals("") ||
                !readFromFile(getApplicationContext(),"phone_data.txt").equals("") ||
                !readFromFile(getApplicationContext(),"details_data.txt").equals("")){

            email.setText(readFromFile(getApplicationContext(),"email_data.txt"));
            name.setText(readFromFile(getApplicationContext(),"name_data.txt"));
            phone.setText(readFromFile(getApplicationContext(),"phone_data.txt"));
            details.setText(readFromFile(getApplicationContext(),"details_data.txt"));
        }
        if(!readFromFile(getApplicationContext(),"gif_selected.txt").equals("")){
            wallpaper.setEnabled(false);
            wallpaper.setVisibility(View.GONE);
            gifs.setEnabled(false);
            gifs.setVisibility(View.GONE);
            text.setText("You've chosen GIF! Great!");
            textView.setVisibility(View.GONE);
        }
        if(!readFromFile(getApplicationContext(),"wall_selected.txt").equals("")){
            wallpaper.setEnabled(false);
            wallpaper.setVisibility(View.GONE);
            gifs.setEnabled(false);
            gifs.setVisibility(View.GONE);
            text.setText("You've chosen WallPaper! Great!");
            textView.setVisibility(View.GONE);

        }

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

        GIFFragment f1 = new GIFFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, f1);
        fragmentTransaction.addToBackStack(f1.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
        send.setVisibility(View.GONE);
        gifs.setVisibility(View.GONE);
        wallpaper.setVisibility(View.GONE);
        f1.setArguments(bundle);
//        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putBoolean("con", true);
        writeToFile("yay",getApplicationContext(),"isStGIF.txt");
        writeToFile(email.getText().toString(),getApplicationContext(),"email_data.txt");
        writeToFile(name.getText().toString(),getApplicationContext(),"name_data.txt");
        writeToFile(phone.getText().toString(),getApplicationContext(),"phone_data.txt");
        writeToFile(details.getText().toString(),getApplicationContext(),"details_data.txt");

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
        gifs.setVisibility(View.GONE);
        wallpaper.setVisibility(View.GONE);

        f1.setArguments(bundle);

        writeToFile("yay",getApplicationContext(),"isStWall.txt");
        writeToFile(email.getText().toString(),getApplicationContext(),"email_data.txt");
        writeToFile(name.getText().toString(),getApplicationContext(),"name_data.txt");
        writeToFile(phone.getText().toString(),getApplicationContext(),"phone_data.txt");
        writeToFile(details.getText().toString(),getApplicationContext(),"details_data.txt");
    }


    private void writeToFile(String data,Context context,String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private String readFromFile(Context context,String name) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(name);

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

    public void showButtons(){
        send.setVisibility(View.VISIBLE);
        gifs.setVisibility(View.VISIBLE);
        wallpaper.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeToFile("",getApplicationContext(),"email_data.txt");
        writeToFile("",getApplicationContext(),"name_data.txt");
        writeToFile("",getApplicationContext(),"phone_data.txt");
        writeToFile("",getApplicationContext(),"details_data.txt");
        writeToFile("",getApplicationContext(),"gif_selected.txt");
        writeToFile("",getApplicationContext(),"wall_selected.txt");
        writeToFile("",getApplicationContext(),"isStGIF.txt");
        writeToFile("",getApplicationContext(),"isStWall.txt");

    }

    public String mailData(String mEmail ,String mPhone, String mDetails,String mSelectedGif,String mSelectedWall){
        String data = "the data of the request:\n\r"+ "email:"+mEmail+"\nphone number:"+mPhone +"\nDetails:"+mDetails;
        if(!mSelectedGif.equals("")||mSelectedGif!=null){
            data+="\nselected Gif -> id = "+ mSelectedGif;
        }
        if(!mSelectedWall.equals("")||mSelectedWall!=null){
            data+="\nselected Wallpaper -> id= "+ mSelectedWall;
        }
        return data;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        writeToFile("",getApplicationContext(),"email_data.txt");
        writeToFile("",getApplicationContext(),"name_data.txt");
        writeToFile("",getApplicationContext(),"phone_data.txt");
        writeToFile("",getApplicationContext(),"details_data.txt");
        writeToFile("",getApplicationContext(),"gif_selected.txt");
        writeToFile("",getApplicationContext(),"wall_selected.txt");
        writeToFile("",getApplicationContext(),"isStGIF.txt");
        writeToFile("",getApplicationContext(),"isStWall.txt");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        send.setEnabled(true);
        send.setVisibility(View.VISIBLE);
        wallpaper.setEnabled(true);
        wallpaper.setVisibility(View.VISIBLE);
        gifs.setEnabled(true);
        gifs.setVisibility(View.VISIBLE);

    }

    public void onClickSend(View view){
        open();
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(RequestActivity.this,"thanks",Toast.LENGTH_LONG).show();
                                //new SendMail().execute("");
                                Thread thread=new Thread(){
                                    @Override
                                    public void run() {

                                        GMailSender sender = new GMailSender("alivemessages1@gmail.com","PasswordForMail");
                                        try {
                                            sender.sendMail("New Request",
                                                    mailData (readFromFile( getApplicationContext(),"email_data.txt"),
                                                              readFromFile(getApplicationContext(),"phone_data.txt"),
                                                              readFromFile(getApplicationContext(),"details_data.txt"),
                                                              readFromFile(getApplicationContext(),"gif_selected.txt"),
                                                              readFromFile(getApplicationContext(),"wall_selected.txt")),
                                                    "alivemessages1@gmail.com", "alivemessages1@gmail.com",
                                                    "");
                                            sender.sendMail("Alive messages - request","Hello\nThanks a lot for using Alive Messages. we promise to give you the top of the best. \nwe'll be in touch for further information... \n\nAliveMessages Team"
                                            , "alivemessages1@gmail.com",readFromFile( getApplicationContext(),"email_data.txt"),"");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }finally {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            writeToFile("",getApplicationContext(),"email_data.txt");
                                            writeToFile("",getApplicationContext(),"name_data.txt");
                                            writeToFile("",getApplicationContext(),"phone_data.txt");
                                            writeToFile("",getApplicationContext(),"details_data.txt");
                                            writeToFile("",getApplicationContext(),"gif_selected.txt");
                                            writeToFile("",getApplicationContext(),"wall_selected.txt");
                                            writeToFile("",getApplicationContext(),"isStGIF.txt");
                                            writeToFile("",getApplicationContext(),"isStWall.txt");

                                        }

                                    }
                                };
                                thread.start();


                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
//                writeToFile("",getApplicationContext(),"gif_selected.text");
//                writeToFile("",getApplicationContext(),"wall_selected.text");
                writeToFile("",getApplicationContext(),"email_data.txt");
                writeToFile("",getApplicationContext(),"name_data.txt");
                writeToFile("",getApplicationContext(),"phone_data.txt");
                writeToFile("",getApplicationContext(),"details_data.txt");
                writeToFile("",getApplicationContext(),"gif_selected.txt");
                writeToFile("",getApplicationContext(),"wall_selected.txt");
                writeToFile("",getApplicationContext(),"isStGIF.txt");
                writeToFile("",getApplicationContext(),"isStWall.txt");
                //

                startActivity(new Intent(RequestActivity.this, MainActivity.class));
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
//    private class SendMail extends AsyncTask<String, Integer, Void> {
//
//        private ProgressDialog progressDialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = ProgressDialog.show(RequestActivity.this, "Please wait", "Sending mail", true, false);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
//        }
//
//        protected Void doInBackground(String... params) {
//            Mail m = new Mail("ronenpi18@gmail.com", "rubi;nxpr;1");
//
//            String[] toArr = {"ronenpi1802@gmail.com", "ronenpi18@gmail.com"};
//            m.setTo(toArr);
//            m.setFrom("youremail@gmail");
//            m.setSubject("New Requset");
//            m.setBody(mailData (readFromFile( getApplicationContext(),"email_data.txt"),
//                    readFromFile(getApplicationContext(),"phone_data.txt"),
//                    readFromFile(getApplicationContext(),"details_data.txt"),
//                    readFromFile(getApplicationContext(),"gif_selected.txt"),
//                    readFromFile(getApplicationContext(),"wall_selected.txt")));
//
//            try {
//                if(m.send()) {
//                    Toast.makeText(RequestActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(RequestActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
//                }
//            } catch(Exception e) {
//                Log.e("MailApp", "Could not send email", e);
//            }
//            return null;
//        }
//    }
}