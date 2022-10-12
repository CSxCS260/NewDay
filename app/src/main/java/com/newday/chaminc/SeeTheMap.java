package com.newday.chaminc;

import android.Manifest;
import android.app.Activity;
import android.app.AuthenticationRequiredException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.todolist.mynewday.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SeeTheMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FloatingActionButton centerOnMeFAB;
    private FloatingActionButton checkMarkFAB;
    private static final int REQUEST_CODE = 101;
    private EditText inputSearch;
    private SharedPreferences sharedPref;
    private String addressRN;
    private String justGotSearched;
    private LatLng location;
    private String zipcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme", false);
        //Set dark mode and contentview
        if (darkTheme) {
            setTheme(R.style.GreenAppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.see_the_map);
        if (darkTheme) {
            findViewById(R.id.searchBar).setBackgroundResource(R.drawable.rounded_corner_black);
            findViewById(R.id.magnify).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.darkestBlue)));
        };
        setTitle("Choose The Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Get the intent/bundle data
        addressRN = getIntent().getStringExtra("addressRN");
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        location = bundle.getParcelable("location");
        zipcode = getIntent().getStringExtra("zipcode");


        //Assign to variables to xml elements
        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setText(addressRN);
        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    geoLocate();
                    inputSearch.clearFocus();
                }
                return false;
            }
        });
        centerOnMeFAB = findViewById(R.id.centerTheMapFAB);
        centerOnMeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackHome();
            }
        });
        checkMarkFAB = findViewById(R.id.endItMap);
        checkMarkFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeTheMap.this, CreatingLoc.class);
                intent.putExtra("theAddress",justGotSearched);
//                intent.putExtra("location", location);
                intent.putExtra("zipcode", zipcode);
                Bundle args = new Bundle();
                args.putParcelable("location", location);
                intent.putExtra("bundle", args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    private void geoLocate() {
        String searchString = inputSearch.getText().toString()+" near "+ getIntent().getStringExtra("zipcode");
        Geocoder geocoder = new Geocoder(SeeTheMap.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            location = new LatLng(address.getLatitude(), address.getLongitude());
            zipcode = address.getPostalCode();
            justGotSearched = address.getAddressLine(0);
            MarkerOptions markerOptions = new MarkerOptions().position(location).title(justGotSearched);
            map.animateCamera(CameraUpdateFactory.newLatLng(location));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            map.clear();
            map.addMarker(markerOptions);
            map.addCircle(new CircleOptions()
                    .center(location)
                    .radius(400)
                    .strokeColor(getColor(R.color.darkestBlueT))
                    .fillColor(getColor(R.color.lightBlueT)));
        }

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(SeeTheMap.this);
                }
            }
        });
    }

    public void goBackHome (){
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        justGotSearched = addressRN;
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        justGotSearched = addressRN;
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Your Location: "+addressRN);
        geoLocate();
//        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
//        map.addMarker(markerOptions);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(point)
                        .snippet(""));
                map.addCircle(new CircleOptions()
                        .center(point)
                        .radius(400)
                        .strokeColor(getColor(R.color.darkestBlueT))
                        .fillColor(getColor(R.color.lightBlueT)));
                location = point;
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(SeeTheMap.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                    Address address = addresses.get(0);
                    justGotSearched = address.getAddressLine(0);
                    zipcode = address.getPostalCode();
                    inputSearch.setText(justGotSearched);
                    geoLocate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
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
