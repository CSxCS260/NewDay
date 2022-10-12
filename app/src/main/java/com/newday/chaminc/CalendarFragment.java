package com.newday.chaminc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment implements ToDoMyRecyclerViewAdapter.ItemClickListener {
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> times = new ArrayList<String>();
    private ArrayList<String> trueTimes = new ArrayList<String>();
    private ArrayList<String> categoryNames = new ArrayList<String>();
    private ArrayList<String> specificCategory = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    private ArrayList<String> specificColor = new ArrayList<String>();
    private ArrayList<String> notes = new ArrayList<String>();
    private ArrayList<String> dailyReminders = new ArrayList<String>();
    private ArrayList<String> newTitles = new ArrayList<String>();
    private ArrayList<String> newDescriptions = new ArrayList<String>();
    private ArrayList<String> newTimes = new ArrayList<String>();
    private ArrayList<String> newTrueTimes = new ArrayList<String>();
    private ArrayList<String> newSpecificCategory = new ArrayList<String>();
    private ArrayList<String> newSpecificColor = new ArrayList<String>();
    private ArrayList<String> newNotes = new ArrayList<String>();
    private ArrayList<String> newDailyReminders = new ArrayList<String>();
    private ArrayList<String> positionsForPendingIntents = new ArrayList<String>();
    private ArrayList<Integer> originalPosition = new ArrayList<Integer>();
    private Context context;
    private String strtext = "";
    private String dateText = "";
    private String categoryText = "";
    private String colorText = "";
    private String oneCategory = "";
    private String oneColor = "";
    private String oneTime = "";
    private String theTrueTime = "";
    private String oneNote = "";
    private String oneDailyReminder = "";
    String temp;
    private Snackbar snackbar;
    private TextView sectionHeader;
    private RecyclerView itemList;
    private CalendarView calendarView;
    ToDoMyRecyclerViewAdapter adapter;
    LinearLayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_calendar,container, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        itemList = view.findViewById(R.id.dailyRecyclerView);
        titles = FileHelper.readData(getContext(),11);
        descriptions = FileHelper.readData(getContext(),12);
        specificCategory = FileHelper.readData(getContext(),13);
        specificColor = FileHelper.readData(getContext(),14);
        times = FileHelper.readData(getContext(),15);
        categoryNames = FileHelper.readData(getContext(),16);
        colors = FileHelper.readData(getContext(),17);
        trueTimes = FileHelper.readData(getContext(),18);
        notes = FileHelper.readData(getContext(),19);
        dailyReminders = FileHelper.readData(getContext(),111);
        positionsForPendingIntents = FileHelper.readData(getContext(),3);
        calendarView = view.findViewById(R.id.calendarViewLight);
        sectionHeader = view.findViewById(R.id.sectionHeaderCalendar);
        mLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(mLayoutManager);
        if (darkTheme){
            view.setBackgroundColor(getContext().getResources().getColor(R.color.black));
            TextView thatThing = view.findViewById(R.id.sectionHeaderCalendar);
            RecyclerView recyclerView = view.findViewById(R.id.dailyRecyclerView);
            recyclerView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.black)));
            thatThing.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.darkBlue)));
        }
        Calendar cal = Calendar.getInstance();
        String theDate = (cal.get(java.util.Calendar.MONTH)+1)+"/"+cal.get(java.util.Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR);
        boolean euroDate = sharedPref.getBoolean("onOrOff",false);
        temp = theDate;
        if(euroDate){
            int firstSlash = temp.indexOf("/");
            int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
            String month = temp.substring(0,firstSlash);
            String day = temp.substring(firstSlash+1,secondSlash);
            String year = temp.substring(secondSlash+1);
            temp = day+"/"+month+"/"+year;
        }
        sectionHeader.setText(temp);
        for (int i = 0; i < titles.size();i++){
            try {
                Date theBigDate = new SimpleDateFormat("MM/dd/yyyy").parse(theDate);
                Date theNewDate = new SimpleDateFormat("MM/dd/yyyy").parse(descriptions.get(i));

            if (descriptions.get(i).equals(theDate)||((dailyReminders.get(i).equalsIgnoreCase("yes"))&&(theNewDate.after(theBigDate)||theNewDate.equals(theBigDate)))){
                newTitles.add(titles.get(i));
                newDescriptions.add(descriptions.get(i));
                newTimes.add(times.get(i));
                newSpecificCategory.add(specificCategory.get(i));
                newSpecificColor.add(specificColor.get(i));
                newTrueTimes.add(trueTimes.get(i));
                newNotes.add(notes.get(i));
                newDailyReminders.add(dailyReminders.get(i));
                originalPosition.add(i);
            }}catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                itemList.smoothScrollToPosition(0);
                newTitles.clear();
                newDescriptions.clear();
                newTimes.clear();
                newSpecificCategory.clear();
                newSpecificColor.clear();
                newNotes.clear();
                newDailyReminders.clear();
                String theDate = (month+1)+"/"+dayOfMonth+"/"+year;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                boolean euroDate = sharedPref.getBoolean("onOrOff",false);
                String temp = theDate;
                if(euroDate){
                    int firstSlash = temp.indexOf("/");
                    int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                    String month2 = temp.substring(0,firstSlash);
                    String day = temp.substring(firstSlash+1,secondSlash);
                    String year2 = temp.substring(secondSlash+1);
                    temp = day+"/"+month2+"/"+year2;
                }
                sectionHeader.setText(temp);
                for (int i = 0; i < titles.size();i++){
                    try {
                        Date theBigDate = new SimpleDateFormat("MM/dd/yyyy").parse(theDate);
                        Date theNewDate = new SimpleDateFormat("MM/dd/yyyy").parse(descriptions.get(i));

                        if (descriptions.get(i).equals(theDate)||((dailyReminders.get(i).equalsIgnoreCase("yes"))&&(theNewDate.after(theBigDate)||theNewDate.equals(theBigDate)))){
                            newTitles.add(titles.get(i));
                            newDescriptions.add(descriptions.get(i));
                            newTimes.add(times.get(i));
                            newSpecificCategory.add(specificCategory.get(i));
                            newSpecificColor.add(specificColor.get(i));
                            newTrueTimes.add(trueTimes.get(i));
                            newNotes.add(notes.get(i));
                            newDailyReminders.add(dailyReminders.get(i));
                            originalPosition.add(i);
                        }}catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
//        FloatingActionButton fab = view.findViewById(R.id.floatingActionButtonAddCalendar);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), CalendarTodo.class);
//                intent.putExtra("category",categoryNames);
//                intent.putExtra("colors", colors);
//                intent.putExtra("date",temp);
//                startActivityForResult(intent, 10007);
//            }
//        });
        if (getArguments() != null) {
            if (getArguments().getString("item") != null) {
                NotificationSender notificationSender = new NotificationSender(getActivity().getBaseContext());
                strtext = getArguments().getString("item");
                dateText = getArguments().getString("chosenDate");
                oneCategory = getArguments().getString("category");
                oneColor = getArguments().getString("color");
                oneTime = getArguments().getString("chosenTime");
                theTrueTime = getArguments().getString("trueTime");
                oneNote = getArguments().getString("notes");
                oneDailyReminder = getArguments().getString("dailyReminder");
                int positionForPending = Integer.parseInt(getArguments().getString("positionForPendingIntent"));
                positionsForPendingIntents.add(getArguments().getString("positionForPendingIntent"));
                boolean sendToNotiRepeating = false;
                if (oneDailyReminder.equalsIgnoreCase("yes")){
                    sendToNotiRepeating=true;
                }
                titles.add(strtext);
                descriptions.add(dateText);
                specificCategory.add(oneCategory);
                specificColor.add(oneColor);
                times.add(oneTime);
                trueTimes.add(theTrueTime);
                if(oneNote!=null) {
                    notes.add(oneNote);
                }
                else{
                    notes.add("None");
                }
                dailyReminders.add(oneDailyReminder);
                if (getArguments().getString("placement")!= null){
                    int whichItem = Integer.parseInt(getArguments().getString("placement"));
                    titles.remove(whichItem);
                    descriptions.remove(whichItem);
                    specificCategory.remove(whichItem);
                    specificColor.remove(whichItem);
                    times.remove(whichItem);
                    trueTimes.remove(whichItem);
                    notes.remove(whichItem);
                    dailyReminders.remove(whichItem);
                    positionsForPendingIntents.remove(whichItem);
                    try {
                        sort();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                notificationSender.removeIt(positionForPending,sendToNotiRepeating,dateText,theTrueTime);
                notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                try {
                    sort();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                writeThosePermanently();
            }
        }
        adapter = new ToDoMyRecyclerViewAdapter(this,getContext(), newTitles, newDescriptions, newSpecificCategory, newSpecificColor, newTimes, newTrueTimes);
        itemList.setAdapter(adapter);
        return view;
    }
    public void sort() throws ParseException {
        ArrayList<Date> newDates = new ArrayList<>();
        ArrayList<Date> newTimes = new ArrayList<>();
        ArrayList<String> newCategoryAll = new ArrayList<String>();
        for (String dates:descriptions){
            newDates.add(new SimpleDateFormat("MM/dd/yyyy").parse(dates));
        }
        for (String times:trueTimes){
            newTimes.add(new SimpleDateFormat("HH:mm").parse(times));
        }

        for (String categoryIn:categoryNames){
            newCategoryAll.add(categoryIn);
        }

        for(int i = 0; i < newDates.size(); i++){
            for (int j = 0; j < newDates.size()-i-1; j++){
                if (newDates.get(j).compareTo(newDates.get(j+1))>0){
                    newDates.add(j,newDates.remove(j+1));
                    descriptions.add(j,descriptions.remove(j+1));
                    titles.add(j,titles.remove(j+1));
                    specificCategory.add(j,specificCategory.remove((j+1)));
                    specificColor.add(j,specificColor.remove((j+1)));
                    times.add(j, times.remove(j+1));
                    trueTimes.add(j, trueTimes.remove(j+1));
                    notes.add(j, notes.remove(j+1));
                    dailyReminders.add(j, dailyReminders.remove(j+1));
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j+1));
                }
            }
        }
        for(int i = 0; i < newTimes.size(); i++){
            for (int j = 0; j < newTimes.size()-i-1; j++){
                if (newTimes.get(j).compareTo(newTimes.get(j+1))>0&&newDates.get(j).equals(newDates.get(j+1))){
                    newTimes.add(j,newTimes.remove(j+1));
                    descriptions.add(j,descriptions.remove(j+1));
                    titles.add(j,titles.remove(j+1));
                    specificCategory.add(j,specificCategory.remove((j+1)));
                    specificColor.add(j,specificColor.remove((j+1)));
                    times.add(j, times.remove(j+1));
                    trueTimes.add(j, trueTimes.remove(j+1));
                    notes.add(j, notes.remove(j+1));
                    dailyReminders.add(j, dailyReminders.remove(j+1));
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j+1));
                }
            }
        }
        newCategoryAll.add(0, "None");
        for (int i = newCategoryAll.size()-1; i > -1; i--){
            for(int j = 0; j < specificCategory.size()-1;j++){
                int firstIndex = newCategoryAll.indexOf(specificCategory.get(j));
                int secondIndex = newCategoryAll.indexOf(specificCategory.get(j+1));
                if (firstIndex>secondIndex&&newDates.get(j+1).equals(newDates.get(j))&&newTimes.get(j+1).equals(newTimes.get(j))){
                    descriptions.add(j,descriptions.remove(j+1));
                    titles.add(j,titles.remove(j+1));
                    specificCategory.add(j,specificCategory.remove((j+1)));
                    specificColor.add(j,specificColor.remove((j+1)));
                    times.add(j, times.remove(j+1));
                    trueTimes.add(j, trueTimes.remove(j+1));
                    notes.add(j, notes.remove(j+1));
                    dailyReminders.add(j, dailyReminders.remove(j+1));
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j+1));
                }
            }
        }
        for (int i = 0; i < titles.size(); i++){
            for(int j = 0; j < titles.size()-i-1;j++){
                String firstChar = titles.get(j).substring(0,1).toUpperCase();
                String secondChar = titles.get(j+1).substring(0,1).toUpperCase();
                if (firstChar.compareTo(secondChar)>0&&specificCategory.get(j).equals(specificCategory.get(j+1))&&newDates.get(j+1).equals(newDates.get(j))&&newTimes.get(j+1).equals(newTimes.get(j))){
                    descriptions.add(j,descriptions.remove(j+1));
                    titles.add(j,titles.remove(j+1));
                    specificCategory.add(j,specificCategory.remove((j+1)));
                    specificColor.add(j,specificColor.remove((j+1)));
                    times.add(j, times.remove(j+1));
                    trueTimes.add(j, trueTimes.remove(j+1));
                    notes.add(j, notes.remove(j+1));
                    dailyReminders.add(j, dailyReminders.remove(j+1));
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j+1));
                }
            }
        }
        writeThosePermanently();
    }

    public void writeThosePermanently(){
        FileHelper.writeData(titles, getContext(), 11);
        FileHelper.writeData(descriptions, getContext(), 12);
        FileHelper.writeData(specificCategory, getContext(),13);
        FileHelper.writeData(specificColor,getContext(),14);
        FileHelper.writeData(times,getContext(),15);
        FileHelper.writeData(categoryNames,getContext(),16);
        FileHelper.writeData(colors,getContext(),17);
        FileHelper.writeData(trueTimes,getContext(),18);
        FileHelper.writeData(notes,getContext(),19);
        FileHelper.writeData(dailyReminders,getContext(),111);
    }


    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", newTitles.get(position));
        bundle.putString("notes",newNotes.get(position));
        bundle.putString("category", newSpecificCategory.get(position));
        bundle.putString("color", newSpecificColor.get(position));
        bundle.putString("chosenTime", newTimes.get(position));
        bundle.putString("trueTime", newTrueTimes.get(position));
        bundle.putString("chosenDate", newDescriptions.get(position));
        bundle.putString("dailyReminderNow", newDailyReminders.get(position));
        bundle.putStringArrayList("allCategories", categoryNames);
        bundle.putStringArrayList("allColors", colors);
        bundle.putInt("position", position);
        ToDoFragment.ToDoBottomSheetDialog frag = new ToDoFragment.ToDoBottomSheetDialog();
        frag.setArguments(bundle);
        FragmentTransaction f = getFragmentManager().beginTransaction();
        f.add(frag, "lol");
        f.detach(frag);
        f.attach(frag);
        f.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if ((requestCode == 10007) && (resultCode == Activity.RESULT_OK)) {
//            adapter = new ToDoMyRecyclerViewAdapter(this,getContext(), newTitles, newDescriptions, newSpecificCategory, newSpecificColor, newTimes, newTrueTimes);
            adapter.notifyDataSetChanged();
            itemList.setAdapter(adapter);
            ft.detach(this).attach(this).commit();
        }

    }
}

