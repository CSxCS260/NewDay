package com.newday.chaminc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;


    public GeofenceHelper(Context base) {
        super(base);
    }
    public GeofencingRequest getGeofencingRequest (Geofence geofence){
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }
    public Geofence getGeofence (String ID, LatLng latLng, float radius, int transitionTypes){
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(0)
                .setNotificationResponsiveness(100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }
    public PendingIntent getPendingIntent(String title, String category, String date, boolean dailyReminder, int positionPending) {
        if (pendingIntent != null){
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.putExtra("title", title);
//        intent.putExtra("category", category);
        if (dailyReminder){
            intent.putExtra("message", category+" - Reminding Every Day !@#$"+date);
        }
        else{
            intent.putExtra("message", category+" - Reminding Just Today !@#$"+date);
        }
        intent.putExtra("dailyReminder", dailyReminder);
        intent.putExtra("positionPending",positionPending);

        pendingIntent = PendingIntent.getBroadcast(this, positionPending, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
