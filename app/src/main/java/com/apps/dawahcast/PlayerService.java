package com.apps.dawahcast;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.apps.utils.Constant;
import com.apps.utils.DBHelper;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Random;

public class PlayerService extends IntentService implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FIRST_PLAY = "com.apps.onlinemp3.action.ACTION_FIRST";
    public static final String ACTION_SEEKTO = "com.apps.onlinemp3.action.ACTION_SEEKTO";
    public static final String ACTION_PLAY = "com.apps.onlinemp3.action.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.apps.onlinemp3.action.PAUSE";
    public static final String ACTION_STOP = "com.apps.onlinemp3.action.STOP";
    public static final String ACTION_SKIP = "com.apps.onlinemp3.action.SKIP";
    public static final String ACTION_REWIND = "com.apps.onlinemp3.action.REWIND";
    public static final String ACTION_NOTI_PLAY = "com.apps.onlinemp3.action.NOTI_PLAY";
    public static final String ACTION_APP_OPEN = "com.apps.onlinemp3.action.APP_OPEN";

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
//    MediaPlayer mediaPlayer;
    TrackSelector trackSelector;
    NotificationCompat.Builder notification;
    RemoteViews bigViews, smallViews;
    DBHelper dbHelper;
    public PlayerService() {
        super(null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DBHelper(Constant.context);
        registerReceiver(onCallIncome, new IntentFilter("android.intent.action.PHONE_STATE"));

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        Constant.exoPlayer = ExoPlayerFactory.newSimpleInstance((MainActivity)Constant.context, trackSelector);
        Constant.exoPlayer.addListener(listener);

//        Intent notificationIntent = new Intent(this, SplashActivity.class);
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
////        notificationIntent.putExtra("from","service");
////        notificationIntent.putExtra("pos",Constants.pos);
////        notificationIntent.putExtra("song",Constants.arrayList_songitem);
//        notificationIntent.setAction(ACTION_FIRST_PLAY);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent playIntent = new Intent(this, PlayerService.class);
//        playIntent.setAction(ACTION_PLAY);
//        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
//                playIntent, 0);
//
//        Intent pauseIntent = new Intent(this, PlayerService.class);
//        pauseIntent.setAction(ACTION_PAUSE);
//        PendingIntent ppauseIntent = PendingIntent.getService(this, 0,
//                pauseIntent, 0);
//
//        Intent stopIntent = new Intent(this, PlayerService.class);
//        stopIntent.setAction(ACTION_STOP);
//        PendingIntent pstopIntent = PendingIntent.getService(this, 0,
//                stopIntent, 0);
//
//
//        notification = new NotificationCompat.Builder(this)
//                .setContentTitle("MP3 Streamer")
//                .setTicker("MP3 Streamer")
//                .setContentText("Surah Name")
//                .setSmallIcon(R.mipmap.app_icon)
//                .setPriority(Notification.PRIORITY_MAX)
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .addAction(android.R.drawable.ic_media_play, "Play",
//                        pplayIntent)
//                .addAction(android.R.drawable.ic_media_pause, "Pause",
//                        ppauseIntent)
//                .addAction(android.R.drawable.ic_delete, "Stop",
//                        pstopIntent);
        createNoti();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        switch (action) {
            case ACTION_FIRST_PLAY:
                handleFirstPlay();
                break;
            case ACTION_SEEKTO:
                seekTo(intent.getExtras().getLong("seekto"));
                break;
            case ACTION_PLAY:
                play();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_STOP:
                stop(intent);
                break;
            case ACTION_REWIND:
                previous();
                break;
            case ACTION_SKIP:
                next();
                break;
            case ACTION_NOTI_PLAY:
                if(Constant.isPlaying) {
                    pause();
                } else {
                    play();
                }
                break;
        }

        return START_STICKY;
    }

    private void handleFirstPlay() {
        Constant.isPlayed = true;
        changePlayPause();
        changeText();
        playAudio();
        showNotification();
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        setBuffer(false);
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(Constant.isRepeat) {
            mediaPlayer.seekTo(0);
        } else {
            if(Constant.isSuffle) {
                Random rand = new Random();
                Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
            } else {
                setNext();
            }
        }

        changeText();
        playAudio();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

        Constant.currentProgress = mediaPlayer.getCurrentPosition();
//        ((MainActivity)Constant.context).setSeekbarSecondaryProgress(i);
//        Constant.secondaryProgress = i;
//        double ratio = i / 100.0;
//        int bufferingLevel = (int)(calculateTime(song_duration) * ratio);
//
//        ((MainActivity)context).setSeekbarSecondaryProgress(bufferingLevel);
    }

    SimpleExoPlayer.EventListener listener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.e("---exo","state changed");
            if(playbackState == SimpleExoPlayer.STATE_ENDED) {
                onCompletion();
            }
            if (playbackState == SimpleExoPlayer.STATE_READY && playWhenReady) {
                Log.e("---exo","play");
                Constant.exoPlayer.setPlayWhenReady(true);
                setBuffer(false);
                updateNoti();
            }

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e("---exo","state error");
        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }
    };

    private void onCompletion() {
        if(Constant.isRepeat) {
            Constant.exoPlayer.seekTo(0);
        } else {
            if(Constant.isSuffle) {
                Random rand = new Random();
                Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
            } else {
                setNext();
            }
        }


        playAudio();
        changeText();
    }

    private void changeText() {
        ((MainActivity)Constant.context).changeText(Constant.arrayList_play.get(Constant.playPos).getMp3Name(),Constant.arrayList_play.get(Constant.playPos).getCategoryName(),Constant.playPos+1,Constant.arrayList_play.size(),Constant.arrayList_play.get(Constant.playPos).getDuration(),Constant.arrayList_play.get(Constant.playPos).getImageBig(),"");
    }

    private void setBuffer(Boolean isBuffer) {
        ((MainActivity)Constant.context).isBuffering(isBuffer);
        if(!isBuffer) {
            ((MainActivity) Constant.context).seekUpdation();
            changeEquilizer();
        }
        Constant.isPlaying = !isBuffer;
    }

    private void playAudio() {
        new LoadSong().execute();
    }

    class LoadSong extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            setBuffer(true);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... a) {
//            mediaPlayer.reset();
            String s = Constant.arrayList_play.get(Constant.playPos).getMp3Url().replace(" ","%20");
            try {
//                mediaPlayer.setDataSource(PlayerService.this, Uri.parse(s));
//                mediaPlayer.prepare();
//                setBuffer(true);
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory((MainActivity)Constant.context,
                        Util.getUserAgent((MainActivity)Constant.context, "onlinemp3"), bandwidthMeter);
// Produces Extractor instances for parsing the media data.
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource(Uri.parse(s),
                        dataSourceFactory, extractorsFactory, null, null);
// Prepare the player with the source.
                Constant.exoPlayer.prepare(videoSource);

                Constant.exoPlayer.setPlayWhenReady(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dbHelper.addToRecent(Constant.arrayList_play.get(Constant.playPos));
            super.onPostExecute(s);
        }
    }

    private void setNext() {
        if (Constant.playPos < (Constant.arrayList_play.size() - 1)) {
            Constant.playPos = Constant.playPos + 1;
        } else {
            Constant.playPos = 0;
        }

        changeEquilizer();
    }

    private void seekTo(long seek) {
//        mediaPlayer.seekTo((int) seek);
        Constant.exoPlayer.seekTo((int) seek);
    }

    private void play() {
//        mediaPlayer.start();
        if(Constant.isPlayed) {
            Constant.isPlaying = true;
            Constant.exoPlayer.setPlayWhenReady(true);
            changePlayPause();
            ((MainActivity) Constant.context).seekUpdation();
        } else {
            changeText();
            handleFirstPlay();
        }
        changeEquilizer();
        updateNotiPlay(Constant.isPlaying);
    }

    private void previous() {
        setBuffer(true);
        if(Constant.isSuffle) {
            Random rand = new Random();
            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
        } else {
            if (Constant.playPos > 0) {
                Constant.playPos = Constant.playPos - 1;
            } else {
                Constant.playPos = Constant.arrayList_play.size() - 1;
            }
        }

        changeEquilizer();

        handleFirstPlay();
    }

    private void next() {
        setBuffer(true);
        if(Constant.isSuffle) {
            Random rand = new Random();
            Constant.playPos = rand.nextInt((Constant.arrayList_play.size() - 1) + 1);
        } else {
            if (Constant.playPos < (Constant.arrayList_play.size() - 1)) {
                Constant.playPos = Constant.playPos + 1;
            } else {
                Constant.playPos = 0;
            }
        }

        changeEquilizer();

        handleFirstPlay();
    }

    private void pause() {
//        mediaPlayer.pause();
        Constant.isPlaying = false;
        changeEquilizer();
        Constant.exoPlayer.setPlayWhenReady(false);
        changePlayPause();
        updateNotiPlay(Constant.isPlaying);
    }

    private void changePlayPause() {
        ((MainActivity)Constant.context).changePlayPauseIcon(Constant.isPlaying);
    }

    private void stop(Intent intent) {
//        notificationCloseRequest();
//        SongStream.button_playpause.setBackgroundResource(R.mipmap.play);
        Constant.isPlaying = false;
        Constant.isPlayed = false;
        ((MainActivity)Constant.context).changePlayPauseIcon(Constant.isPlaying);
        Constant.exoPlayer.stop();
        Constant.exoPlayer.release();
        unregisterReceiver(onCallIncome);
        stopService(intent);
        stopForeground(true);
//        stopSelf();
    }

    private void showNotification() {
        startForeground(101, notification.build());
    }

    private void notificationCloseRequest() {
            stopForeground(true);
    }

    private void createNoti() {
        bigViews = new RemoteViews(getPackageName(),R.layout.layout_notification);
        smallViews = new RemoteViews(getPackageName(),R.layout.layout_noti_small);

        Intent notificationIntent = new Intent(this, SplashActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        notificationIntent.setAction(ACTION_FIRST_PLAY);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra("isnoti",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, SplashActivity.class);
        previousIntent.setAction(ACTION_REWIND);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(ACTION_NOTI_PLAY);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(ACTION_SKIP);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction(ACTION_STOP);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);


        bigViews.setOnClickPendingIntent(R.id.imageView_noti_play, pplayIntent);

        bigViews.setOnClickPendingIntent(R.id.imageView_noti_next, pnextIntent);

        bigViews.setOnClickPendingIntent(R.id.imageView_noti_prev, ppreviousIntent);

        bigViews.setOnClickPendingIntent(R.id.imageView_noti_close, pcloseIntent);
        smallViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        bigViews.setImageViewResource(R.id.imageView_noti_play,android.R.drawable.ic_media_pause);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            bigViews.setTextColor(R.id.textView_noti_artist,getResources().getColor(R.color.black));
//            bigViews.setTextColor(R.id.textView_noti_name,getResources().getColor(R.color.black));
//            smallViews.setTextColor(R.id.status_bar_track_name,getResources().getColor(R.color.white));
//            smallViews.setTextColor(R.id.status_bar_artist_name,getResources().getColor(R.color.black));
//        } else {
//            bigViews.setTextColor(R.id.textView_noti_artist,getResources().getColor(R.color.white));
//            bigViews.setTextColor(R.id.textView_noti_name,getResources().getColor(R.color.white));
//            smallViews.setTextColor(R.id.status_bar_track_name,getResources().getColor(R.color.white));
//            smallViews.setTextColor(R.id.status_bar_artist_name,getResources().getColor(R.color.white));
//        }
        bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
        smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());

        bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist());
        smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist());

        bigViews.setImageViewResource(R.id.imageView_noti, R.mipmap.app_icon);
        smallViews.setImageViewResource(R.id.status_bar_album_art, R.mipmap.app_icon);

        notification = new NotificationCompat.Builder(this)
            .setPriority(Notification.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.notification)
            .setTicker(Constant.arrayList_play.get(Constant.playPos).getMp3Name())
            .setCustomContentView(smallViews)
            .setCustomBigContentView(bigViews)
            .setOngoing(true);
    }

    private void updateNoti() {
        bigViews.setTextViewText(R.id.textView_noti_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
        bigViews.setTextViewText(R.id.textView_noti_artist, Constant.arrayList_play.get(Constant.playPos).getArtist());

        smallViews.setTextViewText(R.id.status_bar_artist_name, Constant.arrayList_play.get(Constant.playPos).getArtist());
        smallViews.setTextViewText(R.id.status_bar_track_name, Constant.arrayList_play.get(Constant.playPos).getMp3Name());
//        bigViews.setImageViewResource(R.id.imageView_noti, R.drawable.ic_action_favorite);
        updateNotiPlay(Constant.isPlaying);
//        startForeground(101, notification.build());
    }

    private void updateNotiPlay(Boolean isPlay) {
        if(isPlay) {
            bigViews.setImageViewResource(R.id.imageView_noti_play,android.R.drawable.ic_media_pause);
        } else {
            bigViews.setImageViewResource(R.id.imageView_noti_play,android.R.drawable.ic_media_play);
        }
        startForeground(101, notification.build());
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    BroadcastReceiver onCallIncome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String a = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if(Constant.isPlaying) {
                if(a.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) || a.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                    Constant.exoPlayer.setPlayWhenReady(false);
                } else if (a.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Constant.exoPlayer.setPlayWhenReady(true);
                }
            }
        }
    };

    private void changeEquilizer() {
        if (Constant.frag.equals("cat")) {
            FragmentSongByCat.adapterSongList.notifyDataSetChanged();
        } else if(Constant.frag.equals("art")){
            FragmentSongByArtist.adapterSongList.notifyDataSetChanged();
        } else if(Constant.frag.equals("fav")){
            FragmentFav.adapterSongList.notifyDataSetChanged();
        }

    }


}
