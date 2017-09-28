package com.apps.dawahcast;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;

public class SplashActivity extends AppCompatActivity {

    MyApplication App;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        hideStatusBar();
        setStatusColor();
        App = MyApplication.getInstance();
        try {
           Constant.isFromPush = getIntent().getExtras().getBoolean("ispushnoti", false);
           Constant.pushID = getIntent().getExtras().getString("noti_nid");
        } catch (Exception e) {
            Constant.isFromPush = false;
        }
        try {
            Constant.isFromNoti = getIntent().getExtras().getBoolean("isnoti", false);
        } catch (Exception e) {
            Constant.isFromNoti = false;
        }

        JsonUtils jsonUtils = new JsonUtils(SplashActivity.this);

        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constant.GRID_PADDING, r.getDisplayMetrics());
        Constant.columnWidth = (int) ((jsonUtils.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);

        if(!Constant.isFromNoti) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openMainActivity();
                }
            }, 2000);
        } else {
            openMainActivity();
        }
    }

    private void openMainActivity() {
        if(App.getIsLogin())
        {
            Intent int1=new Intent(getApplicationContext(),MainActivity.class);
            int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(int1);
            finish();
        }
        else
        {
            Intent intent = new Intent(SplashActivity.this,ActivityLogin.class);
            startActivity(intent);
            finish();
        }

    }

    public void setStatusColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
    }
}