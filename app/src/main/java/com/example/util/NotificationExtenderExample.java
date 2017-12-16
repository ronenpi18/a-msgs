package com.example.util;

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
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.viaviapp.hdwallpaper.R;
import com.viaviapp.hdwallpaper.SplashActivity;


public class NotificationExtenderExample extends NotificationExtenderService {
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	
   @Override
   protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

	   String title = receivedResult.payload.title;
	   String message = receivedResult.payload.body;

	   String url="";
	   try {
		   url = receivedResult.payload.launchURL;
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	   sendNotification(title, message, url);
	   
      return true;
   }
   
   private void sendNotification(String title, String msg, String url) {
	   mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
	   Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
	   Intent intent;
	   if(url==null) {
		   intent = new Intent(this,SplashActivity.class);
	   } else {
		   intent = new Intent(Intent.ACTION_VIEW);
		   intent.setData(Uri.parse(url));
	   }
	   PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
	   Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setAutoCancel(true)
			.setSound(uri)
	   		.setAutoCancel(true)
	   		.setLights(Color.RED, 800, 800)
//							.setLargeIcon(largeIcon)
			.setContentText(msg)
			.setContentText(msg);

	   		mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
            
            if(title.trim().isEmpty()) {
            	mBuilder.setContentTitle(getString(R.string.app_name));
            	mBuilder.setTicker(getString(R.string.app_name));
            } else {
            	mBuilder.setContentTitle(title);
            	mBuilder.setTicker(title);
            }
     
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   		
	}

	private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			notificationBuilder.setColor(getColour());
			return R.drawable.icon_nav;

		} else {
			return R.drawable.app_icon;
		}
	}

	private int getColour() {
		return 0xee2c7a;
	}

}
