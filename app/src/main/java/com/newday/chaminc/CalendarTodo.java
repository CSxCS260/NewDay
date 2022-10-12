package com.newday.chaminc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.todolist.mynewday.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarTodo extends AppCompatActivity {
    private FloatingActionButton doneFab;
    private EditText enterItem;
    private TextView dateChooser;
    private String dateChoice;
    private TextView categoryChooser;
    private String categoryChoice;
    private String theColor;
    private TextView timeChooser;
    private EditText notes;
    private String time;
    private String amPmTime;
    private String sendToNotiTitle;
    private String sendToNotiDescription;
    private String sendToNotiTime;
    private boolean sendToNotiRepeating;
    private Switch dailyReminder;
    private TextView dailyChooserText;
    private TextView categoryTitle;
    private ArrayList<String> allCategories = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    final Calendar cal = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_todo);
        Intent theIntent = getIntent();
        allCategories= theIntent.getStringArrayListExtra("category");
        allCategories.add(0,"None");
        colors = theIntent.getStringArrayListExtra("colors");
        colors.add(0,"None");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enterItem = findViewById(R.id.editText);
        enterItem.requestFocus();
        dateChooser = findViewById(R.id.textView);
        dateChoice = theIntent.getStringExtra("date");
        setTitle("New Checklist Item");
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
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        final int theme;
        if(darkTheme){
            theme = android.R.style.Theme_DeviceDefault_Dialog;
        }
        else{
            theme = android.R.style.ThemeOverlay_Material_Dialog;
        }
        dateChooser.setText(dateChoice);
        if (darkTheme) {
            dateChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_round_lock_24_white, 0);
        }
        else {
            dateChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_round_lock_24, 0);
        }
        timeChooser = findViewById(R.id.textView2);
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        int hourInitial = hour;
        String amOrPm;
        if (hour > 12) {
            hourInitial%=12;
            amOrPm = " PM";
        }
        else{
            amOrPm = " AM";
        }
        if(hourInitial==0){
            hourInitial=12;
        }
        amPmTime = hourInitial+":"+minute+amOrPm;
        timeChooser.setText(amPmTime);
        final boolean twentyFourTime = sharedPref.getBoolean("onOrOffTime", false);
        String postTime;
        if (twentyFourTime) {
            String temp2 = time;
            if(time.substring(time.indexOf(":")+1).length()<2){
                temp2 = time.substring(0,time.indexOf(":")+1)+0+time.substring(time.indexOf(":")+1);
            }
            timeChooser.setText(temp2);
        }
        timeChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CalendarTodo.this,theme, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
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
                        time = selectedHour + ":" + selectedMinute;
                        if (selectedHour<12) {
                            String time =  hour + ":" + minute +" AM";
                            amPmTime = time;
                            timeChooser.setText(time);
                        }
                        else{
                            String time = hour + ":" + minute +" PM";
                            amPmTime = time;
                            timeChooser.setText(time);
                        }
                        if (twentyFourTime){
                            String tempTrue = time;
                            if(tempTrue.substring(tempTrue.indexOf(":")+1).length()<2){
                                tempTrue = tempTrue.substring(0,tempTrue.indexOf(":")+1)+0+tempTrue.substring(tempTrue.indexOf(":")+1);
                            }
                            timeChooser.setText(tempTrue);
                        }

                    }
                }, hour, minute, twentyFourTime);
                mTimePicker.show();
                int setHourAs, setMinuteAs;
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
                else{
                    if(tempHour.equals("12")){
                        setHourAs = 12;
                    }
                    else{
                        setHourAs = Integer.parseInt(tempHour)+12;
                    }
                }
                mTimePicker.updateTime(setHourAs,setMinuteAs);
            }
        });
        categoryChooser = findViewById(R.id.textView3);
        categoryChooser.setText("None");
        categoryChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(CalendarTodo.this, v);
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
        dailyReminder = findViewById(R.id.switch1);
        dailyChooserText = findViewById(R.id.textView5);
        dailyChooserText.setText("No");
        categoryTitle = findViewById(R.id.nameTitle2);
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
//            timeChooser.setTextColor(getColor(R.color.white));
//            dailyChooserText.setTextColor(getColor(R.color.white));
//            categoryChooser.setTextColor(getColor(R.color.white));
//            categoryTitle.setTextColor(getColor(R.color.white));
        }
        doneFab = findViewById(R.id.doneFAB);
        final String finalTemp = temp;
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dateChoice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (enterItem.getText().toString().equals("endIt")) {

                } else {
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
                    bundle.putString("trueTime", time);
                    bundle.putString("chosenTime", amPmTime);
                    bundle.putString("chosenDate", finalTemp);
                    bundle.putString("positionForPendingIntent", Integer.toString(getIntent().getIntExtra("positionForPendingIntent", -1)));
                    CalendarFragment frag = new CalendarFragment();
                    frag.setArguments(bundle);
                    FragmentTransaction f = getSupportFragmentManager().beginTransaction();
                    f.add(frag, "lol");
                    f.detach(frag);
                    f.attach(frag);
                    f.commit();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });
    }

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
