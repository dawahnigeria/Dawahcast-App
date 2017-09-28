package com.apps.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.apps.item.ItemSong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "mp3.db";
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

    public void addToFav(ItemSong itemSong) {
        String a = DatabaseUtils.sqlEscapeString(itemSong.getDescription());
        String insert = "insert into song (sid,title,desc,artist,duration,url,image,image_small,cid,cname) values ('" + itemSong.getId() + "', '" +itemSong.getMp3Name() + "', " + a + ", '" +itemSong.getArtist() + "', '" + itemSong.getDuration() + "', '" + itemSong.getMp3Url() + "', '" + itemSong.getImageBig() + "', '" + itemSong.getImageSmall() + "', '" + itemSong.getCategoryId() + "', '" + itemSong.getCategoryName() + "')";
        dml(insert);
    }

    public void addToRecent(ItemSong itemSong) {
        if(checkRecent(itemSong.getId())) {
            dml("delete from recent where sid = '"+itemSong.getId()+"'");

        }
        String a = DatabaseUtils.sqlEscapeString(itemSong.getDescription());
        String insert = "insert into recent (sid,title,desc,artist,duration,url,image,image_small,cid,cname) values ('" + itemSong.getId() + "', '" + itemSong.getMp3Name() + "', " + a + ", '" + itemSong.getArtist() + "', '" + itemSong.getDuration() + "', '" + itemSong.getMp3Url() + "', '" + itemSong.getImageBig() + "', '" + itemSong.getImageSmall() + "', '" + itemSong.getCategoryId() + "', '" + itemSong.getCategoryName() + "')";
        dml(insert);
    }

    public void removeFromFav(String id) {
        String delete = "delete from song where sid = '"+id+"'";
        dml(delete);
    }

    public void removeFromRecent(String id) {
        String delete = "delete from recent where sid = '"+id+"'";
        dml(delete);
    }

    public Boolean checkFav(String id) {
        String select = "select * from song where sid = '"+id+"'";
        Cursor cursor = getData(select);
        if(cursor!=null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkRecent(String id) {
        String select = "select * from recent where sid = '"+id+"'";
        Cursor cursor = getData(select);
        if(cursor!=null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<ItemSong> loadData() {
        ArrayList<ItemSong> arrayList = new ArrayList<>();
        String select = "select * from song";
        Cursor cursor = getData(select);
        if(cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for(int i=0; i<cursor.getCount();i++) {

                String id = cursor.getString(cursor.getColumnIndex("sid"));
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                String name = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String desc = cursor.getString(cursor.getColumnIndex("desc"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                String imagebig = cursor.getString(cursor.getColumnIndex("image"));
                String imagesmall = cursor.getString(cursor.getColumnIndex("image_small"));

                ItemSong objItem = new ItemSong(id,cid,cname,artist,url,imagebig,imagesmall,name,duration,desc,desc);
                arrayList.add(objItem);

                cursor.moveToNext();
            }
        }
        return arrayList;
    }

    public ArrayList<ItemSong> loadDataRecent() {
        ArrayList<ItemSong> arrayList = new ArrayList<>();
        String select = "select * from recent";
        Cursor cursor = getData(select);
        if(cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int a = 0;
            if(cursor.getCount() > 20) {
                a = 20;
            } else {
                a = cursor.getCount();
            }
            for(int i=0; i<a; i++) {

                String id = cursor.getString(cursor.getColumnIndex("sid"));
                String cid = cursor.getString(cursor.getColumnIndex("cid"));
                String cname = cursor.getString(cursor.getColumnIndex("cname"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                String name = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String desc = cursor.getString(cursor.getColumnIndex("desc"));
                String duration = cursor.getString(cursor.getColumnIndex("duration"));
                String imagebig = cursor.getString(cursor.getColumnIndex("image"));
                String imagesmall = cursor.getString(cursor.getColumnIndex("image_small"));

                ItemSong objItem = new ItemSong(id,cid,cname,artist,url,imagebig,imagesmall,name,duration,desc,desc);
                arrayList.add(objItem);

                cursor.moveToNext();
            }
            Collections.reverse(arrayList);
        }
        return arrayList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}  