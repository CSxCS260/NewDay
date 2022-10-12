package com.newday.chaminc;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.todolist.mynewday.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatingLoc extends AppCompatActivity implements OnMapReadyCallback {
    private FloatingActionButton doneFab;
    private EditText enterItem;
    private TextView dateChooser;
    private String dateChoice;
    private TextView categoryChooser;
    private String categoryChoice;
    private String theColor;
    private TextView timeChooser;
    private EditText notes;
    private String title;
    private String time;
    private String amPmTime;
    private String sendToNotiTitle;
    private String sendToNotiDescription;
    private String sendToNotiTime;
    private boolean sendToNotiRepeating;
    private LatLng actualLocation;
    private Switch dailyReminder;
    private TextView dailyChooserText;
    private TextView categoryTitle;
    private TextView locationToRemindTitle;
    private TextView locationToRemindText;
    private String oneNote;
    private String theSwitch;
    private String trueTime;
    private ArrayList<String> allCategories = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    final Calendar cal = Calendar.getInstance();
    SharedPreferences sharedPref;
    String zipcode;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    boolean twentyFourOrNah;
    Location currentLocation;
    String addressRN;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
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

        setTitle("New Place Item");
        final Intent theIntent = getIntent();

        setContentView(R.layout.activity_creating_todo);
        enterItem = findViewById(R.id.editText);
        dateChooser = findViewById(R.id.textView);
        categoryChooser = findViewById(R.id.textView3);
        notes = findViewById(R.id.textView4);
        dailyReminder = findViewById(R.id.switch1);
        dailyChooserText = findViewById(R.id.textView5);
        categoryTitle = findViewById(R.id.nameTitle2);
        locationToRemindTitle = findViewById(R.id.nameTitle3);
        locationToRemindText = findViewById(R.id.textView2);
        locationToRemindTitle.setText("Location to remind at");
        locationToRemindText.setText("Loading...");

        if (theIntent.getStringExtra("time") != null) {
            String passed = theIntent.getStringExtra("time");
            int whenDoesItEnd = passed.indexOf("%^&*");
            addressRN = passed.substring(0, whenDoesItEnd);
            int comma = passed.substring(whenDoesItEnd + 14).indexOf(",");
            double lat = Double.parseDouble(passed.substring(whenDoesItEnd + 14,
                    whenDoesItEnd + 14 + comma));
            double lon = Double.parseDouble(passed.substring(whenDoesItEnd + 14 + comma + 1, passed.length() - 1));
            actualLocation = new LatLng(lat, lon);
            Geocoder geocoder = new Geocoder(CreatingLoc.this, Locale.getDefault());
            List<Address> addresses;


            try {
                addresses = geocoder.getFromLocation(actualLocation.latitude, actualLocation.longitude, 1);
                zipcode = addresses.get(0).getPostalCode();
                locationToRemindText = findViewById(R.id.textView2);
                locationToRemindText.setText(addressRN);

                title = theIntent.getStringExtra("title");
                if (title.equals("Empty")) {
                    title = "";
                }
                setTitle("Editing \""+title+"\"");
                dateChoice = theIntent.getStringExtra("description");
                categoryChoice = theIntent.getStringExtra("scategory");
                theColor = theIntent.getStringExtra("scolor");
                oneNote = theIntent.getStringExtra("notes");
                theSwitch = theIntent.getStringExtra("dailyReminder");
                allCategories = theIntent.getStringArrayListExtra("category");
                allCategories.add(0, "None");
                colors = theIntent.getStringArrayListExtra("colors");
                colors.add(0, "None");
                time = theIntent.getStringExtra("time");
                trueTime = theIntent.getStringExtra("trueTime");
                dateChooser.setText(dateChoice);
                notes.setText(oneNote);
                enterItem.requestFocus();
                enterItem.setText(title);
                dailyChooserText.setText(theSwitch);
                categoryChooser.setText(categoryChoice);


                if (theSwitch.equals("Yes")) {
                    dailyReminder.setChecked(true);
                    categoryTitle.setText("Date To Remind Until");
                } else {
                    dailyReminder.setChecked(false);
                    categoryTitle.setText("Date To Remind At");
                }

                if (theColor.equals("Dark Red")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkredcircle, 0);
                } else if (theColor.equals("Red")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.aredcircle, 0);
                } else if (theColor.equals("Dark Orange")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkorangecircle, 0);
                } else if (theColor.equals("Orange")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.aorangecircle, 0);
                } else if (theColor.equals("Yellow")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ayellowcircle, 0);
                } else if (theColor.equals("Dark Green")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkgreencircle, 0);
                } else if (theColor.equals("Green")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.agreencircle, 0);
                } else if (theColor.equals("Dark Blue")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkbluecircle, 0);
                } else if (theColor.equals("Blue")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.abluecircle, 0);
                } else if (theColor.equals("Dark Purple")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkpurplecircle, 0);
                } else if (theColor.equals("Purple")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.apurplecircle, 0);
                } else if (theColor.equals("Dark Pink")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.adarkpinkcircle, 0);
                } else if (theColor.equals("Pink")) {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.apinkcircle, 0);
                } else {
                    categoryChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_brightness_1_24, 0);
                }
                categoryChooser.setText(categoryChoice);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            enterItem.requestFocus();
            dailyChooserText.setText("No");
            categoryChooser.setText("None");
        }
        allCategories= theIntent.getStringArrayListExtra("category");
        allCategories.add(0,"None");
        colors = theIntent.getStringArrayListExtra("colors");
        colors.add(0,"None");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        boolean euroDate = sharedPref.getBoolean("onOrOff",false);
        dateChoice = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR);
        String temp = dateChoice;
        locationToRemindText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreatingLoc.this, SeeTheMap.class);
                intent.putExtra("addressRN", addressRN);
                intent.putExtra("zipcode",zipcode);
                Bundle args = new Bundle();args.putParcelable("location", actualLocation);
                intent.putExtra("bundle", args);
                startActivityForResult(intent,1);
            }
        });
        if(euroDate){
            int firstSlash = temp.indexOf("/");
            int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
            String month2 = temp.substring(0,firstSlash);
            String day = temp.substring(firstSlash+1,secondSlash);
            String year2 = temp.substring(secondSlash+1);
            temp = day+"/"+month2+"/"+year2;
        }
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        final int theme;
        if(darkTheme){
            theme = android.R.style.Theme_DeviceDefault_Dialog;
        }
        else{
            theme = android.R.style.ThemeOverlay_Material_Dialog;
        }
        if (theIntent.getStringExtra("time") == null) {
            dateChooser.setText(temp);
        }
        else{
            dateChoice = theIntent.getStringExtra("description");
        }
        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int firstSlash = dateChoice.indexOf("/");
                int secondSlash = dateChoice.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                int year = Integer.parseInt(dateChoice.substring(secondSlash+1));
                int month = Integer.parseInt(dateChoice.substring(0,firstSlash))-1;
                int day = Integer.parseInt(dateChoice.substring(firstSlash+1,secondSlash));

                DatePickerDialog dialog = new DatePickerDialog(
                        CreatingLoc.this,
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

        categoryChooser = findViewById(R.id.textView3);
        categoryChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(CreatingLoc.this, v);
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
            dateChooser.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            locationToRemindText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            categoryChooser.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            notes.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            dailyChooserText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            enterItem.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            dateChooser.setTextColor(getColor(R.color.white));
            locationToRemindText.setTextColor(getColor(R.color.white));
            categoryChooser.setTextColor(getColor(R.color.white));
            notes.setTextColor(getColor(R.color.white));
            dailyChooserText.setTextColor(getColor(R.color.white));
            enterItem.setTextColor(getColor(R.color.white));
            enterItem.setHintTextColor(getColor(R.color.colorGrey));
            notes.setHintTextColor(getColor(R.color.colorGrey));
//            timeChooser.setTextColor(getColor(R.color.white));
//            dailyChooserText.setTextColor(getColor(R.color.white));
//            categoryChooser.setTextColor(getColor(R.color.white));
//            categoryTitle.setTextColor(getColor(R.color.white));
        }
        doneFab = findViewById(R.id.doneFAB);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date date = new SimpleDateFormat("MM/dd/yyyy").parse(dateChoice);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (locationToRemindText.getText().toString().equals("Loading...")|| locationToRemindText.getText().toString().length()<1) {
                    Toast.makeText(CreatingLoc.this, "Whoops, you need to get location permission and try again", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(CreatingLoc.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {
                    Bundle bundle = new Bundle();
                    if (enterItem.getText().toString().equals("")) {
                        bundle.putString("item", "Empty");
                        sendToNotiTitle = "Empty";
                    } else {
                        bundle.putString("item", enterItem.getText().toString());
                        sendToNotiTitle = enterItem.getText().toString();
                    }
                    if (categoryChooser.getText().equals("None")||categoryChooser.getText().equals("none")) {
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

//                    bundle.putString("dailyReminder", dailyChooserText.getText().toString());
//                    bundle.putString("trueTime", time);
//                    bundle.putString("chosenTime", amPmTime);
//                    sendToNotiTime = time;
//                    bundle.putString("chosenDate", dateChoice);
//                    sendToNotiDescription = dateChoice;
//                    bundle.putString("positionForPendingIntent", Integer.toString(getIntent().getIntExtra("positionForPendingIntent", -1)));
//                    ToDoFragment frag = new ToDoFragment();
                    bundle.putString("trueTime", "0:0");
                    bundle.putString("chosenTime", addressRN+"%^&*"+actualLocation.toString());
                    bundle.putString("chosenDate", dateChoice);
                    sendToNotiDescription = dateChoice;
                    if(theIntent.getIntExtra("placement",-1)!= -1) {
                        bundle.putString("placement", Integer.toString(theIntent.getIntExtra("placement", -1)));
                    }
                    bundle.putString("positionForPendingIntent", Integer.toString(theIntent.getIntExtra("positionForPendingIntent", -1)));
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
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CreatingLoc.this);
        if (theIntent.getStringExtra("time") == null) {
            fetchLastLocation();
        }
    }

    private void fetchLastLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            Toast.makeText(this, "Line 354", Toast.LENGTH_SHORT).show();
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Geocoder geocoder = new Geocoder(CreatingLoc.this,Locale.getDefault());
                    Address address = null;
                    try {
                        List<Address> list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                        address = list.get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addressRN = address.getAddressLine(0);
                    actualLocation = new LatLng(address.getLatitude(),address.getLongitude());
                    zipcode = address.getPostalCode();
                    locationToRemindText.setText(addressRN);
//                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//                    supportMapFragment.getMapAsync(CreatingLoc.this);
                }
//                Toast.makeText(CreatingLoc.this, "Line 367", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 1) && (resultCode == Activity.RESULT_OK)){
            addressRN = data.getStringExtra("theAddress");
            zipcode = data.getStringExtra("zipcode");
            locationToRemindText.setText(addressRN);
            Bundle temp = data.getParcelableExtra("bundle");
            actualLocation = temp.getParcelable("location");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
