package com.example.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.item.ItemAbout;
import com.example.item.ItemCategory;
import com.example.item.ItemGIF;
import com.example.item.ItemPhotos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "wallpaper.db";
    private SQLiteDatabase db;
    private final Context context;
    private String DB_PATH;
    String outFileName = "";
    SharedPreferences.Editor spEdit;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        //------------------------------------------------------------
        PackageInfo pinfo = null;
        if (!dbExist) {
            getReadableDatabase();
            copyDataBase();
        }

    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public Cursor getData(String Query) {
        String myPath = DB_PATH + DB_NAME;
        Cursor c = null;
        try {
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            c = db.rawQuery(Query, null);
        } catch (Exception e) {
            Log.e("Err", e.toString());
        }
        return c;
    }

    //UPDATE temp_dquot SET age='20',name1='--',rdt='11/08/2014',basic_sa='100000',plno='814',pterm='20',mterm='20',mat_date='11/08/2034',mode='YLY',dab_sa='100000',tr_sa='0',cir_sa='',bonus_rate='42',prem='5276',basic_prem='5118',dab_prem='100.0',step_rate='for Life',loyal_rate='0',bonus_rate='42',act_mat='1,88,000',mly_b_pr='448',qly_b_pr='1345',hly_b_pr='2664',yly_b_pr='5276'  WHERE uniqid=1
    public void dml(String Query) {
        String myPath = DB_PATH + DB_NAME;
        if (db == null)
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        try {
            db.execSQL(Query);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public ArrayList<ItemPhotos> getAllData(String table){
        ArrayList<ItemPhotos> arrayList = new ArrayList<ItemPhotos>();

        Cursor cursor = getData("select * from '"+table+"'");
        if(cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex("pid"));
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String img = cursor.getString(cursor.getColumnIndex("img"));
                String img_thumb = cursor.getString(cursor.getColumnIndex("img_thumb"));
                String views = cursor.getString(cursor.getColumnIndex("views"));

                ItemPhotos itemPhotos = new ItemPhotos(pid,cid,img,img_thumb,cname,views);
                if(!readFromFile2(context, "isLTS.txt").equals("yay")) {
                    arrayList.add(itemPhotos);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return arrayList;
    }
    private String readFromFile2(Context context,String name) {

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
    public ArrayList<ItemGIF> getAllDataGIF(){
        ArrayList<ItemGIF> arrayList = new ArrayList<ItemGIF>();

        Cursor cursor = getData("select * from gif");
        if(cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String gid = cursor.getString(cursor.getColumnIndex("gid"));
                String img = cursor.getString(cursor.getColumnIndex("image"));
                String views = cursor.getString(cursor.getColumnIndex("views"));

                ItemGIF itemGIF = new ItemGIF(gid,img,views);
                arrayList.add(itemGIF);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<ItemCategory> getAllDataCat(String table){
        ArrayList<ItemCategory> arrayList = new ArrayList<ItemCategory>();

        Cursor cursor = getData("select * from '"+table+"'");
        if(cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String img = cursor.getString(cursor.getColumnIndex("img"));
                String img_thumb = cursor.getString(cursor.getColumnIndex("img_thumb"));
                String tot_wall = cursor.getString(cursor.getColumnIndex("tot_wall"));

                ItemCategory itemCategory = new ItemCategory(cid,cname,img,img_thumb,tot_wall);
                arrayList.add(itemCategory);

                cursor.moveToNext();
            }
            cursor.close();
        }

        return arrayList;
    }

    public ArrayList<ItemPhotos> getFavRow(String id, String table)
    {
        ArrayList<ItemPhotos> dataList = new ArrayList<ItemPhotos>();
        // Select All Query
        String selectQuery = "SELECT  * FROM '"+table+"' WHERE pid="+"'"+id+"'";

        Cursor cursor = getData(selectQuery);

        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex("pid"));
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String img = cursor.getString(cursor.getColumnIndex("img"));
                String img_thumb = cursor.getString(cursor.getColumnIndex("img_thumb"));
                String views = cursor.getString(cursor.getColumnIndex("views"));

                ItemPhotos itemPhotos = new ItemPhotos(pid,cid,img,img_thumb,cname,views);
                dataList.add(itemPhotos);

                cursor.moveToNext();
            }
            cursor.close();
        }

        // return contact list
        return dataList;
    }

    public ArrayList<ItemGIF> getFavRowGIF(String id)
    {
        ArrayList<ItemGIF> dataList = new ArrayList<ItemGIF>();
        // Select All Query
        String selectQuery = "SELECT * FROM gif WHERE gid="+"'"+id+"'";

        Cursor cursor = getData(selectQuery);

        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String gid = cursor.getString(cursor.getColumnIndex("gid"));
                String img = cursor.getString(cursor.getColumnIndex("image"));
                String views = cursor.getString(cursor.getColumnIndex("views"));

                ItemGIF itemGIF = new ItemGIF(gid,img,views);
                dataList.add(itemGIF);

                cursor.moveToNext();
            }
            cursor.close();
        }

        // return contact list
        return dataList;
    }

    public ArrayList<ItemPhotos> getCatList(String id, String table)
    {
        ArrayList<ItemPhotos> dataList = new ArrayList<ItemPhotos>();
        // Select All Query
        String selectQuery = "SELECT  * FROM '"+table+"' WHERE cid="+"'"+id+"'";

        Cursor cursor = getData(selectQuery);

        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            for (int i=0; i<cursor.getCount(); i++) {
                String pid = cursor.getString(cursor.getColumnIndex("pid"));
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String img = cursor.getString(cursor.getColumnIndex("img"));
                String img_thumb = cursor.getString(cursor.getColumnIndex("img_thumb"));
                String views = cursor.getString(cursor.getColumnIndex("views"));

                ItemPhotos itemPhotos = new ItemPhotos(pid,cid,img,img_thumb,cname,views);
                dataList.add(itemPhotos);

                cursor.moveToNext();
            }
            cursor.close();
        }

        // return contact list
        return dataList;
    }

    public void addtoFavorite(ItemPhotos itemPhotos, String table) {
        dml("insert into '"+table+"' (pid,cid,cname,img,img_thumb,views) values ('"+itemPhotos.getId()+"','"+itemPhotos.getCatId()+"','"+itemPhotos.getCName()+"','"+itemPhotos.getImage()+"','"+itemPhotos.getImageThumb()+"','"+itemPhotos.getTotalViews()+"')");
    }

    public void addtoFavoriteGIF(ItemGIF itemGIF) {
        dml("insert into gif (gid,image,views) values ('"+itemGIF.getId()+"','"+itemGIF.getImage()+"','"+itemGIF.getTotalViews()+"')");
    }

    public void addtoCatList(ItemCategory itemCategory, String table) {
        dml("insert into '"+table+"' (cid,cname,img,img_thumb,tot_wall) values ('"+itemCategory.getId()+"','"+itemCategory.getName()+"','"+itemCategory.getImage()+"','"+itemCategory.getImageThumb()+"','"+itemCategory.getTotalWallpaper()+"')");
    }

    public void removeFav(String id) {
        dml("delete from fav where pid = '"+id+"'");
    }

    public void removeFavGIF(String id) {
        dml("delete from gif where gid = '"+id+"'");
    }

    public void updateView(String id, String totview) {
        int views = Integer.parseInt(totview) + 1;
        dml("update catlist set views = '"+String.valueOf(views)+"' where pid = '"+id+"'");
        dml("update fav set views = '"+String.valueOf(views)+"' where pid = '"+id+"'");
        dml("update latest set views = '"+String.valueOf(views)+"' where pid = '"+id+"'");
    }

    public void updateViewGIF(String id, String totview) {
        int views = Integer.parseInt(totview) + 1;
        dml("update gif set views = '"+String.valueOf(views)+"' where gid = '"+id+"'");
    }

    public void addtoAbout() {
        dml("delete from about");
        dml("insert into about (name,logo,version,author,contact,email,website,desc,developed,privacy) values (" +
            "'"+Constant.itemAbout.getAppName()+"','"+Constant.itemAbout.getAppLogo()+"','"+Constant.itemAbout.getAppVersion()+"'" +
            ",'"+Constant.itemAbout.getAuthor()+"','"+Constant.itemAbout.getContact()+"','"+Constant.itemAbout.getEmail()+"'" +
            ",'"+Constant.itemAbout.getWebsite()+"','"+Constant.itemAbout.getAppDesc()+"','"+Constant.itemAbout.getDevelopedby()+"'" +
            ",'"+Constant.itemAbout.getPrivacy()+"')");
    }

    public Boolean getAbout() {
        String selectQuery = "SELECT * FROM about";

        Cursor c = getData(selectQuery);

        if (c != null && c.getCount()>0) {
            c.moveToFirst();
            for (int i=0; i<c.getCount(); i++) {
                String appname = c.getString(c.getColumnIndex("name"));
                String applogo = c.getString(c.getColumnIndex("logo"));
                String desc = c.getString(c.getColumnIndex("desc"));
                String appversion = c.getString(c.getColumnIndex("version"));
                String appauthor = c.getString(c.getColumnIndex("author"));
                String appcontact = c.getString(c.getColumnIndex("contact"));
                String email = c.getString(c.getColumnIndex("email"));
                String website = c.getString(c.getColumnIndex("website"));
                String privacy = c.getString(c.getColumnIndex("privacy"));
                String developedby = c.getString(c.getColumnIndex("developed"));

                Constant.itemAbout = new ItemAbout(appname,applogo,desc,appversion,appauthor,appcontact,email,website,privacy,developedby);
            }
            c.close();
            return true;
        } else {
            return false;
        }
    }
}  