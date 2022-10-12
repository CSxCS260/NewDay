package com.newday.chaminc;

import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    private String title;
    private String message;
    private String wallpaper;
    private NotificationHelper mNotificationHelper;
    private int positionPending;
    @Override
    public void onReceive(final Context context, final Intent intent) {
//        Intent intent = new Intent();
//        intent.getAction();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        title = prefs.getString("title","Empty");
//        message = prefs.getString("message","Empty");
        title = intent.getStringExtra("title");
        message = intent.getStringExtra("message");
        wallpaper = intent.getStringExtra("idk");
        positionPending = intent.getIntExtra("positionPending",-1);
        mNotificationHelper = new NotificationHelper(context);
        final NotificationCompat.Builder[] nb = new NotificationCompat.Builder[1];
        Intent intentToOpenApp = new Intent(context, MainActivity.class);
        final PendingIntent pI = PendingIntent.getActivity(context,1,intentToOpenApp,0);
        //After NotificationSender hits the actual time, this runs
        if(title!=null) {
            nb[0] = mNotificationHelper.getChannel1Notification(title, message);
            nb[0].setContentIntent(pI);
            if (mNotificationHelper.getManager()!= null) {
                mNotificationHelper.getManager().notify(positionPending, nb[0].build());
            }
        }
        else if(wallpaper!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        nb[0] = mNotificationHelper.getChannel1Notification("Wallpaper change", "Your wallpaper is being changed...");
                        nb[0].setContentIntent(pI);
                        if (mNotificationHelper.getManager()!= null) {
                            mNotificationHelper.getManager().notify(positionPending, nb[0].build());
                        }
                        URL url = new URL("https://source.unsplash.com/3000x4000/?PhoneWallpapers");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Toast.makeText(context, input.toString(), Toast.LENGTH_SHORT).show();
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        Toast.makeText(context, wallpaperManager.toString(), Toast.LENGTH_SHORT).show();
                        Bitmap image = BitmapFactory.decodeStream(input);
                        Toast.makeText(context, image.toString(), Toast.LENGTH_SHORT).show();
                        wallpaperManager.setBitmap(image);
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("string_id", connection.getURL().toString());
//                    editor.commit();
                        nb[0] = mNotificationHelper.getChannel1Notification("Wallpaper change", "Your wallpaper has been changed. Enjoy!");
                        nb[0].setContentIntent(pI);
                        if (mNotificationHelper.getManager()!= null) {
                            mNotificationHelper.getManager().notify(positionPending, nb[0].build());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //After the phone restarts, this runs
        else{
            if (mNotificationHelper.getManager()!= null) {
                ArrayList<String> positions = FileHelper.readData(context,3);
                ArrayList<String> titles = FileHelper.readData(context,11);
                ArrayList<String> descriptions = FileHelper.readData(context,12);
                ArrayList<String> specificCategory = FileHelper.readData(context,13);
                ArrayList<String> times = FileHelper.readData(context,15);
                ArrayList<String> trueTimes = FileHelper.readData(context,18);
                ArrayList<String> dailyReminders = FileHelper.readData(context,111);
                ArrayList<String> positionsForPendingIntents = FileHelper.readData(context,3);
                for(int i = 0; i < positions.size();i++) {
                    String strtext = titles.get(i);
                    String dateText = descriptions.get(i);
                    String theTrueTime = trueTimes.get(i);
                    String oneCategory = specificCategory.get(i);
                    int positionForPending = Integer.parseInt(positionsForPendingIntents.get(i));
                    boolean sendToNotiRepeating = false;
                    if(dailyReminders.get(i).equalsIgnoreCase("yes")){
                        sendToNotiRepeating = true;
                    }
                    if(!times.get(i).contains("%^&*")) {
                        NotificationSender notificationSender = new NotificationSender(context);
                        notificationSender.removeIt(positionForPending, sendToNotiRepeating, dateText, theTrueTime);
                        notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                    }
                }



//                nb = mNotificationHelper.getChannel1Notification("Uh oh, some sort of error occurred", "Come check out your notification in the app");
                nb[0] = mNotificationHelper.getChannel1Notification("Uh oh, did you restart your phone?", "You may have missed a reminder set for that time");
                nb[0].setContentIntent(pI);
                mNotificationHelper.getManager().notify(positionPending, nb[0].build());
            }
        }

    }
}

//    private ArrayList<String> titles = new ArrayList<String>();
//    private ArrayList<String> descriptions = new ArrayList<String>();
//    private ArrayList<String> times = new ArrayList<String>();
//    private ArrayList<String> trueTimes = new ArrayList<String>();
//    private ArrayList<String> categoryNames = new ArrayList<String>();
//    private ArrayList<String> specificCategory = new ArrayList<String>();
//    private ArrayList<String> colors = new ArrayList<String>();
//    private ArrayList<String> specificColor = new ArrayList<String>();
//    private ArrayList<String> notes = new ArrayList<String>();
//    private ArrayList<String> dailyReminders = new ArrayList<String>();

//        titles = FileHelper.readData(context,11);
//        descriptions = FileHelper.readData(context,12);
//        specificCategory = FileHelper.readData(context,13);
//        specificColor = FileHelper.readData(context,14);
//        times = FileHelper.readData(context,15);
//        categoryNames = FileHelper.readData(context,16);
//        colors = FileHelper.readData(context,17);
//        trueTimes = FileHelper.readData(context,18);
//        notes = FileHelper.readData(context,19);
//        dailyReminders = FileHelper.readData(context,111);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int i = 0; i < times.size();i++){
//                    String sendToNotiDescription = descriptions.get(i);
//                    String sendToNotiTime = trueTimes.get(i);
//                    int firstBackslash = sendToNotiDescription.indexOf("/");
//                    int secondBackslash = sendToNotiDescription.substring(firstBackslash+1).indexOf("/")+firstBackslash+1;
//                    int firstColon = sendToNotiTime.indexOf(":");
//                    int month = Integer.parseInt(sendToNotiDescription.substring(0,firstBackslash))-1;
//                    int day = Integer.parseInt(sendToNotiDescription.substring(firstBackslash+1,secondBackslash));
//                    int year = Integer.parseInt(sendToNotiDescription.substring(secondBackslash+1));
//                    int hour = Integer.parseInt(sendToNotiTime.substring(0,firstColon));
//                    int minute = Integer.parseInt(sendToNotiTime.substring(firstColon+1));
//                    int dayInMillis = 1000*60*60*24;
//                    Calendar c = Calendar.getInstance();
//                    c.set(Calendar.MONTH,month);
//                    c.set(Calendar.DAY_OF_MONTH,day);
//                    c.set(Calendar.YEAR,year);
//                    c.set(Calendar.HOUR_OF_DAY,hour);
//                    c.set(Calendar.MINUTE,minute);
//                    c.set(Calendar.SECOND,0);
////                    c.set(Calendar.MILLISECOND,500);
//                    int actualMonth = Calendar.getInstance().get(Calendar.MONTH);
//                    int actualDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//                    int actualYear = Calendar.getInstance().get(Calendar.YEAR);
//                    int actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//                    int actualMinute = Calendar.getInstance().get(Calendar.MINUTE);
//                    boolean atSameDay = month==actualMonth&&day==actualDay&&year==actualYear&&hour==actualHour&& minute==actualMinute;
//                    boolean beforeDay = (month>actualMonth&&year==actualYear&&hour==actualHour&&minute==actualMinute)||(day>actualDay&&month==actualMonth&&year==actualYear&&hour==actualHour&&minute==actualMinute)||(year>actualYear&&hour==actualHour&&minute==actualMinute);
//                    positionPending =
////                            (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)+
//                                    (int)(Math.random()*(Integer.MAX_VALUE-1));
//                    if (dailyReminders.get(i).equalsIgnoreCase("no")&&atSameDay){
//                        mNotificationHelper = new NotificationHelper(context);
//                        NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(titles.get(i), "Make sure to finish this up. You got this 😃");
//                        mNotificationHelper.getManager().notify(positionPending, nb.build());
////                        Toast.makeText(context, "The date I got (not repeating) was "+c.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                    else if (dailyReminders.get(i).equalsIgnoreCase("yes")){
//                        if (atSameDay) {
//                            mNotificationHelper = new NotificationHelper(context);
//                            NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(titles.get(i), "Last day to finish this up. 😄");
//                            mNotificationHelper.getManager().notify(positionPending, nb.build());
////                            Toast.makeText(context, "The date I got (repeating before day) was "+c.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                        else if (beforeDay) {
//                            mNotificationHelper = new NotificationHelper(context);
//                            NotificationCompat.Builder nb = mNotificationHelper.getChannel1Notification(titles.get(i), "Keep up your progress until "+descriptions.get(i)+" 😀");
//                            if (mNotificationHelper.getManager()!= null) {
//                                mNotificationHelper.getManager().notify(positionPending, nb.build());
//                            }
////                            Toast.makeText(context, "The date I got (repeating on day) was "+c.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        }).start();