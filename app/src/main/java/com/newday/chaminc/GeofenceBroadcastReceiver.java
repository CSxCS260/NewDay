package com.newday.chaminc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Calendar;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private String title;
    private String message;
    private NotificationHelper mNotificationHelper;
    private int positionPending;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        title = intent.getStringExtra("title");
        message = intent.getStringExtra("message");
        String description = message.substring(message.indexOf("!@#$")+4);
        message = message.substring(0, message.indexOf("!@#$"));
        positionPending = intent.getIntExtra("positionPending",-1);
        ArrayList<String> times = FileHelper.readData(context,15);
        ArrayList<String> dailyReminders = FileHelper.readData(context, 111);
        mNotificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb;
        Intent intentToOpenApp = new Intent(context, MainActivity.class);
        PendingIntent pI = PendingIntent.getActivity(context,1,intentToOpenApp,0);
        if(title!=null) {
            Calendar c = Calendar.getInstance();
            int firstBackslash = description.indexOf("/");
            int secondBackslash = description.substring(firstBackslash + 1).indexOf("/") + firstBackslash + 1;
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
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.set(Calendar.YEAR, year);
            Calendar cIfThisIsRepeating = Calendar.getInstance();
//            cIfThisIsRepeating.set(Calendar.MONTH, Calendar.MONTH);
//            cIfThisIsRepeating.set(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH);
//            cIfThisIsRepeating.set(Calendar.YEAR, Calendar.YEAR);
            boolean dailyReminderBool =  intent.getBooleanExtra("dailyReminder",false);


//            Toast.makeText(context, "Daily reminder bool: "+dailyReminderBool+" and c before cIf: "+cIfThisIsRepeating.before(c)+" and c equals cIf "+c.equals(cIfThisIsRepeating), Toast.LENGTH_SHORT).show();


            if ((dailyReminderBool && (cIfThisIsRepeating.before(c))||c.equals(cIfThisIsRepeating))
                    || (c.equals(cIfThisIsRepeating)) && !dailyReminderBool){nb = mNotificationHelper.getChannel1Notification(title, message);
                nb.setContentIntent(pI);
                if (mNotificationHelper.getManager()!= null) {
                    mNotificationHelper.getManager().notify(positionPending, nb.build());
                }
            }
        }
        else {
            if (mNotificationHelper.getManager() != null) {
                ArrayList<String> positions = FileHelper.readData(context, 3);
                ArrayList<String> titles = FileHelper.readData(context, 11);
                ArrayList<String> descriptions = FileHelper.readData(context, 12);
                ArrayList<String> specificCategory = FileHelper.readData(context, 13);
                ArrayList<String> trueTimes = FileHelper.readData(context, 18);
                ArrayList<String> positionsForPendingIntents = FileHelper.readData(context, 3);
                for (int i = 0; i < positions.size(); i++) {
                    String strtext = titles.get(i);
                    String dateText = descriptions.get(i);
                    String theTrueTime = trueTimes.get(i);
                    String oneCategory = specificCategory.get(i);
                    int positionForPending = Integer.parseInt(positionsForPendingIntents.get(i));
                    boolean sendToNotiRepeating = false;
                    if (dailyReminders.get(i).equalsIgnoreCase("yes")) {
                        sendToNotiRepeating = true;
                    }
                    if(times.get(i).contains("%^&*")) {
                        NotificationSenderLoc notificationSender = new NotificationSenderLoc(context);
                        notificationSender.removeIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                        notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                    }
                }

            }
        }
    }
}