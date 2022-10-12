package com.newday.chaminc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class ChangerSender {
//    private String sendToNotiTitle;
//    private String sendToNotiDescription;
//    private String sendToNotiTime;
//    private boolean sendToNotiRepeating;
//    private int positionForPending;
    private Context context;
    public ChangerSender(Context context){
        this.context = context;
//
    }
    public void sendIt(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(  context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 41);
        Calendar rightNow = Calendar.getInstance();
        if (c.before(rightNow)){
            c.add(Calendar.DATE, 1);
        }
        Intent intent  = new Intent(context, BootReceiver.class);
        intent.putExtra("idk", "k");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);


        if (sharedPref.getBoolean("changeWallOnOrOff", false)){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),24*60*60*1000,pendingIntent);
        }
        else{
            alarmManager.cancel(pendingIntent);
        }
    }
    public void removeIt(){
        Intent intent  = new Intent(context, BootReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
