package com.newday.chaminc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.todolist.mynewday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ToDoCategories extends AppCompatActivity{
    private FloatingActionButton doneFab;
    private EditText enterItem;
    private TextView colorChooser;
    private String colorChoice="";
    private ArrayList<String> allCategories = new ArrayList<String>();
    private ArrayList<String> colors = new ArrayList<String>();
    private int position;
    private String specificCategory;
    private String theColor;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if(darkTheme){
            setTheme(R.style.GreenAppTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.todo_categories);
        setTitle("Manage Category");
        Intent theIntent = getIntent();
        allCategories= theIntent.getStringArrayListExtra("category");
        colors = theIntent.getStringArrayListExtra("colors");
        position = getIntent().getIntExtra("position",-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enterItem = findViewById(R.id.editText);
        enterItem.requestFocus();
        colorChooser = findViewById(R.id.textView);
        if (position != -1){
            specificCategory = allCategories.get(position);
            enterItem.setText(specificCategory);
            theColor = colors.get(position);
            if (theColor != null){
                if (theColor.equals("Dark Red")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkredcircle,0);
                    colorChooser.setText("Dark Red");
                    colorChoice="Dark Red";
                }
                else if (theColor.equals("Red")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aredcircle,0);
                    colorChooser.setText("Red");
                    colorChoice="Red";
                }
                else if (theColor.equals("Dark Orange")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkorangecircle,0);
                    colorChooser.setText("Dark Orange");
                    colorChoice="Dark Orange";
                }
                else if (theColor.equals("Orange")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aorangecircle,0);
                    colorChooser.setText("Orange");
                    colorChoice="Orange";
                }
                else if (theColor.equals("Yellow")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ayellowcircle,0);
                    colorChooser.setText("Yellow");
                    colorChoice="Yellow";
                }
                else if (theColor.equals("Dark Green")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkgreencircle,0);
                    colorChooser.setText("Dark Green");
                    colorChoice="Dark Green";
                }
                else if (theColor.equals("Green")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.agreencircle,0);
                    colorChooser.setText("Green");
                    colorChoice="Green";
                }
                else if (theColor.equals("Dark Blue")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkbluecircle,0);
                    colorChooser.setText("Dark Blue");
                    colorChoice="Dark Blue";
                }
                else if (theColor.equals("Blue")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.abluecircle,0);
                    colorChooser.setText("Blue");
                    colorChoice="Blue";
                }
                else if (theColor.equals("Dark Purple")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpurplecircle,0);
                    colorChooser.setText("Dark Purple");
                    colorChoice="Dark Purple";
                }
                else if (theColor.equals("Purple")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apurplecircle,0);
                    colorChooser.setText("Purple");
                    colorChoice="Purple";
                }
                else if (theColor.equals("Dark Pink")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpinkcircle,0);
                    colorChooser.setText("Dark Pink");
                    colorChoice="Dark Pink";
                }
                else if (theColor.equals("Pink")){
                    colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apinkcircle,0);
                    colorChooser.setText("Pink");
                    colorChoice="Pink";
                }
            }
            else {
                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_brightness_1_24,0);
                colorChooser.setText("Color");
            }
        }
        colorChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu colors = new PopupMenu(getApplicationContext(), v);
                colors.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.dred:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkredcircle,0);
                                colorChooser.setText("Dark Red");
                                colorChoice="Dark Red";
                                return true;
                            case R.id.red:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aredcircle,0);
                                colorChooser.setText("Red");
                                colorChoice="Red";
                                return true;
                            case R.id.dorange:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkorangecircle,0);
                                colorChooser.setText("Dark Orange");
                                colorChoice="Dark Orange";
                                return true;
                            case R.id.orange:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.aorangecircle,0);
                                colorChooser.setText("Orange");
                                colorChoice="Orange";
                                return true;
                            case R.id.yellow:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ayellowcircle,0);
                                colorChooser.setText("Yellow");
                                colorChoice="Yellow";
                                return true;
                            case R.id.dgreen:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkgreencircle,0);
                                colorChooser.setText("Dark Green");
                                colorChoice="Dark Green";
                                return true;
                            case R.id.green:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.agreencircle,0);
                                colorChooser.setText("Green");
                                colorChoice="Green";
                                return true;
                            case R.id.dblue:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkbluecircle,0);
                                colorChooser.setText("Dark Blue");
                                colorChoice="Dark Blue";
                                return true;
                            case R.id.blue:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.abluecircle,0);
                                colorChooser.setText("Blue");
                                colorChoice="Blue";
                                return true;
                            case R.id.dpurple:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpurplecircle,0);
                                colorChooser.setText("Dark Purple");
                                colorChoice="Dark Purple";
                                return true;
                            case R.id.purple:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apurplecircle,0);
                                colorChooser.setText("Purple");
                                colorChoice="Purple";
                                return true;
                            case R.id.dpink:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.adarkpinkcircle,0);
                                colorChooser.setText("Dark Pink");
                                colorChoice="Dark Pink";
                                return true;
                            case R.id.pink:
                                colorChooser.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.apinkcircle,0);
                                colorChooser.setText("Pink");
                                colorChoice="Pink";
                                return true;
                        }
                        return false;
                    }
                });
                colors.inflate(R.menu.colors);
                colors.show();
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if (darkTheme){
            View view = findViewById(R.id.backgroundCategories);
            view.setBackgroundColor(this.getColor(R.color.black));
            enterItem.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            colorChooser.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorDarkGrey)));
            enterItem.setHintTextColor(getColor(R.color.colorGrey));
            enterItem.setTextColor(getColor(R.color.white));
            colorChooser.setTextColor(getColor(R.color.white));
        }
        doneFab = findViewById(R.id.doneFAB);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorChoice.length()>0 && enterItem.getText().toString().length()>0 && contained()) {
//                    Bundle send = new Bundle();
                    Intent intent = new Intent(ToDoCategories.this, EditingCategories.class);
                    String theCategory = enterItem.getText().toString();
                    while(theCategory.substring(theCategory.length()-1).equals(" ")){
                        theCategory=theCategory.substring(0,theCategory.length()-1);
                    }
                    if (position !=-1){
                        intent.putExtra("position",position);
                    }
                    intent.putExtra("categoriesNew", theCategory);
                    intent.putExtra("colorsNew", colorChoice);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                else if(!contained()){
                    View root = findViewById(android.R.id.content);
                    Toast toast = Toast.makeText(ToDoCategories.this, "Whoops, you used that name already", Toast.LENGTH_SHORT);
                    int yOffset = Math.max(0, root.getHeight() - toast.getYOffset()+150);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, yOffset);
                    toast.show();
                }
                else{
                    View root = findViewById(android.R.id.content);
                    Toast toast = Toast.makeText(ToDoCategories.this, "Whoops, you may have forgotten to fill out all the fields", Toast.LENGTH_SHORT);
                    int yOffset = Math.max(0, root.getHeight() - toast.getYOffset()+150);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, yOffset);
                    toast.show();
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
    public boolean contained(){
        for (int i = 0; i < allCategories.size();i++) {
            String categoryAlready = allCategories.get(i);
            if (categoryAlready.equals(enterItem.getText().toString())&&position!=i) {
                return false;
            }
        }
        return true;
    }
}
