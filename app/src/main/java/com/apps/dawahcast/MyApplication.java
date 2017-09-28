package com.apps.dawahcast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.apps.utils.DBHelper;
import com.onesignal.OneSignal;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName="app";
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/futura_med.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getApplicationContext());
        OneSignal.startInit(getApplicationContext()).init();

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public MyApplication() {
        mInstance = this;
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.commit();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        if (preferences != null) {
            boolean flag = preferences.getBoolean(
                    "IsLoggedIn", false);
            return flag;
        }
        return false;
    }

    public void saveLogin(String user_id,String user_name,String email) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.commit();
    }
    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        if (preferences != null) {
            String user_id = preferences.getString(
                    "user_id", "");
            return user_id;
        }
        return "";
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        if (preferences != null) {
            String user_name = preferences.getString(
                    "user_name", "");
            return user_name;
        }
        return "";
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        if (preferences != null) {
            String user_email = preferences.getString(
                    "email", "");
            return user_email;
        }
        return "";
    }
}
