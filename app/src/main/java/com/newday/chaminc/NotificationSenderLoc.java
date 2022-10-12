package com.newday.chaminc;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationSenderLoc {
//    private String sendToNotiTitle;
//    private String sendToNotiDescription;
//    private String sendToNotiTime;
//    private boolean sendToNotiRepeating;
//    private int positionForPending;
    private Context context;
    public NotificationSenderLoc(Context context){
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
    public void sendIt(String strtext, String description, String oneTime, String oneCategory, boolean repeating, int positionForPending) {
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
        Calendar cIfThisIsRepeating = Calendar.getInstance();
        cIfThisIsRepeating.set(Calendar.MONTH, month);
        cIfThisIsRepeating.set(Calendar.DAY_OF_MONTH, day);
        cIfThisIsRepeating.set(Calendar.YEAR, year);
        cIfThisIsRepeating.set(Calendar.HOUR_OF_DAY, 23);
        cIfThisIsRepeating.set(Calendar.MINUTE, 59);
        cIfThisIsRepeating.set(Calendar.SECOND, 59);


        int whenDoesItEnd = oneTime.indexOf("%^&*");
            int comma = oneTime.substring(whenDoesItEnd + 14).indexOf(",");
            double lat = Double.parseDouble(oneTime.substring(whenDoesItEnd + 14,
                    whenDoesItEnd + 14 + comma));
            double lon = Double.parseDouble(oneTime.substring(whenDoesItEnd + 14 + comma + 1, oneTime.length() - 1));
            GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
            GeofenceHelper geofenceHelper = new GeofenceHelper(context);
            Geofence geofence = geofenceHelper.getGeofence(Integer.toString(positionForPending), new LatLng(lat, lon), 400, Geofence.GEOFENCE_TRANSITION_ENTER);
            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent(strtext, oneCategory, description,repeating,positionForPending);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
    }
    public void removeIt(String strtext, String description, String oneTime, String oneCategory, boolean repeating, int positionForPending){
//        if(singleOrRepeat) {
//            ////////// VERY IMPORTANT/////////////////////////////
//            Intent intent = new Intent(context, BootReceiver.class);
//            ////////// VERY IMPORTANT/////////////////////////////
//            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            assert alarmManager != null;
//            alarmManager.cancel(pendingIntent);
//        }
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        GeofenceHelper geofenceHelper = new GeofenceHelper(context);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent(strtext, oneCategory, description, repeating, positionForPending);
        geofencingClient.removeGeofences(pendingIntent);

    }
}
