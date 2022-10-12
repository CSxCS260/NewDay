package com.newday.chaminc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.todolist.mynewday.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;
    BottomNavigationView bottomNav;

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
        setContentView(R.layout.activity_main);
        setNavigationViewListener();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Checklist");
//        setSupportActionBar(toolbar);
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ToDoFragment()).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home).setDrawerLayout(drawer).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        onCreateOptionsMenu(nav);
        bottomNav.setItemRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        if (darkTheme) {
            bottomNav.setBackgroundColor(getColor(R.color.colorDarkGrey));
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setBackgroundColor(getColor(R.color.colorDarkGrey));
        }

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        ArrayList<String> categoryNames = FileHelper.readData(this,16);
//        for(int i = 0; i < categoryNames.size(); i++){
//            menu.add(0, i, Menu.NONE, categoryNames.get(i));
//        }
//        // repeat this to add additional menus
//        return true;
//    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:{
                Intent myIntent = new Intent(this, SettingsActivity.class);
                this.startActivity(myIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            }
        }
        mAppBarConfiguration.getDrawerLayout().closeDrawer(GravityCompat.START);
        return true;
    }
    private void setNavigationViewListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        default:
                            if (!bottomNav.getMenu().getItem(0).isChecked()) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ToDoFragment(),"Checklist").commit();
                                setActionBarTitle("Checklist");
//                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            break;
                        case R.id.nav_checklist: {
                            if (!bottomNav.getMenu().getItem(0).isChecked()) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ToDoFragment(),"Checklist").commit();
                                setActionBarTitle("Checklist");
//                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            break;
                        }
                        case R.id.nav_wishlist: {
                            if (!bottomNav.getMenu().getItem(1).isChecked()) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new CalendarFragment(),"Calendar").commit();
                                setActionBarTitle("Calendar");
//                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            break;
                        }
                        case R.id.nav_alarm: {
                            if (!bottomNav.getMenu().getItem(2).isChecked()) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new CompletedFragment(),"Completed").commit();
                                setActionBarTitle("Completed");
//                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            break;
                        }
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }
            };

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getSupportFragmentManager().findFragmentByTag("Completed")!=null && getSupportFragmentManager().findFragmentByTag("Completed").isVisible()) {
            onNavigationItemSelected(bottomNav.getMenu().getItem(2));
            bottomNav.getMenu().getItem(2).setChecked(true);
        }
        else if (getSupportFragmentManager().findFragmentByTag("Calendar")!=null && getSupportFragmentManager().findFragmentByTag("Calendar").isVisible()) {
            onNavigationItemSelected(bottomNav.getMenu().getItem(1));
            bottomNav.getMenu().getItem(1).setChecked(true);
        }
        else{
            onNavigationItemSelected(bottomNav.getMenu().getItem(0));
            bottomNav.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportFragmentManager().findFragmentByTag("Completed")!=null && getSupportFragmentManager().findFragmentByTag("Completed").isVisible()) {
            onNavigationItemSelected(bottomNav.getMenu().getItem(2));
            bottomNav.getMenu().getItem(2).setChecked(true);
        }
        else if (getSupportFragmentManager().findFragmentByTag("Calendar")!=null && getSupportFragmentManager().findFragmentByTag("Calendar").isVisible()) {
            onNavigationItemSelected(bottomNav.getMenu().getItem(1));
            bottomNav.getMenu().getItem(1).setChecked(true);
        }
        else{
            onNavigationItemSelected(bottomNav.getMenu().getItem(0));
            bottomNav.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }



}
