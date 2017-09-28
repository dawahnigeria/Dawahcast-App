package com.apps.utils;

import android.content.Context;

import com.apps.item.ItemAbout;
import com.apps.item.ItemSong;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static String SERVER_URL="http://dawahnigeria.com/new_app/";

	//		public static final String SERVER_IMAGE_UPFOLDER1=SERVER_URL + "/images/";
//		public static final String SERVER_IMAGE_UPFOLDER=SERVER_URL + "/images/thumbs/";
	public static final String URL_LATEST = SERVER_URL + "api.php?latest";
	public static final String URL_ARTIST = SERVER_URL + "api.php?artist_list";
	public static final String URL_CAT = SERVER_URL + "api.php?cat_list";
	public static final String URL_SONG_BY_CAT = SERVER_URL + "api.php?cat_id=";
	public static final String URL_SONG_BY_ARTIST = SERVER_URL + "api.php?artist_name=";
	public static final String URL_SONG = SERVER_URL + "api.php?mp3_id=";
	public static final String URL_VIDEO_CAT = SERVER_URL + "api.php?video_cat_list";
	public static final String URL_VIDEO_CATLIST = SERVER_URL + "api.php?video_cat_id=";
	//this url gives download list
	public static final String DOWNLOAD_URL = SERVER_URL + "/api.php?all";


	public static final String APP_DETAILS_URL = SERVER_URL +  "api.php";
	public static final String URL_ABOUT_US_LOGO = SERVER_URL + "images/";

	public static final String URL_TWITTER = SERVER_URL + "user_register_twitter_api.php?name=";
	public static final String URL_FACEBOOK = SERVER_URL + "user_register_fb_api.php?name=";
	public static final String URL_GMAIL = SERVER_URL + "user_register_gplus_api.php?name=";

	public static final String TAG_ROOT="ONLINE_MP3";

	public static final String TAG_ID="id";
	public static final String TAG_CAT_ID="cat_id";
	public static final String TAG_CAT_NAME="category_name";
	public static final String TAG_MP3_URL="mp3_url";
	public static final String TAG_DURATION="mp3_duration";
	public static final String TAG_SONG_NAME="mp3_title";
	public static final String TAG_DESC="mp3_description";
	public static final String TAG_THUMB_B="mp3_thumbnail_b";
	public static final String TAG_THUMB_S="mp3_thumbnail_s";
	public static final String TAG_ARTIST="mp3_artist";
	public static final String TAG_SHARE_LINK="mp3_share_url";

	public static  String TAG_URLL;
	public static  String TAG_SHARE_LINKK;

	public static final String TAG_CID="cid";

	public static final String TAG_ARTIST_NAME="artist_name";
	public static final String TAG_ARTIST_IMAGE="artist_image";
	public static final String TAG_ARTIST_THUMB="artist_image_thumb";

	public static final String TAG_VIDEO_CAT_ID="cid";
	public static final String TAG_VIDEO_CAT_NAME="category_name";
	public static  String TAG_VIDEO_CAT_IDD;
	public static  String TAG_VIDEO_CAT_NAMEE;

	public static final String TAG_VIDEO_CATLIST_ID="id";
	public static final String TAG_VIDEO_CATLIST_NAME="video_title";
	public static final String TAG_VIDEO_CATLIST_IMAGE="video_image";
	public static final String TAG_VIDEO_CATLIST_VID="video_url";
	public static final String TAG_VIDEO_CATLIST_NUM="total_rec";


	public static int GET_SUCCESS_MSG;
	public static final String MSG="msg";
	public static final String SUCCESS="success";
	public static final String USER_NAME="name";
	public static final String USER_ID="user_id";
	public static final String USER_EMAIL="email";

	//for title display in CategoryItemF
	public static String CATEGORY_TITLE;
	public static int CATEGORY_ID;
	public static String MUSIC_ID;
	public static String MUSIC_NAME;
	public static String MUSIC_PLAY_ID;
	public static String MUSIC_PLAY_ID_RUNNING;
	public static String MUSIC_PLAY_URL;
	public static int FAV_POSITION;


	public static final String CATEGORY_ITEM_ID="id";
	public static final String CATEGORY_ITEM_CATID="cat_id";
	public static final String CATEGORY_ITEM_CAT_NAME="category_name";
	public static final String CATEGORY_ITEM_MP3_URL="mp3_url";
	public static final String CATEGORY_ITEM_MP3_DURATION="mp3_duration";
	public static final String CATEGORY_ITEM_MP3_NAME="mp3_title";
	public static final String CATEGORY_ITEM_MP3_DESCRIPTION="mp3_description";
	public static final String CATEGORY_ITEM_MP3_IMAGE_THUMB="mp3_thumbnail";
	public static final String CATEGORY_ITEM_MP3_SHARE_URL="share_url";


	public static final String DOWNLOAD_ARRAY="Online Mp3";
	public static final String DOWNLOAD_CAT_NAME="category_name";
	public static final String DOWNLOAD_TITLE="mp3_title";
	public static final String DOWNLOAD_TITLE_URL="mp3_url";


	public static final String DOWNLOAD_SDCARD_FOLDER_PATH_SONGS="/MP3SONGS/";

	public static ItemAbout itemAbout;

	public static SimpleExoPlayer exoPlayer;

	// Number of columns of Grid View
	public static final int NUM_OF_COLUMNS = 3;

	// Gridview image padding
	public static final int GRID_PADDING = 3; // in dp

	public static int columnWidth = 0;

	//	public static PlayerService playerService;
	public static int playPos = 0;
	public static ArrayList<ItemSong> arrayList_play = new ArrayList<>();
	public static Boolean isRepeat = false, isSuffle = false, isPlaying = false, isFav = false, isScrolled = false, isAppFirst = true,
			isPlayed = false, isFromNoti = false, isFromPush = false, isBackStack = false, isAppOpen = false;
	public static long currentProgress = 0;
	public static long secondaryProgress = 0;
	public static Context context;
	public static int volume = 25;
	public static String frag = "", pushID = "";
	public static String timecons;
	public static String loadedSongPage = "";
	public static int adCount = 0;
	public static int adDisplay = 1;

}
