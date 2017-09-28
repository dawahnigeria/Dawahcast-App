package com.apps.dawahcast;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class YtPlayActivity extends YouTubeFailureRecoveryActivity {


    private String id;
    YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yt_play);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar));
        }

        Bundle b = getIntent().getExtras();
        id = b.getString("id");
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView
                .initialize(
                        getString(R.string.youtube_api_key),
                        YtPlayActivity.this);


    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        // TODO Auto-generated method stub
        if (!wasRestored) {
            this.player = player;
            player.loadVideo(id);
        }
    }

    @Override
    protected Provider getYouTubePlayerProvider() {
        // TODO Auto-generated method stub
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }
}
