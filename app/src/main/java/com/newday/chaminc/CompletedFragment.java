package com.newday.chaminc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CompletedFragment extends Fragment implements CompletedMyRecyclerViewAdapter.ItemClickListener {
    private ArrayList<String> allItems;
    private String strtext = "";
    private RecyclerView itemList;
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> individualDates = new ArrayList<String>();
    private ArrayList<String> times = new ArrayList<String>();
    private ArrayList<String> trueTimes = new ArrayList<String>();
    private ArrayList<String> categoryNames = new ArrayList<String>();
    private ArrayList<String> specificCategory = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    private ArrayList<String> specificColor = new ArrayList<String>();
    private ArrayList<String> notes = new ArrayList<String>();
    private ArrayList<String> dailyReminders = new ArrayList<String>();
    private ArrayList<String> positionsForPendingIntents = new ArrayList<String>();
    private Snackbar snackbar;
    private FloatingActionButton fabPurge;
    private ArrayList<String> completedTitles = new ArrayList<String>();
    private ArrayList<String> completedDescriptions = new ArrayList<String>();
    private ArrayList<String> completedTimes = new ArrayList<>();
    private ArrayList<String> completedTrueTimes = new ArrayList<String>();
    private ArrayList<String> completedSpecificCategory = new ArrayList<String>();
    private ArrayList<String> completedSpecificColor = new ArrayList<String>();
    private ArrayList<String> completedNotes = new ArrayList<String>();
    private ArrayList<String> completedDailyReminders = new ArrayList<String>();
    private ArrayList<String> completedPositionsForPendingIntents = new ArrayList<String>();
    private Context context;
    TextView empty;


    CompletedMyRecyclerViewAdapter adapter;
    LinearLayoutManager mLayoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        empty = view.findViewById(R.id.emptyCompletedText);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if (darkTheme){
            view.setBackgroundColor(getContext().getResources().getColor(R.color.black));
            empty.setTextColor(getContext().getResources().getColor(R.color.white));
        }
        allItems = FileHelper.readData(getContext(), 2);
        itemList = view.findViewById(R.id.completedItems);
        titles = FileHelper.readData(getContext(), 11);
        descriptions = FileHelper.readData(getContext(), 12);
        specificCategory = FileHelper.readData(getContext(), 13);
        specificColor = FileHelper.readData(getContext(), 14);
        times = FileHelper.readData(getContext(), 15);
        categoryNames = FileHelper.readData(getContext(), 16);
        colors = FileHelper.readData(getContext(), 17);
        trueTimes = FileHelper.readData(getContext(), 18);
        notes = FileHelper.readData(getContext(), 19);
        dailyReminders = FileHelper.readData(getContext(), 111);
        completedTitles = FileHelper.readData(getContext(), 21);
        completedDescriptions = FileHelper.readData(getContext(), 22);
        completedSpecificCategory = FileHelper.readData(getContext(), 23);
        completedSpecificColor = FileHelper.readData(getContext(), 24);
        completedTimes = FileHelper.readData(getContext(), 25);
        completedTrueTimes = FileHelper.readData(getContext(), 28);
        completedNotes = FileHelper.readData(getContext(), 29);
        completedDailyReminders = FileHelper.readData(getContext(), 211);
        positionsForPendingIntents = FileHelper.readData(getContext(),3);
        completedPositionsForPendingIntents = FileHelper.readData(getContext(),4);
        mLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(mLayoutManager);
        context = getActivity().getApplicationContext();
        adapter = new CompletedMyRecyclerViewAdapter(this, getContext(), completedTitles, completedDescriptions, completedSpecificCategory, completedSpecificColor, completedTimes, completedTrueTimes);
        itemList.setAdapter(adapter);

        fabPurge = view.findViewById(R.id.floatingActionButtonPURGEITALL);
        fabPurge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeThosePermanently();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                builder.setTitle("Do you want to permanently delete all items?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        for (int i = 0; i < completedTitles.size();i++){
                            removeThat(i);
                            i--;
                        }
                        adapter.notifyDataSetChanged();
                        empty.setText("You haven't completed any tasks yet.\n\nLet's get some work done 🙂");
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        if(completedTitles.size()==0){
            empty.setText("You haven't completed any tasks yet.\n\nLet's get some work done 🙂");
        }

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                switch (swipeDir) {
                    case ItemTouchHelper.LEFT:
                        final String deletedTitle = completedTitles.get(position);
                        final String deletedDescription = completedDescriptions.get(position);
                        final String deletedCategory = completedSpecificCategory.get(position);
                        final String deletedColor = completedSpecificColor.get(position);
                        final String deletedTime = completedTimes.get(position);
                        final String deletedTrueTime = completedTrueTimes.get(position);
                        final String deletedNotes = completedNotes.get(position);
                        final String deletedDailyReminder = completedDailyReminders.get(position);
                        final String deletedPositionForPending = completedPositionsForPendingIntents.get(position);
                        removeThat(position);
                        adapter.notifyItemRemoved(position);
                        if (completedTitles.size()==0){
                            empty.setText("You haven't completed any tasks yet.\n\nLet's get some work done 🙂");
                        }
//                        snackbar = Snackbar.make(itemList, deletedTitle + " has been permanently deleted.", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    getItBack(position, deletedTitle, deletedDescription, deletedCategory, deletedColor, deletedTime, deletedTrueTime, deletedNotes, deletedDailyReminder, deletedPositionForPending);
//                                    adapter.notifyItemInserted(position);
////                                    adapter.notifyDataSetChanged();
//                                    sortOriginal();
//                                    TextView text = getView().findViewById(R.id.emptyCompletedText);
//                                    text.setText("");
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        snackbar.show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        final String deletedTitle2 = completedTitles.get(position);
                        final String deletedDescription2 = completedDescriptions.get(position);
                        final String deletedCategory2 = completedSpecificCategory.get(position);
                        final String deletedColor2 = completedSpecificColor.get(position);
                        final String deletedTime2 = completedTimes.get(position);
                        final String deletedTrueTime2 = completedTrueTimes.get(position);
                        final String deletedNotes2 = completedNotes.get(position);
                        final String deletedDailyReminder2 = completedDailyReminders.get(position);
                        final String deletedPendingPosition2 = completedPositionsForPendingIntents.get(position);
                        titles.add(deletedTitle2);
                        descriptions.add(deletedDescription2);
                        specificCategory.add(deletedCategory2);
                        specificColor.add(deletedColor2);
                        times.add(deletedTime2);
                        trueTimes.add(deletedTrueTime2);
                        notes.add(deletedNotes2);
                        dailyReminders.add(deletedDailyReminder2);
                        positionsForPendingIntents.add(deletedPendingPosition2);
                        addThatToReadded();
                        removeThat(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyDataSetChanged();
                        boolean repeating = false;
                        if (dailyReminders.get(dailyReminders.size()-1).equalsIgnoreCase("yes")){
                            repeating = true;
                        }
                        if(completedTitles.size()==0){
                            empty.setText("You haven't completed any tasks yet.\n\nLet's get some work done 🙂");
                        }
                        whenReaddedNotificationHandler(Integer.parseInt(positionsForPendingIntents.get(dailyReminders.size()-1)),
                                descriptions.get(dailyReminders.size()-1),trueTimes.get(dailyReminders.size()-1),
                                specificCategory.get((dailyReminders.size()-1)),repeating,
                                titles.get(dailyReminders.size()-1));
//                        snackbar = Snackbar.make(itemList, deletedTitle2 + " has been added back", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    readdThat(position, deletedTitle2, deletedDescription2, deletedCategory2, deletedColor2, deletedTime2, deletedTrueTime2, deletedNotes2, deletedDailyReminder2, deletedPendingPosition2);
//                                    adapter.notifyItemInserted(position);
//                                    TextView empty = getView().findViewById(R.id.emptyCompletedText);
//                                    empty.setText("");
////                                    adapter.notifyDataSetChanged();
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        snackbar.show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightOrange))
                        .addSwipeRightActionIcon(R.drawable.ic_round_history_24_white)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.darkishRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_round_delete_forever_24_white)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemList);
        return view;
    }
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", completedTitles.get(position));
        bundle.putString("notes", completedNotes.get(position));
        bundle.putString("category", completedSpecificCategory.get(position));
        bundle.putString("color", completedSpecificColor.get(position));
        bundle.putString("chosenTime", completedTimes.get(position));
        bundle.putString("trueTime", completedTrueTimes.get(position));
        bundle.putString("chosenDate", completedDescriptions.get(position));
        bundle.putString("dailyReminderNow", completedDailyReminders.get(position));
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

    public void sortOriginal() throws ParseException {
        ArrayList<Date> newDates = new ArrayList<>();
        ArrayList<Date> newTimes = new ArrayList<>();
        ArrayList<String> newCategoryAll = new ArrayList<String>();
        for (String dates:descriptions){
            newDates.add(new SimpleDateFormat("MM/dd/yyyy").parse(dates));
        }
        for (String times:trueTimes){
            int colon = times.indexOf(":");
            if(times.substring(colon+1).length()<2){
                times = times.substring(0,colon+1)+"0"+times.substring(colon+1);
            }
            if(times.substring(0,colon).length()<2){
                times = "0"+times;
            }
            newTimes.add(new SimpleDateFormat("hh:mm").parse(times));
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
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j + 1));

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
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j + 1));
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
                    positionsForPendingIntents.add(j, positionsForPendingIntents.remove(j + 1));
                }
            }
        }
        writeThosePermanently();
    }

    public void writeThosePermanently() {
        FileHelper.writeData(titles, getContext(), 11);
        FileHelper.writeData(descriptions, getContext(), 12);
        FileHelper.writeData(specificCategory, getContext(), 13);
        FileHelper.writeData(specificColor, getContext(), 14);
        FileHelper.writeData(times, getContext(), 15);
        FileHelper.writeData(categoryNames, getContext(), 16);
        FileHelper.writeData(colors, getContext(), 17);
        FileHelper.writeData(trueTimes, getContext(), 18);
        FileHelper.writeData(notes, getContext(), 19);
        FileHelper.writeData(dailyReminders, getContext(), 111);
        FileHelper.writeData(completedTitles, getContext(), 21);
        FileHelper.writeData(completedDescriptions, getContext(), 22);
        FileHelper.writeData(completedSpecificCategory, getContext(), 23);
        FileHelper.writeData(completedSpecificColor, getContext(), 24);
        FileHelper.writeData(completedTimes, getContext(), 25);
        FileHelper.writeData(completedTrueTimes, getContext(), 28);
        FileHelper.writeData(completedNotes, getContext(), 29);
        FileHelper.writeData(completedDailyReminders, getContext(), 211);
        FileHelper.writeData(positionsForPendingIntents, getContext(), 3);
        FileHelper.writeData(completedPositionsForPendingIntents, getContext(), 4);
    }

//    If the user undoes the delete, the notification is readded
    public void whenReaddedNotificationHandler(int positionForPendingInt, String sendToNotiDescription, String sendToNotiTime, String sendToNotiCategory, boolean sendToNotiRepeating, String sendToNotiTitle) {
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        assert alarmManager != null;
//        Intent intent = new Intent(getContext(), BootReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, positionForPendingInt, intent, PendingIntent.FLAG_ONE_SHOT);
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
//        c.set(Calendar.MONTH, month);
//        c.set(Calendar.DAY_OF_MONTH, day);
//        c.set(Calendar.YEAR, year);
//        c.set(Calendar.HOUR_OF_DAY, hour);
//        c.set(Calendar.MINUTE, minute);
//        c.set(Calendar.SECOND, 0);
//        intent.putExtra("title",sendToNotiTitle);
//        intent.putExtra("positionPending",positionForPendingInt);
//        Toast.makeText(getContext(), sendToNotiTitle, Toast.LENGTH_SHORT).show();
//        if (sendToNotiRepeating) {
//            if (c.before(Calendar.getInstance())){
//                c.add(Calendar.DATE,1);
//            }
//            assert alarmManager != null;
//            intent.putExtra("message","Here's your repeating reminder");
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayInMillis, pendingIntent);
//        } else {
//            if (c.before(Calendar.getInstance())) {
//                c.add(Calendar.DATE, 1);
//            }
//            assert alarmManager != null;
//            intent.putExtra("message","Here's your one-time reminder");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            } else {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            }
//        }
        NotificationSender notificationSender = new NotificationSender(getActivity().getBaseContext());
        notificationSender.sendIt(sendToNotiTitle,sendToNotiDescription,sendToNotiTime, sendToNotiCategory, sendToNotiRepeating,positionForPendingInt);

    }

    public static class ToDoBottomSheetDialog extends BottomSheetDialogFragment {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.todoitemviewer, container, false);
            final String itemTitle = getArguments().getString("title");
            final String itemNote = getArguments().getString("notes");
            final String itemCategory = getArguments().getString("category");
            final String itemColor = getArguments().getString("color");
            final String itemTime = getArguments().getString("chosenTime");
            String itemDate = getArguments().getString("chosenDate");
            final String itemDailyReminder = getArguments().getString("dailyReminderNow");
            final String itemTrueTime = getArguments().getString("trueTime");
            ArrayList<String> allCategories = getArguments().getStringArrayList("allCategories");
            ArrayList<String> allColors = getArguments().getStringArrayList("allColors");
            int position = getArguments().getInt("position");
            TextView titleTextView = v.findViewById(R.id.editText);
            TextView noteTextView = v.findViewById(R.id.textView4);
            TextView categoryTextView = v.findViewById(R.id.textView3);
            TextView timeTextView = v.findViewById(R.id.textView2);
            TextView dateTextView = v.findViewById(R.id.textView);
            TextView dailyReminderTextView = v.findViewById(R.id.textView5);
            ImageView firstIcon = v.findViewById(R.id.dateToCompleteByImage);
            ImageView secondIcon = v.findViewById(R.id.timeToRemindAtImage);
            ImageView thirdIcon = v.findViewById(R.id.remindDailyImage);
            ImageView fourthIcon = v.findViewById(R.id.categoryOfItemImage);
            ImageView fifthIcon = v.findViewById(R.id.notesAboutItemImage);
//            ImageView handle = v.findViewById(R.id.handleTop);
            titleTextView.setText(itemTitle);
            noteTextView.setText(itemNote);
            categoryTextView.setText(itemCategory);
            FloatingActionButton shareFab = v.findViewById(R.id.shareFAB);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean euroDate = sharedPref.getBoolean("onOrOff",false);
            String temp = itemDate;
            if(euroDate){
                int firstSlash = temp.indexOf("/");
                int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                String month2 = temp.substring(0,firstSlash);
                String day = temp.substring(firstSlash+1,secondSlash);
                String year2 = temp.substring(secondSlash+1);
                itemDate = day+"/"+month2+"/"+year2;
            }
            boolean twentyFourTime = sharedPref.getBoolean("onOrOffTime", false);
            if (twentyFourTime) {
                timeTextView.setText(itemTrueTime);
            }
            else {
                timeTextView.setText(itemTime);
            }
            final String completedItemDate = itemDate;
            dateTextView.setText(itemDate);
            dailyReminderTextView.setText(itemDailyReminder);
            if (itemDailyReminder.equals("No")) {
                thirdIcon.setImageResource(R.drawable.ic_round_alarm_off_24);
            }
            int colorInt = 0;
            int colorDrawable;
            if (itemColor != null) {
                if (itemColor.equals("Dark Red")) {
                    colorInt = getResources().getColor(R.color.darkRed);
                } else if (itemColor.equals("Red")) {
                    colorInt = getResources().getColor(R.color.lightRed);
                } else if (itemColor.equals("Dark Orange")) {
                    colorInt = getResources().getColor(R.color.darkOrange);
                } else if (itemColor.equals("Orange")) {
                    colorInt = getResources().getColor(R.color.lightOrange);
                } else if (itemColor.equals("Yellow")) {
                    colorInt = getResources().getColor(R.color.lightYellow);
                } else if (itemColor.equals("Dark Green")) {
                    colorInt = getResources().getColor(R.color.darkGreen);
                } else if (itemColor.equals("Green")) {
                    colorInt = getResources().getColor(R.color.lightGreen);
                } else if (itemColor.equals("Dark Blue")) {
                    colorInt = getResources().getColor(R.color.darkBlue);
                } else if (itemColor.equals("Blue")) {
                    colorInt = getResources().getColor(R.color.lightBlue);
                } else if (itemColor.equals("Dark Purple")) {
                    colorInt = getResources().getColor(R.color.darkPurple);
                } else if (itemColor.equals("Purple")) {
                    colorInt = getResources().getColor(R.color.lightPurple);
                } else if (itemColor.equals("Dark Pink")) {
                    colorInt = getResources().getColor(R.color.darkPink);
                } else if (itemColor.equals("Pink")) {
                    colorInt = getResources().getColor(R.color.lightPink);
                } else {
                    colorInt = getResources().getColor(R.color.colorGrey);
                }
            } else {
                colorInt = getResources().getColor(R.color.colorGrey);
            }
            TextView name2 = v.findViewById(R.id.nameTitle2);
            TextView name3 = v.findViewById(R.id.nameTitle3);
            TextView name4 = v.findViewById(R.id.nameTitle4);
            TextView name5 = v.findViewById(R.id.nameTitle5);
            TextView name6 = v.findViewById(R.id.nameTitle6);
            ImageView backgroundColor = v.findViewById(R.id.backgroundColor);
//            firstIcon.setColorFilter(getResources().getColor(R.color.colorLightGrey));
//            secondIcon.setColorFilter(getResources().getColor(R.color.colorLightGrey));
//            thirdIcon.setColorFilter(getResources().getColor(R.color.colorLightGrey));
//            fourthIcon.setColorFilter(getResources().getColor(R.color.colorLightGrey));
//            fifthIcon.setColorFilter(getResources().getColor(R.color.colorLightGrey));
            titleTextView.setTextColor(colorInt);
//            noteTextView.setBackgroundTintList(ColorStateList.valueOf(colorInt));
//            categoryTextView.setBackgroundTintList(ColorStateList.valueOf(colorInt));
//            timeTextView.setBackgroundTintList(ColorStateList.valueOf(colorInt));
//            dailyReminderTextView.setBackgroundTintList(ColorStateList.valueOf(colorInt));
//            dateTextView.setBackgroundTintList(ColorStateList.valueOf(colorInt));
            name2.setTextColor(colorInt);
            name3.setTextColor(colorInt);
            name4.setTextColor(colorInt);
            name5.setTextColor(colorInt);
            name6.setTextColor(colorInt);
            backgroundColor.setBackgroundColor(colorInt);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
            shareFab.setBackgroundTintList(ColorStateList.valueOf(colorInt));
            shareFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = completedItemDate+" @ "+itemTime+"\nNotes: "+itemNote;
                    String shareTitle = itemCategory+": "+itemTitle;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareTitle);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share your item!"));
                }
            });
            if (darkTheme){
                View viewBack = v.findViewById(R.id.backgroundTodoItemViewer);
                viewBack.setBackgroundColor(getContext().getResources().getColor(R.color.black));
                int theme = R.color.colorLightGrey;
                int theme2 = R.color.white;
                noteTextView.setTextColor(getContext().getColor(theme2));
                categoryTextView.setTextColor(getContext().getColor(theme2));
                timeTextView.setTextColor(getContext().getColor(theme2));
                dateTextView.setTextColor(getContext().getColor(theme2));
                dailyReminderTextView.setTextColor(getContext().getColor(theme2));
                firstIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(theme)));
                secondIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(theme)));
                thirdIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(theme)));
                fourthIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(theme)));
                fifthIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(theme)));
            }
//            handle.setColorFilter(getResources().getColor(R.color.white));
            return v;
        }
    }

    public void addThatToReadded() {
        FileHelper.writeData(titles, getContext(), 11);
        FileHelper.writeData(descriptions, getContext(), 12);
        FileHelper.writeData(specificCategory, getContext(), 13);
        FileHelper.writeData(specificColor, getContext(), 14);
        FileHelper.writeData(times, getContext(), 15);
        FileHelper.writeData(trueTimes, getContext(), 18);
        FileHelper.writeData(notes, getContext(), 19);
        FileHelper.writeData(dailyReminders, getContext(), 111);
        FileHelper.writeData(positionsForPendingIntents, getContext(), 3);
    }

    public void removeThat(int whichItem) {
        completedTitles.remove(whichItem);
        completedDescriptions.remove(whichItem);
        completedSpecificCategory.remove(whichItem);
        completedSpecificColor.remove(whichItem);
        completedTimes.remove(whichItem);
        completedTrueTimes.remove(whichItem);
        completedNotes.remove(whichItem);
        completedDailyReminders.remove(whichItem);
        completedPositionsForPendingIntents.remove(whichItem);
        writeThosePermanently();
    }

    //Readd the item if the user undos the delete option
    public void readdThat(int position, String title, String descriptionsString, String category, String color, String timesString, String trueTime, String theNote, String theDailyReminder, String thePendingPosition) throws ParseException {
        completedTitles.add(position, title);
        completedDescriptions.add(position, descriptionsString);
        completedSpecificCategory.add(position, category);
        completedSpecificColor.add(position, color);
        completedTimes.add(position, timesString);
        completedTrueTimes.add(position, trueTime);
        completedNotes.add(position, theNote);
        completedDailyReminders.add(position, theDailyReminder);
        completedPositionsForPendingIntents.add(position, thePendingPosition);
        int completed = titles.size() - 1;
        titles.remove(completed);
        descriptions.remove(completed);
        specificCategory.remove(completed);
        specificColor.remove(completed);
        times.remove(completed);
        trueTimes.remove(completed);
        notes.remove(completed);
        dailyReminders.remove(completed);
        positionsForPendingIntents.remove(completed);
        writeThosePermanently();
        addThatToReadded();
    }
    //If the user undoes the restore option
    public void getItBack(int position, String title, String descriptionsString, String category, String color, String timesString, String trueTime, String theNote, String theDailyReminder, String thePendingPosition) throws ParseException {
        completedTitles.add(position, title);
        completedDescriptions.add(position, descriptionsString);
        completedSpecificCategory.add(position, category);
        completedSpecificColor.add(position, color);
        completedTimes.add(position, timesString);
        completedTrueTimes.add(position, trueTime);
        completedNotes.add(position, theNote);
        completedDailyReminders.add(position, theDailyReminder);
        completedPositionsForPendingIntents.add(position, thePendingPosition);
        int positionn = Integer.parseInt(completedPositionsForPendingIntents.get(position));
        writeThosePermanently();
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        assert alarmManager != null;
//        Intent intent = new Intent(getContext(), BootReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, positionn, intent, PendingIntent.FLAG_ONE_SHOT);
//        intent.removeExtra("title");
//        alarmManager.cancel(pendingIntent);
        NotificationSender notificationSender = new NotificationSender(getActivity().getBaseContext());
        boolean singleOrRepeating = false;
        if (theDailyReminder.equalsIgnoreCase("no")) {
            singleOrRepeating = true;
        }
        notificationSender.removeIt(positionn,singleOrRepeating,descriptionsString,timesString);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (snackbar!= null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            sortOriginal();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}


