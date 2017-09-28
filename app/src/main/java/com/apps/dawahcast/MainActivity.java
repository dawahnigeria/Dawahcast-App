package com.apps.dawahcast;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.item.ItemAbout;
import com.apps.item.ItemSong;
import com.apps.utils.Constant;
import com.apps.utils.DBHelper;
import com.apps.utils.JsonUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    FragmentManager fm;
    private NavigationView navigationView;
    private TextView textView;

    private DBHelper dbHelper;
    public RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    public RelativeLayout rl_dragview;

    private Handler seekHandler = new Handler();
    public RelativeLayout rl_loading, rl_topviewone;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;

    public TextView txt_songname, txt_artistname, txt_song_no, txt_totaltime, txt_duration, txt_artist_small, txt_song_small,
            txt_playesongname_slidetoptwo, txt_songartistname_slidetoptwo;

    public ImageView imageView_backward, imageView_download, imageView_volume, imageView_forward, imageView_shuffle, imageView_repeat, imageView_playpause,
            imageView_Favorite, imageView_heart, imageView_share;

    public View view_round;

    public ImageView btn_playpausePanel;
    public AppCompatSeekBar seekBar;
    public ViewPager viewpager;
    ImagePagerAdapter adapter;
    SlidingUpPanelLayout mLayout;
    AudioManager am;
    InterstitialAd mInterstitial;
    Handler mExitHandler = new Handler();
    Boolean mRecentlyBackPressed = false;
    String website, email, desc, applogo, appname, appversion, appauthor, appcontact, privacy, developedby;
    MyApplication App;
    private GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    String mp3url;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(this);
        Constant.isAppOpen = true;
        Constant.context = MainActivity.this;

        setStatusColor();
        loadInter();
        App = MyApplication.getInstance();
        if (Constant.itemAbout == null) {
            Constant.itemAbout = new ItemAbout("", "", "", "", "", "", "", "", "", "");
        }

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        txt_duration = (TextView) findViewById(R.id.slidepanel_time_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fm = getSupportFragmentManager();

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.apps.onlinemp3", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset == 0) {
//                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                } else {
//                    slidepanelchildtwo_topviewone.setVisibility(View.GONE);
                }
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        ;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        textView = (TextView) findViewById(R.id.textView_developedby);
        textView.setText("Developed By - " + Constant.itemAbout.getDevelopedby());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        View headerView = navigationView.getHeaderView(0);
//        navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
//        headerView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
//        navigationView.getBackground().setAlpha(100);

        FragmentHome f1 = new FragmentHome();
        loadFrag(f1, "Home", fm);
        getSupportActionBar().setTitle("Home");

        initiSlidingUpPanel();

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Constant.isScrolled = true;
            }

            @Override
            public void onPageSelected(int position) {
                Constant.playPos = position;
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_FIRST_PLAY);
                startService(intent);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        dbHelper = new DBHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Constant.isFromNoti) {
            changePlayPauseIcon(Constant.isPlaying);
            changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getCategoryName(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getImageBig(), "cat");
            seekUpdation();
        } else if (Constant.isFromPush) {
            if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
                new LoadSong().execute(Constant.URL_SONG + Constant.pushID);
            } else {

            }
        } else if (Constant.isPlaying) {
            changePlayPauseIcon(Constant.isPlaying);
            changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(), Constant.arrayList_play.get(Constant.playPos).getCategoryName(), Constant.playPos + 1, Constant.arrayList_play.size(), Constant.arrayList_play.get(Constant.playPos).getDuration(), Constant.arrayList_play.get(Constant.playPos).getImageBig(), "cat");
            seekUpdation();
        }

        if (Constant.itemAbout.getAppName().equals("")) {
            if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
                new MyTask().execute(Constant.APP_DETAILS_URL);
            }
        }

        checkPer();
    }

    Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mRecentlyBackPressed = false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (Constant.isBackStack) {
            Constant.isBackStack = false;
            super.onBackPressed();
        } else {
            if (mRecentlyBackPressed) {
                mExitHandler.removeCallbacks(mExitRunnable);
//            mExitHandler = null;
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    }
                mRecentlyBackPressed = false;
                moveTaskToBack(true);
            } else {
                mRecentlyBackPressed = true;
                Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show();
                mExitHandler.postDelayed(mExitRunnable, 2000L);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        item.setChecked(true);
//        item.setCheckable(true);
//        navigationView.setCheckedItem(item.getItemId());
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentHome fh = new FragmentHome();
            loadFrag(fh, "Home", fm);
            item.setCheckable(true);

        } else if (id == R.id.nav_cat) {
            FragmentCat fcat = new FragmentCat();
            loadFrag(fcat, "Categories", fm);
            item.setCheckable(true);
        } else if (id == R.id.nav_artist) {
            FragmentArtist fart = new FragmentArtist();
            loadFrag(fart, "Artist", fm);
            item.setCheckable(true);
        } else if (id == R.id.nav_fav) {
            FragmentFav ffav = new FragmentFav();
            loadFrag(ffav, "Favourite", fm);
            item.setCheckable(true);
        } else if (id == R.id.nav_ytvideo) {

            Intent intentvideo = new Intent(MainActivity.this, ActivityVideoCat.class);
            startActivity(intentvideo);
            if (Constant.isPlaying) {
                Constant.isPlaying = false;
                ((MainActivity) Constant.context).changePlayPauseIcon(Constant.isPlaying);
                Constant.exoPlayer.setPlayWhenReady(false);

                Intent closeIntent = new Intent(this, PlayerService.class);
                closeIntent.setAction(PlayerService.ACTION_STOP);
                PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                        closeIntent, 0);
            }

        } else if (id == R.id.nav_rate) {
            final String appName = getPackageName();//your application package name i.e play store application url
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + appName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id="
                                + appName)));
            }
        } else if (id == R.id.nav_share) {
            Intent ishare = new Intent(Intent.ACTION_SEND);
            ishare.setType("text/plain");
            ishare.putExtra(Intent.EXTRA_TEXT, "Dawahgeria App - http://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(ishare);
        } else if (id == R.id.nav_more) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);


        } else if ( id == R.id.nav_live) {
            Toast.makeText(getApplicationContext(),"Coming Soon!",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_privacy) {
            openPrivacyDialog();
        } else if (id == R.id.nav_logout) {
            MyApplication.getInstance().saveIsLogin(false);
            LoginManager.getInstance().logOut();
            signOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        if (Constant.isBackStack) {
            FragmentManager fragm = getSupportFragmentManager();
            for (int i = 0; i < fragm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            Constant.isBackStack = false;
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment, f1, name);
        ft.commit();

        getSupportActionBar().setTitle(name);

        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (!Constant.isPlaying) {
            stopService(new Intent(getApplicationContext(), PlayerService.class));
        }
        Constant.isAppOpen = false;
        super.onDestroy();
    }

    public void openPrivacyDialog() {
        Dialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            dialog = new Dialog(MainActivity.this);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy);

        WebView webview = (WebView) dialog.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        if (Constant.itemAbout != null) {
            String text = "<html><head>"
                    + "<style> body{color: #000 !important;text-align:left}"
                    + "</style></head>"
                    + "<body>"
                    + Constant.itemAbout.getPrivacy()
                    + "</body></html>";

            webview.loadData(text, mimeType, encoding);
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void initiSlidingUpPanel() {
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        rl_topviewone = (RelativeLayout) findViewById(R.id.rl_topviewone);
        rl_dragview = (RelativeLayout) findViewById(R.id.include_sliding_panel_childtwo);
        view_round = findViewById(R.id.vBgLike);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);


        txt_totaltime = (TextView) findViewById(R.id.slidepanel_time_total);
        txt_songname = (TextView) findViewById(R.id.textView_songname_full);
        txt_artistname = (TextView) findViewById(R.id.textView_artistname_full);
        txt_song_no = (TextView) findViewById(R.id.textView_song_count);

        imageView_backward = (ImageView) findViewById(R.id.btn_backward);
        imageView_forward = (ImageView) findViewById(R.id.btn_forward);
        imageView_repeat = (ImageView) findViewById(R.id.btn_repeat);
        imageView_shuffle = (ImageView) findViewById(R.id.btn_shuffle);
        imageView_playpause = (ImageView) findViewById(R.id.btn_play);
        imageView_download = (ImageView) findViewById(R.id.imageView_download);
        imageView_volume = (ImageView) findViewById(R.id.imageView_volume);
        imageView_heart = (ImageView) findViewById(R.id.ivLike);

        adapter = new ImagePagerAdapter();
        viewpager = (ViewPager) findViewById(R.id.viewPager_song);
        viewpager.setPadding(100, 0, 100, 0);
        viewpager.setClipToPadding(false);
        viewpager.setPageMargin(50);
        viewpager.setClipChildren(false);

        seekBar = (AppCompatSeekBar) findViewById(R.id.audio_progress_control);

        btn_playpausePanel = (ImageView) findViewById(R.id.bottombar_play);
        imageView_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);
        imageView_share = (ImageView) findViewById(R.id.bottombar_shareicon);

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        seekBar.setProgress(0);

        imageView_backward.setOnClickListener(this);
        imageView_forward.setOnClickListener(this);
        imageView_repeat.setOnClickListener(this);
        imageView_shuffle.setOnClickListener(this);
        imageView_Favorite.setOnClickListener(this);
        imageView_share.setOnClickListener(this);
        imageView_playpause.setOnClickListener(this);
        btn_playpausePanel.setOnClickListener(this);
        imageView_download.setOnClickListener(this);
        imageView_volume.setOnClickListener(this);


        txt_artist_small = (TextView) findViewById(R.id.txt_artist_small);
        txt_song_small = (TextView) findViewById(R.id.txt_songname_small);

        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        findViewById(R.id.bottombar_play).setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
                    intent.setAction(PlayerService.ACTION_SEEKTO);
                    intent.putExtra("seekto", JsonUtils.getSeekFromPercentage(progress, JsonUtils.calculateTime(Constant.arrayList_play.get(Constant.playPos).getDuration())));
                    startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (slideOffset == 0.0f) {
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {

                } else {
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottombar_img_Favorite:
                JsonUtils.animateHeartButton(view);
                JsonUtils.animatePhotoLike(view_round, imageView_heart);
                view.setSelected(!view.isSelected());
                findViewById(R.id.ivLike).setSelected(view.isSelected());
                fav();
                break;
            case R.id.bottombar_play:
                playPause();
                break;
            case R.id.btn_play:
                playPause();
                break;
            case R.id.btn_shuffle:
                setShuffle();
                break;
            case R.id.btn_repeat:
                setRepeat();
                break;
            case R.id.btn_forward:
                next();
                break;
            case R.id.btn_backward:
                previous();
                break;
            case R.id.bottombar_shareicon:
                shareSong();
                break;
            case R.id.imageView_download:
                download();
                break;
            case R.id.imageView_volume:
                changeVolume();
                break;
        }
    }

    public void changeText(String sname, String aname, int pos, int total, String totaltime, final String image, String page) {
        txt_artist_small.setText(aname);
        txt_song_small.setText(sname);

        txt_duration.setText("00:00");
        txt_totaltime.setText("--:--");
        txt_artistname.setText(aname);
        txt_songname.setText(sname);
        txt_song_no.setText(pos + "/" + total);

        txt_playesongname_slidetoptwo.setText(sname);
        txt_songartistname_slidetoptwo.setText(aname);

        Picasso.with(MainActivity.this)
                .load(image)
                .into(img_bottom_slidetwo);

        Picasso.with(MainActivity.this)
                .load(image)
                .into(img_bottom_slideone);

        checkFav();

        if (!page.equals(Constant.loadedSongPage) || !page.equals("")) {
            viewpager.setAdapter(adapter);
        }

//        Log.e("scroll",String.valueOf(Constant.isScrolled));
//        if(!Constant.isScrolled) {
        viewpager.setCurrentItem(Constant.playPos);
//        } else {
//            Constant.isScrolled = false;
//        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        seekBar.setProgress(JsonUtils.getProgressPercentage(Constant.exoPlayer.getCurrentPosition(), Constant.exoPlayer.getDuration()));
        txt_duration.setText(JsonUtils.milliSecondsToTimer(Constant.exoPlayer.getCurrentPosition()));
        if (Constant.isPlaying)
        txt_totaltime.setText(JsonUtils.milliSecondsToTimer(Constant.exoPlayer.getDuration()));
        seekBar.setSecondaryProgress(Constant.exoPlayer.getBufferedPercentage());
        if (Constant.isPlaying && Constant.isAppOpen) {
            seekHandler.postDelayed(run, 500);
        }
    }

    public void isBuffering(Boolean isBuffer) {
        Constant.isPlaying = !isBuffer;
        if (isBuffer) {
            rl_loading.setVisibility(View.VISIBLE);
            imageView_playpause.setVisibility(View.INVISIBLE);
        } else {
            rl_loading.setVisibility(View.INVISIBLE);
            imageView_playpause.setVisibility(View.VISIBLE);
            imageView_playpause.setImageDrawable(getResources().getDrawable(R.drawable.selector_pause));
            btn_playpausePanel.setImageDrawable(getResources().getDrawable(R.drawable.selector_pause));
        }
        imageView_backward.setEnabled(!isBuffer);
        imageView_forward.setEnabled(!isBuffer);
        imageView_download.setEnabled(!isBuffer);
        btn_playpausePanel.setEnabled(!isBuffer);
        seekBar.setEnabled(!isBuffer);
    }

    public void setRepeat() {
        if (Constant.isRepeat) {
            Constant.isRepeat = false;
            imageView_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.repeat));
        } else {
            Constant.isRepeat = true;
            imageView_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.repeat_hover));
        }
    }

    public void setShuffle() {
        if (Constant.isSuffle) {
            Constant.isSuffle = false;
            imageView_shuffle.setImageDrawable(getResources().getDrawable(R.mipmap.shuffle));
        } else {
            Constant.isSuffle = true;
            imageView_shuffle.setImageDrawable(getResources().getDrawable(R.mipmap.shuffle_hover));
        }
    }

    public void next() {
//        isBuffering(true);
//        if(Constant.isSuffle) {
//            Random rand = new Random();
//            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
//        } else {
//            if (Constant.playPos < (Constant.arrayList_play.size() - 1)) {
//                Constant.playPos = Constant.playPos + 1;
//            } else {
//                Constant.playPos = 0;
//            }
//        }
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_SKIP);
        startService(intent);
    }

    public void previous() {
//        isBuffering(true);
//        if(Constant.isSuffle) {
//            Random rand = new Random();
//            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
//        } else {
//            if (Constant.playPos > 0) {
//                Constant.playPos = Constant.playPos - 1;
//            } else {
//                Constant.playPos = Constant.arrayList_play.size() - 1;
//            }
//        }
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_REWIND);
        startService(intent);
    }

    public void playPause() {
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        if (Constant.isPlayed) {
            if (Constant.isPlaying) {
                intent.setAction(PlayerService.ACTION_PAUSE);
            } else {
                intent.setAction(PlayerService.ACTION_PLAY);
            }
            //        changePlayPauseIcon(Constant.isPlaying);
        } else {
            intent.setAction(PlayerService.ACTION_FIRST_PLAY);
        }
        startService(intent);
    }

    public void changePlayPauseIcon(Boolean isPlay) {
        if (!isPlay) {
            imageView_playpause.setImageDrawable(getResources().getDrawable(R.drawable.selector_play));
            btn_playpausePanel.setImageDrawable(getResources().getDrawable(R.drawable.selector_play));
        } else {
            imageView_playpause.setImageDrawable(getResources().getDrawable(R.drawable.selector_pause));
            btn_playpausePanel.setImageDrawable(getResources().getDrawable(R.drawable.selector_pause));
        }
    }

    public void fav() {
        if (Constant.isFav) {
            dbHelper.removeFromFav(Constant.arrayList_play.get(Constant.playPos).getId());
            Toast.makeText(MainActivity.this, "Removed to Fav", Toast.LENGTH_SHORT).show();
            Constant.isFav = false;
            changeFav();
        } else {
            dbHelper.addToFav(Constant.arrayList_play.get(Constant.playPos));
            Toast.makeText(MainActivity.this, "Added to Fav", Toast.LENGTH_SHORT).show();
            Constant.isFav = true;
            changeFav();
        }
    }

    public void checkFav() {
        Constant.isFav = dbHelper.checkFav(Constant.arrayList_play.get(Constant.playPos).getId());

        changeFav();
    }

    public void changeFav() {
        if (Constant.isFav) {
            imageView_Favorite.setImageDrawable(getResources().getDrawable(R.drawable.fav_hover));
        } else {
            imageView_Favorite.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        }
    }

    public void notifyViewPager() {
        viewpager.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {
            // TODO Auto-generated constructor stub

            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList_play.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.viewpager_item, container, false);
            assert imageLayout != null;
            RoundedImageView imageView = (RoundedImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            Picasso.with(MainActivity.this)
                    .load(Constant.arrayList_play.get(position).getImageBig())
                    .placeholder(R.mipmap.app_icon)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private void shareSong() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Lecture");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Lecture - " + Constant.arrayList_play.get(Constant.playPos).getMp3Name() + "\n\n" + "Category - " + Constant.arrayList_play.get(Constant.playPos).getArtist() + "\n\n" + Constant.arrayList_play.get(Constant.playPos).getShareurl());
        startActivity(Intent.createChooser(sharingIntent, "Share Lecture"));
        Log.e("share", "" + Constant.arrayList_play.get(Constant.playPos).getMp3Name() + "\n\n" + "Category - " + Constant.arrayList_play.get(Constant.playPos).getArtist() + "\n\n" + Constant.arrayList_play.get(Constant.playPos).getShareurl());

    }

    private void download() {
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.download_desti));

        if (!root.exists()) {
            root.mkdirs();
        }

        File file = new File(root, Constant.arrayList_play.get(Constant.playPos).getMp3Name() + ".mp3");

        if (!file.exists()) {
            String url = Constant.arrayList_play.get(Constant.playPos).getMp3Url();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDescription("Downloading - " + Constant.arrayList_play.get(Constant.playPos).getMp3Name());
            request.setTitle(Constant.arrayList_play.get(Constant.playPos).getMp3Name());
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(getString(R.string.download_desti), Constant.arrayList_play.get(Constant.playPos).getMp3Name() + ".mp3");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } else {
            Toast.makeText(MainActivity.this, "File already downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeVolume() {

        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_dailog_volume);
        dialog.setTitle("Volume");

        SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar_volume);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volume_level);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Constant.volume = i;
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void loadInter() {
        mInterstitial = new InterstitialAd(MainActivity.this);
        mInterstitial.setAdUnitId(getResources().getString(R.string.admob_intertestial_id));
        mInterstitial.loadAd(new AdRequest.Builder().build());
    }

    private class LoadSong extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_ID);
                    String cid = objJson.getString(Constant.TAG_CAT_ID);
                    String cname = objJson.getString(Constant.TAG_CAT_NAME);
                    String artist = objJson.getString(Constant.TAG_ARTIST);
                    String name = objJson.getString(Constant.TAG_SONG_NAME);
                    String url = objJson.getString(Constant.TAG_MP3_URL);
                    String desc = objJson.getString(Constant.TAG_SHARE_LINK);
                    String duration = objJson.getString(Constant.TAG_DURATION);
                    String thumb = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    String thumb_small = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    mp3url = objJson.getString(Constant.TAG_SHARE_LINK);
                    Log.e("linkshare", "" + Constant.TAG_SHARE_LINKK);

                    ItemSong objItem = new ItemSong(id, cid, cname, artist, url, thumb, thumb_small, name, duration, desc, mp3url);
                    Constant.arrayList_play.add(objItem);
                }

                return "1";
            } catch (JSONException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception ee) {
                ee.printStackTrace();
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                Intent intent = new Intent(MainActivity.this, PlayerService.class);
//                if(Constant.isPlayed) {
//                    if (Constant.isPlaying) {
//                        intent.setAction(PlayerService.ACTION_PAUSE);
//                    } else {
//                        intent.setAction(PlayerService.ACTION_PLAY);
//                    }
                //        changePlayPauseIcon(Constant.isPlaying);
//                } else {
                intent.setAction(PlayerService.ACTION_FIRST_PLAY);
//                }
                startService(intent);
            }

            super.onPostExecute(s);
        }
    }

    public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"},
                        1);
            }
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, "Cannot use save feature without requested permission", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (null == result || result.length() == 0) {
                Toast.makeText(MainActivity.this, "No data found from web!!!", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject c = null;
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

                        Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
                        textView.setText("Contact US - " + Constant.itemAbout.getDevelopedby());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
}
