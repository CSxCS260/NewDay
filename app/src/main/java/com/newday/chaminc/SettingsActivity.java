package com.newday.chaminc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.todolist.mynewday.R;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    public static final String CATEGORY_KEY = "choices";
    public static final String DATE_SWITCH_KEY = "onOrOff";
    public static final String THEME_SWITCH_KEY = "onOrOffAppTheme";
    public static final String SIZE_SWITCH_KEY = "onOrOffSize";
    public static final String TIME_SWITCH_KEY = "onOrOffTime";
    public static final String EMAIL_SWITCH_KEY = "email";
    public static final String REVIEW_SWITCH_KEY = "review";
    public static final String DONATION_SWITCH_KEY = "donation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if(darkTheme){
            setTheme(R.style.GreenAppTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchPref = sharedPref.getBoolean(SettingsActivity.DATE_SWITCH_KEY, false);
//        if (switchPref){
//            Calendar c = Calendar.getInstance();
//            c.set(Calendar.HOUR_OF_DAY,2);
//            c.set(Calendar.MINUTE,54);
//            c.set(Calendar.SECOND,0);
//            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//            Intent intent = new Intent(this, WallpaperBootReceiver.class);
//            final PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1, intent,0);
//            if (c.before(Calendar.getInstance())){
//                c.add(Calendar.DATE,1);
//            }
//            int dayInMillis = 1000 * 60 * 60 * 24;
//            assert alarmManager != null;
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),dayInMillis,pendingIntent);
////            String data1 = sharedPref.getString("url", ".... psych didn't work dummy lol");
////            String data2 = sharedPref.getString("searchTerm", ".... psych didn't work dummy lol");
////            Toast.makeText(this, "URL is "+data1, Toast.LENGTH_SHORT).show();
////            Toast.makeText(this, "I received "+data2, Toast.LENGTH_SHORT).show();
//        }
        boolean switchColor = sharedPref.getBoolean(SettingsActivity.THEME_SWITCH_KEY, false);
        if(switchColor){
            View backgroundSettings = findViewById(R.id.backgroundSettings);
            backgroundSettings.setBackgroundColor(getResources().getColor(R.color.black));
            setTheme(R.style.GreenAppTheme);
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        return true;
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}