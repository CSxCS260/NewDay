package com.newday.chaminc;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.todolist.mynewday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class EditingTodo extends AppCompatActivity {
    private FloatingActionButton doneFab;
    private EditText enterItem;
    private String title;
    private TextView dateChooser;
    private String dateChoice;
    private TextView categoryChooser;
    private String categoryChoice;
    private String theColor;
    private TextView timeChooser;
    private String time;
    private String sendToNotiTitle;
    private String sendToNotiDescription;
    private String sendToNotiTime;
    private boolean sendToNotiRepeating;
    private Switch dailyReminder;
    private TextView dailyChooserText;
    private TextView categoryTitle;
    private String trueTime;
    private String oneNote;
    private String theSwitch;
    private EditText notes;
    final Calendar cal = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ArrayList<String> allCategories = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    SharedPreferences sharedPref;
    boolean twentyFourOrNah;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if(darkTheme){
            setTheme(R.style.GreenAppTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_creating_todo);
        final Intent theIntent = getIntent();
        title = theIntent.getStringExtra("title");
        if(title.equals("Empty")){
            title = "";
        }
        dateChoice = theIntent.getStringExtra("description");
        categoryChoice= theIntent.getStringExtra("scategory");
        theColor = theIntent.getStringExtra("scolor");
        oneNote = theIntent.getStringExtra("notes");
        theSwitch = theIntent.getStringExtra("dailyReminder");
        allCategories= theIntent.getStringArrayListExtra("category");
        allCategories.add(0,"None");
        colors = theIntent.getStringArrayListExtra("colors");
        colors.add(0, "None");
        time = theIntent.getStringExtra("time");
        trueTime = theIntent.getStringExtra("trueTime");
        categoryTitle = findViewById(R.id.nameTitle2);
        setTitle("Editing \""+title+"\"");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enterItem = findViewById(R.id.editText);
        Selection.setSelection(enterItem.getText(), enterItem.getText().toString().length());
        enterItem.requestFocus();
        enterItem.setText(title);
        dateChooser = findViewById(R.id.textView);
        final boolean euroDate = sharedPref.getBoolean("onOrOff",false);
        String temp = dateChoice;
        if(euroDate){
            int firstSlash = temp.indexOf("/");
            int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
            String month = temp.substring(0,firstSlash);
            String day = temp.substring(firstSlash+1,secondSlash);
            String year = temp.substring(secondSlash+1);
            temp = day+"/"+month+"/"+year;
        }
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        final int theme;
        if(darkTheme){
            theme = android.R.style.Theme_DeviceDefault_Dialog;
        }
        else{
            theme = android.R.style.ThemeOverlay_Material_Dialog;
        }
        dateChooser.setText(temp);
        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int firstSlash = dateChoice.indexOf("/");
                int secondSlash = dateChoice.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                int year = Integer.parseInt(dateChoice.substring(secondSlash+1));
                int month = Integer.parseInt(dateChoice.substring(0,firstSlash))-1;
                int day = Integer.parseInt(dateChoice.substring(firstSlash+1,secondSlash));

                DatePickerDialog dialog = new DatePickerDialog(
                        EditingTodo.this,
                        theme,
                        dateSetListener,
                        year, month, day);
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateChoice = month+1 + "/" + dayOfMonth + "/" + year;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean euroDate = sharedPref.getBoolean("onOrOff",false);
                String temp = dateChoice;
                if(euroDate){
                    int firstSlash = temp.indexOf("/");
                    int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                    String month2 = temp.substring(0,firstSlash);
                    String day = temp.substring(firstSlash+1,secondSlash);
                    String year2 = temp.substring(secondSlash+1);
                    temp = day+"/"+month2+"/"+year2;
                }
                dateChooser.setText(temp);
            }
        };
        timeChooser = findViewById(R.id.textView2);
        Calendar mcurrentTime = Calendar.getInstance();
        int colon = trueTime.indexOf(":");
        final int hour = Integer.parseInt(trueTime.substring(0,colon));
        final int minute = Integer.parseInt(trueTime.substring(colon+1));
        timeChooser.setText(time);
        final boolean twentyFourTime = sharedPref.getBoolean("onOrOffTime", false);
        String postTime;
        twentyFourOrNah = false;
        if (twentyFourTime) {
            twentyFourOrNah = true;
            String tempTrue = trueTime;
            if(tempTrue.substring(tempTrue.indexOf(":")+1).length()<2){
                tempTrue = tempTrue.substring(0,tempTrue.indexOf(":")+1)+0+tempTrue.substring(tempTrue.indexOf(":")+1);
            }
            timeChooser.setText(tempTrue);
        }

        timeChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditingTodo.this,theme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        int hour = selectedHour % 12;
                        if (hour==0){
                            hour=12;
                        }
                        String minute = Integer.toString(selectedMinute);
                        if (minute.length()<2){
                            minute= "0"+minute;
                        }
                        trueTime = selectedHour + ":" + selectedMinute;
                        if (selectedHour<12) {
                            time = hour + ":" + minute +" AM";
                            timeChooser.setText(time);
                        }
                        else{
                            time = hour + ":" + minute +" PM";
                            timeChooser.setText(time);
                        }
                        if (twentyFourTime){
                            String tempTrue = trueTime;
                            if(tempTrue.substring(tempTrue.indexOf(":")+1).length()<2){
                                tempTrue = tempTrue.substring(0,tempTrue.indexOf(":")+1)+0+tempTrue.substring(tempTrue.indexOf(":")+1);
                            }
                            timeChooser.setText(tempTrue);
                        }
                    }
                }, hour, minute, twentyFourOrNah);
                mTimePicker.show();int setHourAs, setMinuteAs;
                String timeChooserString = timeChooser.getText().toString();
                int colon = timeChooserString.indexOf(":");
                String tempHour = timeChooserString.substring(0,colon);
                setMinuteAs = Integer.parseInt(timeChooserString.substring(colon+1, colon+3));
                if (timeChooserString.contains("AM")){
                    if(tempHour.equals("12")){
                        setHourAs = 0;
                    }
                    else{
                        setHourAs = Integer.parseInt(tempHour);
                    }
                }
                else if (timeChooserString.contains("PM")){
                    if(tempHour.equals("12")){
                        setHourAs = 12;
                    }
                    else{
                        setHourAs = Integer.parseInt(tempHour)+12;
                    }
                }
                else{
                    setHourAs = Integer.parseInt(tempHour);
                }
                mTimePicker.updateTime(setHourAs,setMinuteAs);
            }
        });
        if (twentyFourTime) {
            String tempTrue = trueTime;
            if(tempTrue.substring(tempTrue.indexOf(":")+1).length()<2){
                tempTrue = tempTrue.substring(0,tempTrue.indexOf(":")+1)+0+tempTrue.substring(tempTrue.indexOf(":")+1);
            }
            timeChooser.setText(tempTrue);
        }
        categoryChooser = findViewById(R.id.textView3);
//        if (!categoryChooser.getText().toString().equals("None")) {
//            theColor = colors.get(allCategories.indexOf(categoryChoice));  }
//        else {
//            theColor="None";
//        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if (darkTheme){
            View view = findViewById(R.id.backgroundCreating);
            view.setBackgroundColor(this.getColor(R.color.black));
            TextView temp1 = findViewById(R.id.textView);
            TextView temp2 = findViewById(R.id.textView2);
            TextView temp3 = findViewById(R.id.textView3);
            TextView temp4 = findViewById(R.id.textView4);
            TextView temp5 = findViewById(R.id.textView5);
            EditText edit = findViewById(R.id.editText);
            temp1.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            temp2.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            temp3.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            temp4.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            temp5.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            edit.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            temp1.setTextColor(getColor(R.color.white));
            temp2.setTextColor(getColor(R.color.white));
            temp3.setTextColor(getColor(R.color.white));
            temp4.setTextColor(getColor(R.color.white));
            temp5.setTextColor(getColor(R.color.white));
            edit.setTextColor(getColor(R.color.white));
            edit.setHintTextColor(getColor(R.color.colorGrey));
            temp4.setHintTextColor(getColor(R.color.colorGrey));
//            dateChooser.setTextColor(getColor(R.color.white));
//            timeChooser.setTextColor(getColor(R.color.white));
//            dailyChooserText.setTextColor(getColor(R.color.white));
//            categoryChooser.setTextColor(getColor(R.color.white));
//            categoryTitle.setTextColor(getColor(R.color.white));
        }
        if (theColor.equals("Dark Red")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkredcircle,0);
        }
        else if (theColor.equals("Red")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aredcircle,0);
        }
        else if (theColor.equals("Dark Orange")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkorangecircle,0);
        }
        else if (theColor.equals("Orange")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aorangecircle,0);
        }
        else if (theColor.equals("Yellow")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ayellowcircle,0);
        }
        else if (theColor.equals("Dark Green")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkgreencircle,0);
        }
        else if (theColor.equals("Green")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.agreencircle,0);
        }
        else if (theColor.equals("Dark Blue")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkbluecircle,0);
        }
        else if (theColor.equals("Blue")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.abluecircle,0);
        }
        else if (theColor.equals("Dark Purple")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpurplecircle,0);
        }
        else if (theColor.equals("Purple")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apurplecircle,0);
        }
        else if (theColor.equals("Dark Pink")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpinkcircle,0);
        }
        else if (theColor.equals("Pink")){
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apinkcircle,0);
        }
        else{
            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_brightness_1_24,0);
        }
        categoryChooser.setText(categoryChoice);
        categoryChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(EditingTodo.this, v);
                for(int i = 0; i < allCategories.size();i++){
                    menu.getMenu().add(allCategories.get(i));
                }
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        categoryChoice= item.getTitle().toString();
                        categoryChooser.setText(categoryChoice);
                        if (!categoryChooser.getText().toString().equals("None")) {
                            theColor = colors.get(allCategories.indexOf(categoryChoice));  }
                        else {
                            theColor="None";
                        }
                        if (theColor.equals("Dark Red")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkredcircle,0);
                        }
                        else if (theColor.equals("Red")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aredcircle,0);
                        }
                        else if (theColor.equals("Dark Orange")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkorangecircle,0);
                        }
                        else if (theColor.equals("Orange")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aorangecircle,0);
                        }
                        else if (theColor.equals("Yellow")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ayellowcircle,0);
                        }
                        else if (theColor.equals("Dark Green")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkgreencircle,0);
                        }
                        else if (theColor.equals("Green")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.agreencircle,0);
                        }
                        else if (theColor.equals("Dark Blue")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkbluecircle,0);
                        }
                        else if (theColor.equals("Blue")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.abluecircle,0);
                        }
                        else if (theColor.equals("Dark Purple")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpurplecircle,0);
                        }
                        else if (theColor.equals("Purple")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apurplecircle,0);
                        }
                        else if (theColor.equals("Dark Pink")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpinkcircle,0);
                        }
                        else if (theColor.equals("Pink")){
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apinkcircle,0);
                        }
                        else{
                            categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_brightness_1_24,0);
                        }
                        return false;
                    }
                });
            }
        });
        notes = findViewById(R.id.textView4);
        if(oneNote.equalsIgnoreCase("None")){
            notes.setHint(oneNote);
        }
        else {
            notes.setText(oneNote);
        }
        dailyReminder = findViewById(R.id.switch1);
        dailyChooserText = findViewById(R.id.textView5);
        dailyChooserText.setText(theSwitch);
        if(theSwitch.equals("Yes")){
            dailyReminder.setChecked(true);
            categoryTitle.setText("Date To Remind Until");
        }
        else{
            dailyReminder.setChecked(false);
            categoryTitle.setText("Date To Remind At");
        }
        categoryTitle = findViewById(R.id.nameTitle2);
        sendToNotiRepeating = dailyChooserText.getText().toString().equalsIgnoreCase("yes");
        dailyReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dailyReminder.isChecked()) {
                    dailyChooserText.setText("Yes");
                    categoryTitle.setText("Date To Remind Until");
                    sendToNotiRepeating = true;
                } else {
                    dailyChooserText.setText("No");
                    categoryTitle.setText("Date To Remind At");
                    sendToNotiRepeating = false;
                }
            }
        });
        doneFab = findViewById(R.id.doneFAB);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Bundle bundle = new Bundle();
                if (enterItem.getText().toString().equals("")) {
                    bundle.putString("item", "Empty");
                    sendToNotiTitle = "Empty";
                } else {
                    bundle.putString("item", enterItem.getText().toString());
                    sendToNotiTitle = enterItem.getText().toString();
                }
                if (categoryChooser.getText().equals("None")) {
                    bundle.putString("category", "None");
                    bundle.putString("color", "None");
                } else {
                    bundle.putString("category", categoryChoice);
                    bundle.putString("color", theColor);
                }
                if (notes.getText().toString().equals("")) {
                    bundle.putString("notes", "None");
                } else {
                    bundle.putString("notes", notes.getText().toString());
                }
                bundle.putString("dailyReminder", dailyChooserText.getText().toString());
                bundle.putString("trueTime", trueTime);
                bundle.putString("chosenTime", time);
                sendToNotiTime = trueTime;
                bundle.putString("chosenDate", dateChoice);
                sendToNotiDescription = dateChoice;
                bundle.putString("placement", Integer.toString(theIntent.getIntExtra("placement", -1)));
                bundle.putString("positionForPendingIntent", Integer.toString(getIntent().getIntExtra("positionForPendingIntent", -1)));
                sendForNotification(sendToNotiTitle, sendToNotiDescription, sendToNotiTime, sendToNotiRepeating);
                ToDoFragment frag = new ToDoFragment();
                frag.setArguments(bundle);
                FragmentTransaction f = getSupportFragmentManager().beginTransaction();
                f.add(frag, "lol");
                f.detach(frag);
                f.attach(frag);
                f.commit();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void sendForNotification(String sendToNotiTitle, String sendToNotiDescription, String sendToNotiTime, boolean sendToNotiRepeating) {
//        Calendar c = Calendar.getInstance();
//        int firstBackslash = sendToNotiDescription.indexOf("/");
//        int secondBackslash = sendToNotiDescription.substring(firstBackslash + 1).indexOf("/") + firstBackslash + 1;
//        int firstColon = sendToNotiTime.indexOf(":");
//        int month = Integer.parseInt(sendToNotiDescription.substring(0, firstBackslash)) - 1;
//        int day = Integer.parseInt(sendToNotiDescription.substring(firstBackslash + 1, secondBackslash));
//        int year = Integer.parseInt(sendToNotiDescription.substring(secondBackslash + 1));
//        int hour = Integer.parseInt(sendToNotiTime.substring(0, firstColon));
//        int minute = Integer.parseInt(sendToNotiTime.substring(firstColon + 1));
//        int dayInMillis = 1000 * 60 * 60 * 24;
//        c.set(Calendar.HOUR_OF_DAY, hour);
//        c.set(Calendar.MINUTE, minute);
//        c.set(Calendar.SECOND, 0);
//        ////////// VERY IMPORTANT
//        Intent intent = new Intent(getBaseContext(), BootReceiver.class);
//        ////////// VERY IMPORTANT
        int positionForPending = getIntent().getIntExtra("positionForPendingIntent", -1);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
////        if (PendingIntent.getBroadcast(getBaseContext(), POSITION_FOR_PENDING_INTENT, intent, PendingIntent.FLAG_NO_CREATE) == null) {
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), positionForPending, intent, PendingIntent.FLAG_ONE_SHOT);
//        assert alarmManager != null;
//        alarmManager.cancel(pendingIntent);
//        intent.putExtra("title", sendToNotiTitle);
//        intent.putExtra("positionPending", positionForPending);
//        intent.setAction("wewe");
//        Toast.makeText(this, "The PositionForPendingIntent is " + positionForPending, Toast.LENGTH_SHORT).show();
//        if (c.after(Calendar.getInstance())) {
//            if (sendToNotiRepeating) {
//                intent.putExtra("message", "Here's your repeating reminder");
//                pendingIntent = PendingIntent.getBroadcast(getBaseContext(), positionForPending, intent, PendingIntent.FLAG_ONE_SHOT);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayInMillis, pendingIntent);
//            } else {
//                c.set(Calendar.MONTH, month);
//                c.set(Calendar.DAY_OF_MONTH, day);
//                c.set(Calendar.YEAR, year);
//                assert alarmManager != null;
//                intent.putExtra("message", "Here's your one-time reminder");
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                    pendingIntent = PendingIntent.getBroadcast(getBaseContext(), positionForPending, intent, PendingIntent.FLAG_ONE_SHOT);
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//                } else {
//                    pendingIntent = PendingIntent.getBroadcast(getBaseContext(), positionForPending, intent, PendingIntent.FLAG_ONE_SHOT);
//                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//                }
//            }
//        }
//
//        NotificationSender notificationSender = new NotificationSender(getBaseContext());
//        notificationSender.removeIt(positionForPending, sendToNotiRepeating,sendToNotiDescription,sendToNotiTime);
//        notificationSender.sendIt(sendToNotiTitle,sendToNotiDescription,sendToNotiTime,sendToNotiRepeating,positionForPending);
    }
//        else{
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), positionForPendingInt, intent, PendingIntent.FLAG_ONE_SHOT);
//            alarmManager.cancel(pendingIntent);
//        }
//        boolean isItAlready = PendingIntent.getBroadcast(getBaseContext(), POSITION_FOR_PENDING_INTENT, intent, PendingIntent.FLAG_NO_CREATE) == null;
//        Toast.makeText(this, "The isItAlready is "+isItAlready, Toast.LENGTH_SHORT).show();
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}