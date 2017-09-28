package com.apps.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.apps.dawahcast.R;
import com.apps.dawahcast.SplashActivity;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NotificationExtenderExample extends NotificationExtenderService {
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	String title, message, bigpicture, nid, url;
	
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

	   title = receivedResult.payload.title;
	   message = receivedResult.payload.body;
	   bigpicture = receivedResult.payload.bigPicture;
	   nid = "";
	   if(receivedResult.payload.additionalData != null) {
		   try {
			   nid = receivedResult.payload.additionalData.getString("id");
		   } catch (JSONException e) {
			   e.printStackTrace();
		   }
	   }

	   try {
		   url = receivedResult.payload.launchURL;
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   sendNotification();
	   
      return true;
   }
   
   private void sendNotification() {
	   mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

	   Intent intent;
	   if(url != null) {
		   intent = new Intent(Intent.ACTION_VIEW);
		   intent.setData(Uri.parse(url));
	   } else if(bigpicture != null || !nid.equals("")) {
		   intent = new Intent(this,SplashActivity.class);
		   intent.putExtra("noti_nid",nid);
		   intent.putExtra("ispushnoti",true);
	   } else {
		   intent = new Intent(this,SplashActivity.class);
	   }
	   PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
	   Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setAutoCancel(true)
			.setSound(uri)
	   		.setAutoCancel(true)
	   		.setLights(Color.RED, 800, 800)
//							.setLargeIcon(largeIcon)
			.setContentText(message);

	   mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
            
	   if(title.trim().isEmpty()) {
		   mBuilder.setContentTitle(getString(R.string.app_name));
		   mBuilder.setTicker(getString(R.string.app_name));
	   } else {
		   mBuilder.setContentTitle(title);
		   mBuilder.setTicker(title);
	   }

	   if(bigpicture != null) {
		   mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)));
	   }
     
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   		
	}

	private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			notificationBuilder.setColor(getColour());
			return R.drawable.notification;

		} else {
			return R.mipmap.app_icon;
		}
	}

	private int getColour() {
		return 0xee2c7a;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}

}
