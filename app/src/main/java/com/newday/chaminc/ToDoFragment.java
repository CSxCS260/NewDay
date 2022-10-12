package com.newday.chaminc;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.todolist.mynewday.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.content.Context.ALARM_SERVICE;

public class ToDoFragment extends Fragment implements ToDoMyRecyclerViewAdapter.ItemClickListener {
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
    private ArrayList<String> positionsForPendingIntents = new ArrayList<String>();
    private Snackbar snackbar;
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
    private int pendingIntentInt = 0;
    private RecyclerItemDecoration recyclerItemDecoration;
    private RecyclerView itemList;
    ToDoMyRecyclerViewAdapter adapter;
    LinearLayoutManager mLayoutManager;
    boolean isRotate = false;
    TextView empty;

    private GoogleMap googleMap;
    private GeofencingClient geofencingClient;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Let's read the data we already have
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        empty = view.findViewById(R.id.emptyText);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme", false);
        if (darkTheme) {
            view.setBackgroundColor(getContext().getResources().getColor(R.color.black));
            empty.setTextColor(getContext().getResources().getColor(R.color.white));
        }

        sendTheWallpaper();

        itemList = view.findViewById(R.id.items);
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
        positionsForPendingIntents = FileHelper.readData(getContext(), 3);
        completedPositionsForPendingIntents = FileHelper.readData(getContext(), 4);




        mLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(mLayoutManager);
        context = getActivity().getApplicationContext();
        for (int i = 0; i < positionsForPendingIntents.size(); i++) {
            String strtext = titles.get(i);
            String dateText = descriptions.get(i);
            String theTrueTime = trueTimes.get(i);
            String oneCategory = specificCategory.get(i);
            int positionForPending = Integer.parseInt(positionsForPendingIntents.get(i));
            boolean sendToNotiRepeating = false;
            if (dailyReminders.get(i).equalsIgnoreCase("yes")) {
                sendToNotiRepeating = true;
            }
            NotificationSender notificationSender = new NotificationSender(context);
            notificationSender.removeIt(positionForPending, sendToNotiRepeating, dateText, theTrueTime);
            notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
        }






        try {
            sort();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter = new ToDoMyRecyclerViewAdapter(this, getContext(), titles, descriptions, specificCategory, specificColor, times, trueTimes);
        itemList.setAdapter(adapter);
        recyclerItemDecoration = new RecyclerItemDecoration(getContext(), getResources().getDimensionPixelSize(R.dimen.other_header_height), false, getSectionCallback(descriptions));
        itemList.addItemDecoration(recyclerItemDecoration);
//        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, allItems);
//        adapter = new MyAdapter(getContext(), titles, descriptions,specificCategory,specificColor,times);


        final FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        final FloatingActionButton fabNew = view.findViewById(R.id.fabNew);
        final FloatingActionButton fabEditCategories = view.findViewById(R.id.fabEditCategories);
        final TextView fabNewDesc = view.findViewById(R.id.fabNewDesc);
        final FloatingActionButton fabNewLoc = view.findViewById(R.id.fabNewLoc);
        final TextView fabNewLocDesc = view.findViewById(R.id.fabNewLocDesc);
//        Typeface customFont1 = Typeface.createFromAsset(assetManager(),"fonts/Montserrat-Medium.otf");
//        fabNewDesc.setTypeface(customFont1);
        final TextView fabEditCategoriesDesc = view.findViewById(R.id.fabEditCategoriesDesc);
        if (titles.size() == 0) {
            empty.setText("Looks like your schedule's totally open.\n\nEnjoy that free time! 😉");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if (isRotate) {
                    categoryNames = FileHelper.readData(getContext(), 16);
                    colors = FileHelper.readData(getContext(), 17);
                    ViewAnimation.showIn(fabNewLoc);
                    ViewAnimation.showIn(fabNewLocDesc);
                    ViewAnimation.showIn(fabNew);
                    ViewAnimation.showIn(fabNewDesc);
                    ViewAnimation.showIn(fabEditCategories);
                    ViewAnimation.showIn(fabEditCategoriesDesc);
                    fabNew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), CreatingTodo.class);
                            intent.putExtra("category", categoryNames);
                            intent.putExtra("colors", colors);
                            pendingIntentInt = (int) (Math.random() * (Integer.MAX_VALUE - 1) + 1);
                            intent.putExtra("positionForPendingIntent",pendingIntentInt);
                            fab.animate().rotationBy(-135);
                            startActivityForResult(intent, 10000);
                            ViewAnimation.showOut(fabNew);
                            ViewAnimation.showOut(fabNewDesc);
                            ViewAnimation.showOut(fabEditCategories);
                            ViewAnimation.showOut(fabEditCategoriesDesc);
                            ViewAnimation.showOut(fabNewLocDesc);
                            ViewAnimation.showOut(fabNewLoc);
                        }
                    });
                    fabNewDesc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), CreatingTodo.class);
                            intent.putExtra("category", categoryNames);
                            intent.putExtra("colors", colors);
                            pendingIntentInt = (int) (Math.random() * (Integer.MAX_VALUE - 1) + 1);
                            intent.putExtra("positionForPendingIntent", pendingIntentInt);
                            fab.animate().rotationBy(-135);
                            startActivityForResult(intent, 10000);
                            ViewAnimation.showOut(fabNew);
                            ViewAnimation.showOut(fabNewDesc);
                            ViewAnimation.showOut(fabEditCategories);
                            ViewAnimation.showOut(fabEditCategoriesDesc);
                            ViewAnimation.showOut(fabNewLocDesc);
                            ViewAnimation.showOut(fabNewLoc);
                        }
                    });
                    fabEditCategories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditingCategories.class);
                            fab.animate().rotationBy(-135);
                            startActivityForResult(intent, 10003);
                            ViewAnimation.showOut(fabNew);
                            ViewAnimation.showOut(fabNewDesc);
                            ViewAnimation.showOut(fabEditCategories);
                            ViewAnimation.showOut(fabEditCategoriesDesc);
                            ViewAnimation.showOut(fabNewLocDesc);
                            ViewAnimation.showOut(fabNewLoc);
                        }
                    });
                    fabEditCategoriesDesc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EditingCategories.class);
                            fab.animate().rotationBy(-135);
                            startActivityForResult(intent, 10003);
                            ViewAnimation.showOut(fabNew);
                            ViewAnimation.showOut(fabNewDesc);
                            ViewAnimation.showOut(fabEditCategories);
                            ViewAnimation.showOut(fabEditCategoriesDesc);
                            ViewAnimation.showOut(fabNewLocDesc);
                            ViewAnimation.showOut(fabNewLoc);
                        }
                    });
                    fabNewLoc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                                return;
                            }
                            else{
                                Intent intent = new Intent(getContext(), CreatingLoc.class);
                                intent.putExtra("category", categoryNames);
                                intent.putExtra("colors", colors);
                                pendingIntentInt = (int) (Math.random() * (Integer.MAX_VALUE - 1) + 1);
                                intent.putExtra("positionForPendingIntent",pendingIntentInt);
                                fab.animate().rotationBy(-135);
                                startActivityForResult(intent, 10004);
                                ViewAnimation.showOut(fabNew);
                                ViewAnimation.showOut(fabNewDesc);
                                ViewAnimation.showOut(fabEditCategories);
                                ViewAnimation.showOut(fabEditCategoriesDesc);
                                ViewAnimation.showOut(fabNewLocDesc);
                                ViewAnimation.showOut(fabNewLoc);
                            }
                        }
                    });
                    fabNewLocDesc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                                return;
                            } else {
                                Intent intent = new Intent(getContext(), CreatingLoc.class);
                                intent.putExtra("category", categoryNames);
                                intent.putExtra("colors", colors);
                                pendingIntentInt = (int) (Math.random() * (Integer.MAX_VALUE - 1) + 1);
                                intent.putExtra("positionForPendingIntent", pendingIntentInt);
                                fab.animate().rotationBy(-135);
                                startActivityForResult(intent, 10004);
                                ViewAnimation.showOut(fabNew);
                                ViewAnimation.showOut(fabNewDesc);
                                ViewAnimation.showOut(fabEditCategories);
                                ViewAnimation.showOut(fabEditCategoriesDesc);
                                ViewAnimation.showOut(fabNewLocDesc);
                                ViewAnimation.showOut(fabNewLoc);
                            }
                        }
                    });

                } else {
                    ViewAnimation.showOut(fabNew);
                    ViewAnimation.showOut(fabNewDesc);
                    ViewAnimation.showOut(fabEditCategories);
                    ViewAnimation.showOut(fabEditCategoriesDesc);
                    ViewAnimation.showOut(fabNewLocDesc);
                    ViewAnimation.showOut(fabNewLoc);
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getContext(), CreatingTodo.class);
                intent.putExtra("category", categoryNames);
                intent.putExtra("colors", colors);
                startActivityForResult(intent, 10000);
                return false;
            }
        });


        //Let's write the new data we received
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
                if (oneDailyReminder.equalsIgnoreCase("yes")) {
                    sendToNotiRepeating = true;
                }
                if (oneCategory == null) {
                    oneCategory = "None";
                }
                if (oneColor == null) {
                    oneColor = "None";
                }
                titles.add(strtext);
                descriptions.add(dateText);
                specificCategory.add(oneCategory);
                specificColor.add(oneColor);
                times.add(oneTime);
                trueTimes.add(theTrueTime);
                if (oneNote != null) {
                    notes.add(oneNote);
                } else {
                    notes.add("None");
                }
                dailyReminders.add(oneDailyReminder);
                if (getArguments().getString("placement") != null) {
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
                if (oneTime.contains("%^&*")) {
                    NotificationSenderLoc notificationSenderLoc = new NotificationSenderLoc(getActivity().getBaseContext());
                    notificationSenderLoc.removeIt(strtext, dateText, oneTime, oneCategory, sendToNotiRepeating, positionForPending);
                    notificationSenderLoc.sendIt(strtext, dateText, oneTime, oneCategory, sendToNotiRepeating, positionForPending);
                }
                else {
                    notificationSender.removeIt(positionForPending, sendToNotiRepeating, dateText, theTrueTime);
                    notificationSender.sendIt(strtext, dateText, theTrueTime, oneCategory, sendToNotiRepeating, positionForPending);
                }
//                sendTheWallpaper();
                try {
                    sort();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                writeThosePermanently();
            }
        }

        adapter.notifyDataSetChanged();


        //Set swiping capabilities
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                switch (swipeDir){
                    case ItemTouchHelper.LEFT:
                        final String deletedTitle = titles.get(position);
                        final String deletedDescription = descriptions.get(position);
                        final String deletedCategory = specificCategory.get(position);
                        final String deletedColor = specificColor.get(position);
                        final String deletedTime = times.get(position);
                        final String deletedTrueTime = trueTimes.get(position);
                        final String deletedNotes = notes.get(position);
                        final String deletedDailyReminder = dailyReminders.get(position);
                        final String deletedPositionForPending = positionsForPendingIntents.get(position);
                        completedTitles.add(0,deletedTitle);
                        completedDescriptions.add(0,deletedDescription);
                        completedSpecificCategory.add(0,deletedCategory);
                        completedSpecificColor.add(0,deletedColor);
                        completedTimes.add(0,deletedTime);
                        completedTrueTimes.add(0,deletedTrueTime);
                        completedNotes.add(0,deletedNotes);
                        completedDailyReminders.add(0,deletedDailyReminder);
                        completedPositionsForPendingIntents.add(0,deletedPositionForPending);
                        addThatToCompleted();
                        adapter.notifyDataSetChanged();
                        removeThat(position);
//                        itemList.invalidateItemDecorations();
                        adapter.notifyItemRemoved(position);
//                        snackbar = Snackbar.make(itemList, deletedTitle + " has been completed! Great job!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                try {
//                                    readdThat(position, deletedTitle, deletedDescription, deletedCategory, deletedColor, deletedTime, deletedTrueTime, deletedNotes, deletedDailyReminder, deletedPositionForPending);
////                                    adapter.notifyItemInserted(position);
//                                    adapter.notifyItemInserted(position);
//                                    TextView text = getView().findViewById(R.id.emptyText);
//                                    text.setText("");
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                                snackbar.getView().getLayoutParams();
//                        params.setMargins(0, 200, 0, 200);
//                        snackbar.getView().setLayoutParams(params);
//                        snackbar.show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        Intent intent;
                        if (times.get(position).contains("%^&*")) {
                            intent = new Intent(getContext(), CreatingLoc.class);
                        }
                        else{
                            intent = new Intent(getContext(), EditingTodo.class);
                        }
                        intent.putExtra("title",titles.get(position));
                        intent.putExtra("description", descriptions.get(position));
                        intent.putExtra("scategory", specificCategory.get(position));
                        intent.putExtra("scolor", specificColor.get(position));
                        intent.putExtra("time", times.get(position));
                        intent.putExtra("trueTime", trueTimes.get(position));
                        intent.putExtra("notes", notes.get(position));
                        intent.putExtra("dailyReminder", dailyReminders.get(position));
                        intent.putExtra("positionForPendingIntent", Integer.parseInt(positionsForPendingIntents.get(position)));
                        intent.putExtra("category",categoryNames);
                        intent.putExtra("colors", colors);
                        intent.putExtra("placement", position);
                        startActivityForResult(intent, 10002);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(getContext(),c, recyclerView,viewHolder,dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(),R.color.darkishGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_round_edit_24_white)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.darkishRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_round_delete_24_white)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemList);


        return view;
    }

    //Sends data back to the RecyclerItemDecoration that shows dates above the items
    private RecyclerItemDecoration.SectionCallback getSectionCallback(final ArrayList<String> addTheseDates){
        return new RecyclerItemDecoration.SectionCallback() {
            @Override
            public boolean isSectionHeader(int pos) {
                return pos==0|| !addTheseDates.get(pos).equals(addTheseDates.get(pos - 1));
            }
            public boolean isSectionHeader2(int pos) {
                return addTheseDates.get(pos).equals(addTheseDates.get(pos + 1));
            }

            @Override
            public String getSectionHeaderName(int pos) {
                return addTheseDates.get(pos);
            }
        };
    }



    //Sort the stuff by date
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
//        sendTheWallpaper();
    }
    //When the "create new item" activity closes
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if ((requestCode == 10000) && (resultCode == Activity.RESULT_OK))
            ft.detach(this).attach(this).commit();
        else if ((requestCode==10001)&&(resultCode==Activity.RESULT_OK)){
            ft.detach(this).attach(this).commit();
        }
        else if ((requestCode==10002)&&(resultCode==Activity.RESULT_OK)){
            ft.detach(this).attach(this).commit();
            itemList.setAdapter(adapter);
        }
        else if ((requestCode==10003)&&(resultCode==Activity.RESULT_OK)){
            ft.detach(this).attach(this).commit();
        }
        else if ((requestCode==10004)&&(resultCode==Activity.RESULT_OK)){
            ft.detach(this).attach(this).commit();
        }
        isRotate=false;
    }

    //Deleting an item and updating the screen
    public void removeThat (int whichItem){
        titles.remove(whichItem);
        String descriptionsString = descriptions.get(whichItem);
        descriptions.remove(whichItem);
        specificCategory.remove(whichItem);
        specificColor.remove(whichItem);
        String timesString = trueTimes.get(whichItem);
        times.remove(whichItem);
        trueTimes.remove(whichItem);
        notes.remove(whichItem);
        boolean singleOrRepeating = false;
        if (dailyReminders.get(whichItem).equalsIgnoreCase("no")) {
            singleOrRepeating = true;
        }
        dailyReminders.remove(whichItem);
        int positionn = Integer.parseInt(positionsForPendingIntents.get(whichItem));
        positionsForPendingIntents.remove(whichItem);
        writeThosePermanently();
        if (titles.size()<1){;
            empty.setText("Looks like your schedule's totally open.\n\nEnjoy that free time! 😉");
        }
        writeThosePermanently();
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        assert alarmManager != null;
//        Intent intent = new Intent(getContext(), BootReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, positionn, intent, PendingIntent.FLAG_ONE_SHOT);
//        intent.removeExtra("title");
//        alarmManager.cancel(pendingIntent);

        if (timesString.contains("%^&*")){
            //Location Notification Sender Class
            NotificationSenderLoc notificationSenderLoc = new NotificationSenderLoc(getActivity().getBaseContext());
//            notificationSenderLoc.removeIt(positionn,singleOrRepeating,descriptionsString,timesString);
            notificationSenderLoc.removeIt(strtext, dateText, oneTime, oneCategory, singleOrRepeating, positionn);
        }
        else {
            NotificationSender notificationSender = new NotificationSender(getActivity().getBaseContext());
            notificationSender.removeIt(positionn, singleOrRepeating, descriptionsString, timesString);
        }
//        sendTheWallpaper();
    }
    //Readd the item if the user undos the delete option
    public void readdThat (int position, String title, String descriptionsString, String category, String color, String timesString, String trueTime, String theNote, String theDailyReminder, String thePositionForPendingIntent) throws ParseException {
//        titles.add(position, title);
//        descriptions.add(position, descriptionsString);
//        specificCategory.add(position, category);
//        specificColor.add(position, color);
//        times.add(position, timesString);
//        trueTimes.add(position, trueTime);
//        notes.add(position, theNote);
//        dailyReminders.add(position, theDailyReminder);
//        positionsForPendingIntents.add(position, thePositionForPendingIntent);
//        int completed = 0;
//        completedTitles.remove(completed);
//        completedDescriptions.remove(completed);
//        completedSpecificCategory.remove(completed);
//        completedSpecificColor.remove(completed);
//        completedTimes.remove(completed);
//        completedTrueTimes.remove(completed);
//        completedNotes.remove(completed);
//        completedDailyReminders.remove(completed);
//        completedPositionsForPendingIntents.remove(completed);
//        boolean repeating = false;
//        if (dailyReminders.get(position).equalsIgnoreCase("yes")){
//            repeating = true;
//        }
//        whenReaddedNotificationHandler(Integer.parseInt(positionsForPendingIntents.get(position)),
//                descriptions.get(position),trueTimes.get(position), specificCategory.get(position), repeating, titles.get(position));
//        writeThosePermanently();
//        addThatToCompleted();
//        sort();
    }
//    If the user undoes the delete, the notification is readded
    public void whenReaddedNotificationHandler(int positionForPendingInt, String sendToNotiDescription, String sendToNotiTime, String sendToNotiCategory, boolean sendToNotiRepeating, String sendToNotiTitle) {
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        assert alarmManager != null;
//        Intent intent = new Intent(context, BootReceiver.class);
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
//        intent.putExtra("title", sendToNotiTitle);
//        intent.putExtra("positionPending",positionForPendingInt);
//        Toast.makeText(getContext(), sendToNotiTitle, Toast.LENGTH_SHORT).show();
//        if (sendToNotiRepeating) {
//            assert alarmManager != null;
//            intent.putExtra("message","Here's your repeating reminder");
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), dayInMillis, pendingIntent);
//        } else {
//            assert alarmManager != null;
//            intent.putExtra("message","Here's your one-time reminder");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            } else {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            }
//        }
//
//        NotificationSender notificationSender = new NotificationSender(getActivity().getBaseContext());
//        notificationSender.sendIt(sendToNotiTitle,sendToNotiDescription,sendToNotiTime, sendToNotiCategory,sendToNotiRepeating,positionForPendingInt);
    }


    //Write all the data from arraylists into files
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
        FileHelper.writeData(completedTitles, getContext(), 21);
        FileHelper.writeData(completedDescriptions, getContext(), 22);
        FileHelper.writeData(completedSpecificCategory, getContext(),23);
        FileHelper.writeData(completedSpecificColor,getContext(),24);
        FileHelper.writeData(completedTimes,getContext(),25);
        FileHelper.writeData(completedTrueTimes,getContext(),28);
        FileHelper.writeData(completedNotes, getContext(),29);
        FileHelper.writeData(completedDailyReminders, getContext(), 211);
        FileHelper.writeData(positionsForPendingIntents, getContext(), 3);
        FileHelper.writeData(completedPositionsForPendingIntents, getContext(), 4);
    }
    //Add removed data into the completed arraylists
    public void addThatToCompleted(){
        FileHelper.writeData(completedTitles, getContext(), 21);
        FileHelper.writeData(completedDescriptions, getContext(), 22);
        FileHelper.writeData(completedSpecificCategory, getContext(),23);
        FileHelper.writeData(completedSpecificColor,getContext(),24);
        FileHelper.writeData(completedTimes,getContext(),25);
        FileHelper.writeData(completedTrueTimes,getContext(),28);
        FileHelper.writeData(completedNotes, getContext(),29);
        FileHelper.writeData(completedDailyReminders, getContext(), 211);
        FileHelper.writeData(completedPositionsForPendingIntents, getContext(), 4);
    }


    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", titles.get(position));
        bundle.putString("notes",notes.get(position));
        bundle.putString("category", specificCategory.get(position));
        bundle.putString("color", specificColor.get(position));
        bundle.putString("chosenTime", times.get(position));
        bundle.putString("trueTime", trueTimes.get(position));
        bundle.putString("chosenDate", descriptions.get(position));
        bundle.putString("dailyReminderNow", dailyReminders.get(position));
        bundle.putStringArrayList("allCategories", categoryNames);
        bundle.putStringArrayList("allColors", colors);
        bundle.putInt("position", position);
        ToDoBottomSheetDialog frag = new ToDoBottomSheetDialog();
        frag.setArguments(bundle);
        FragmentTransaction f = getFragmentManager().beginTransaction();
        f.add(frag, "lol");
        f.detach(frag);
        f.attach(frag);
        f.commit();
    }

    public static class ToDoBottomSheetDialog extends BottomSheetDialogFragment{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.todoitemviewer, container, false);
            final String itemTitle = getArguments().getString("title");
            final String itemNote = getArguments().getString("notes");
            final String itemCategory = getArguments().getString("category");
            final String itemColor = getArguments().getString("color");
            String tempString = getArguments().getString("chosenTime");
            final String itemTime;
            if(tempString.contains("%^&*")){
                itemTime = tempString.substring(0,tempString.indexOf("%^&*"));
            }
            else{
                itemTime = tempString;
            }
//            final String itemTime = getArguments().getString("chosenTime");
            String itemDate = getArguments().getString("chosenDate");
            final String itemDailyReminder = getArguments().getString("dailyReminderNow");
            final String itemTrueTime = getArguments().getString("trueTime");
            ArrayList<String> allCategories = getArguments().getStringArrayList("allCategories");
            ArrayList<String> allColors = getArguments().getStringArrayList("allColors");
            int position = getArguments().getInt("position");
            TextView titleTextView = v.findViewById(R.id.editText);
            final TextView noteTextView = v.findViewById(R.id.textView4);
            TextView categoryTextView = v.findViewById(R.id.textView3);
            final TextView timeTextView = v.findViewById(R.id.textView2);
            TextView dateTextView = v.findViewById(R.id.textView);
            TextView categoryTitle = v.findViewById(R.id.nameTitle2);
            TextView dailyReminderTextView = v.findViewById(R.id.textView5);
            ImageView firstIcon = v.findViewById(R.id.dateToCompleteByImage);
            ImageView secondIcon = v.findViewById(R.id.timeToRemindAtImage);
            ImageView thirdIcon = v.findViewById(R.id.remindDailyImage);
            ImageView fourthIcon = v.findViewById(R.id.categoryOfItemImage);
            ImageView fifthIcon = v.findViewById(R.id.notesAboutItemImage);
            FloatingActionButton shareFab = v.findViewById(R.id.shareFAB);
//            ImageView handle = v.findViewById(R.id.handleTop);
            titleTextView.setText(itemTitle);
            noteTextView.setText(itemNote);
            categoryTextView.setText(itemCategory);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean euroDate = sharedPref.getBoolean("onOrOff", false);
            String temp = itemDate;
            if (euroDate) {
                int firstSlash = temp.indexOf("/");
                int secondSlash = temp.substring(firstSlash + 1).indexOf("/") + firstSlash + 1;
                String month2 = temp.substring(0, firstSlash);
                String day = temp.substring(firstSlash + 1, secondSlash);
                String year2 = temp.substring(secondSlash + 1);
                itemDate = day + "/" + month2 + "/" + year2;
            }
            boolean twentyFourTime = sharedPref.getBoolean("onOrOffTime", false);
            if (twentyFourTime) {
                String itemTrueTimeTemp = itemTrueTime;
                if(itemTrueTime.substring(itemTrueTime.indexOf(":")+1).length()<2){
                    itemTrueTimeTemp = itemTrueTime.substring(0,itemTrueTime.indexOf(":")+1)+"0"+itemTrueTime.substring(itemTrueTime.indexOf(":")+1);
                }
                timeTextView.setText(itemTrueTimeTemp);
            }
            else {
                timeTextView.setText(itemTime);
            }
            TextView timeTitle = v.findViewById(R.id.nameTitle3);
            final String completedItemDate = itemDate;
            dateTextView.setText(itemDate);
            dailyReminderTextView.setText(itemDailyReminder);
            if(sharedPref.getBoolean("onOrOffAppTheme",false)) {
                if (tempString.contains("%^&*")) {
                    timeTitle.setText("Location To Remind At");
                    secondIcon.setBackgroundResource(R.drawable.ic_round_add_location_24_lightgrey);
                } else {
                    secondIcon.setBackgroundResource(R.drawable.ic_round_access_time_24_lightgrey);
                }
            }
            else{
                if (tempString.contains("%^&*")) {
                    timeTitle.setText("Location To Remind At");
                    secondIcon.setBackgroundResource(R.drawable.ic_round_add_location_24);
                } else {
                    secondIcon.setBackgroundResource(R.drawable.ic_round_access_time_24);
                }
            }
            if (itemDailyReminder.equals("No")){
                thirdIcon.setImageResource(R.drawable.ic_round_alarm_off_24);
                categoryTitle.setText("Date To Remind At");
            }
            int colorInt = 0;
            int colorDrawable;
            if (itemColor != null){
                if (itemColor.equals("Dark Red")){
                    colorInt = getResources().getColor(R.color.darkRed);
                }
                else if (itemColor.equals("Red")){
                    colorInt = getResources().getColor(R.color.lightRed);
                }
                else if (itemColor.equals("Dark Orange")){
                    colorInt = getResources().getColor(R.color.darkOrange);
                }
                else if (itemColor.equals("Orange")){
                    colorInt = getResources().getColor(R.color.lightOrange);
                }
                else if (itemColor.equals("Yellow")){
                    colorInt = getResources().getColor(R.color.lightYellow);
                }
                else if (itemColor.equals("Dark Green")){
                    colorInt = getResources().getColor(R.color.darkGreen);
                }
                else if (itemColor.equals("Green")){
                    colorInt = getResources().getColor(R.color.lightGreen);
                }
                else if (itemColor.equals("Dark Blue")){
                    colorInt = getResources().getColor(R.color.darkBlue);
                }
                else if (itemColor.equals("Blue")){
                    colorInt = getResources().getColor(R.color.lightBlue);
                }
                else if (itemColor.equals("Dark Purple")){
                    colorInt = getResources().getColor(R.color.darkPurple);
                }
                else if (itemColor.equals("Purple")){
                    colorInt = getResources().getColor(R.color.lightPurple);
                }
                else if (itemColor.equals("Dark Pink")){
                    colorInt = getResources().getColor(R.color.darkPink);
                }
                else if (itemColor.equals("Pink")){
                    colorInt = getResources().getColor(R.color.lightPink);
                }
                else{
                    colorInt = getResources().getColor(R.color.colorGrey);
                }
            }
            else {
                colorInt = getResources().getColor(R.color.colorGrey);
            }
            TextView name2 = v.findViewById(R.id.nameTitle2);
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
            timeTitle.setTextColor(colorInt);
            name4.setTextColor(colorInt);
            name5.setTextColor(colorInt);
            name6.setTextColor(colorInt);
            shareFab.setBackgroundTintList(ColorStateList.valueOf(colorInt));
            shareFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = itemCategory+": "+itemTitle+"\n"+completedItemDate+" @ "+timeTextView.getText().toString()+"\nNotes: "+itemNote;
                    String shareTitle = itemCategory+": "+itemTitle;
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareTitle);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share your item!"));
                }
            });
            backgroundColor.setBackgroundColor(colorInt);
            boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
            if (darkTheme){
                View viewBack = v.findViewById(R.id.backgroundTodoItemViewer);
                int theme = R.color.colorLightGrey;
                int theme2 = R.color.white;
                viewBack.setBackgroundColor(getContext().getResources().getColor(R.color.black));
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
//        sendTheWallpaper();
        try {
            sort();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        sendTheWallpaper();
    }

//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        sendTheWallpaper();
//    }

    public void sendTheWallpaper(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 40);
        c.set(Calendar.SECOND, 0);
        Calendar rightNow = Calendar.getInstance();
        if (c.before(rightNow)){
            c.add(Calendar.DATE, 1);
        }
        Intent intent  = new Intent(getContext(), BootReceiver.class);
        intent.putExtra("idk", "k");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        assert alarmManager != null;
        if (sharedPref.getBoolean("changeWallOnOrOff", false)){
            Toast.makeText(getContext(), "Did it", Toast.LENGTH_SHORT).show();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),24*60*60*1000,pendingIntent);
//            ChangerSender changerSender = new ChangerSender(getActivity().getBaseContext());
//            changerSender.sendIt();
        }
        else{
            alarmManager.cancel(pendingIntent);
//            ChangerSender changerSender = new ChangerSender(getActivity().getBaseContext());
//            changerSender.removeIt();
        }
    }
}

/*
NewDay helps you organize your day with a beautiful flat design application that lets you create categories for various parts of your day.
Categories can have colors assigned to them that allow for easy visualization of the tasks you need to complete or reminders you have set.
The items are then organized by dates, times, categories, and then alphabetical order.
The built in calendar allows you to check out all your items day by day for more organization purposes.
You can even set notifications for each item and choose for yourself whether you want to be notified every day or just once.
*/