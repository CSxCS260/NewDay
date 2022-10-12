package com.newday.chaminc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.todolist.mynewday.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationSender {
//    private String sendToNotiTitle;
//    private String sendToNotiDescription;
//    private String sendToNotiTime;
//    private boolean sendToNotiRepeating;
//    private int positionForPending;
    private Context context;
    public NotificationSender(Context context){
//        this.sendToNotiTitle = title;
//        this.sendToNotiDescription = description;
//        this.sendToNotiTime = time;
//        this.sendToNotiRepeating = repeating;
//        this.positionForPending = position;
        this.context = context;
//        if(sendOrRemove){
//            if(createOrEdit) {
//                sendIt();
//            }
//            else{
//                removeIt(positionForPending);
//                sendIt();
//            }
//        }
//        else{
//            removeIt();
//        }
    }
    public void sendIt(String title, String description, String time, String category, boolean repeating, int position){
        Calendar c = Calendar.getInstance();
        int firstBackslash = description.indexOf("/");
        int secondBackslash = description.substring(firstBackslash + 1).indexOf("/") + firstBackslash + 1;
        int firstColon = time.indexOf(":");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean euroDate = sharedPref.getBoolean("onOrOff",false);
        int month;
        int day;
        int year;
        if(euroDate){
            month = Integer.parseInt(description.substring(0, firstBackslash)) - 1;
            day = Integer.parseInt(description.substring(firstBackslash + 1, secondBackslash));
            year = Integer.parseInt(description.substring(secondBackslash + 1));
        }
        else{
            month = Integer.parseInt(description.substring(0, firstBackslash)) - 1;
            day = Integer.parseInt(description.substring(firstBackslash + 1, secondBackslash));
            year = Integer.parseInt(description.substring(secondBackslash + 1));
        }
        int hour = Integer.parseInt(time.substring(0, firstColon));
        int minute = Integer.parseInt(time.substring(firstColon + 1));
        int dayInMillis = 1000 * 60 * 60 * 24;
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND,0);
        Calendar cIfThisIsRepeating = Calendar.getInstance();
        cIfThisIsRepeating.set(Calendar.MONTH, month);
        cIfThisIsRepeating.set(Calendar.DAY_OF_MONTH, day);
        cIfThisIsRepeating.set(Calendar.YEAR, year);
        cIfThisIsRepeating.set(Calendar.HOUR_OF_DAY, hour);
        cIfThisIsRepeating.set(Calendar.MINUTE, minute);
        cIfThisIsRepeating.set(Calendar.SECOND, 0);
        ////////// VERY IMPORTANT/////////////////////////////
        Intent intent = new Intent(context, BootReceiver.class);
        ////////// VERY IMPORTANT/////////////////////////////
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        if (PendingIntent.getBroadcast(getBaseContext(), POSITION_FOR_PENDING_INTENT, intent, PendingIntent.FLAG_NO_CREATE) == null) {
        PendingIntent pendingIntent;
        intent.putExtra("title", title);
        intent.putExtra("positionPending",position);
//        intent.setAction("wewe");
        if(category.equalsIgnoreCase("none")){
            category = "No category";
        }
        if (repeating) {
            if (c.before(Calendar.getInstance())&&!cIfThisIsRepeating.before(Calendar.getInstance())){
                c.add(Calendar.DATE,1);
            }
            if (!cIfThisIsRepeating.before(Calendar.getInstance())) {
                assert alarmManager != null;
                intent.putExtra("message",category+" - Reminding Everyday");
                pendingIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayInMillis, pendingIntent);
            }
        }
        else {
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.YEAR, year);
            if (!c.before(Calendar.getInstance())) {
                assert alarmManager != null;
                intent.putExtra("message",category+" - Reminding Once");
                pendingIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            }
        }
    }
    public void removeIt(int position, boolean singleOrRepeat, String description, String time){
        if(singleOrRepeat) {
            ////////// VERY IMPORTANT/////////////////////////////
            Intent intent = new Intent(context, BootReceiver.class);
            ////////// VERY IMPORTANT/////////////////////////////
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.cancel(pendingIntent);
        }

    }
}
