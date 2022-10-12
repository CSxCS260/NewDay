package com.newday.chaminc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class EditingCategories extends AppCompatActivity{
    private ArrayList<String> categoryNames;
    private ArrayList<String> colors;
    private ArrayList<String> specificColor;
    private ArrayList<String> specificCategory;
    private String theCategory;
    private String theColor;
    private RecyclerView itemList;
    private FloatingActionButton fabDone;
    private FloatingActionButton fabAdd;
    ToDoCategoryRecyclerViewAdapter adapter;
    LinearLayoutManager mLayoutManager;
    Snackbar snackbar;
    boolean darkTheme;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if(darkTheme){
            setTheme(R.style.GreenAppTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.editcategories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent theIntent = getIntent();
        specificCategory = FileHelper.readData(EditingCategories.this, 13);
        specificColor = FileHelper.readData(EditingCategories.this, 14);
        categoryNames = FileHelper.readData(EditingCategories.this, 16);
        colors = FileHelper.readData(EditingCategories.this, 17);
        itemList = findViewById(R.id.categories);
        fabDone = findViewById(R.id.floatingActionButtonFinish);
        fabAdd = findViewById(R.id.floatingActionButtonAdd);
        mLayoutManager = new LinearLayoutManager(EditingCategories.this);
        itemList.setLayoutManager(mLayoutManager);
        adapter = new ToDoCategoryRecyclerViewAdapter(EditingCategories.this, categoryNames, colors);
        itemList.setAdapter(adapter);
        TextView empty = findViewById(R.id.emptyCategoryText);
        if(categoryNames.size()==0){
            empty.setText("Categories help with organization.\n\nTry and add some now 😃");
        }
        else{
            empty.setText("");
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        View view123 = findViewById(R.id.backgroundEditingCategories);
        if (darkTheme){
            view123.setBackgroundColor(this.getColor(R.color.black));
            empty.setTextColor(getColor(R.color.white));
        }
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                FileHelper.writeData(specificCategory, EditingCategories.this,13);
                FileHelper.writeData(specificColor,EditingCategories.this,14);
                FileHelper.writeData(categoryNames, EditingCategories.this, 16);
                FileHelper.writeData(colors, EditingCategories.this, 17);
                ToDoFragment frag = new ToDoFragment();
                frag.setArguments(bundle);
                FragmentTransaction f = getSupportFragmentManager().beginTransaction();
                f.add(frag, "hehe");
                f.detach(frag);
                f.attach(frag);
                f.commit();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditingCategories.this, ToDoCategories.class);
                intent.putExtra("category", categoryNames);
                intent.putExtra("colors", colors);
                startActivityForResult(intent, 10001);
                FileHelper.writeData(categoryNames, EditingCategories.this, 16);
                FileHelper.writeData(colors, EditingCategories.this, 17);
                adapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(categoryNames,positionDragged,positionTarget);
                Collections.swap(colors,positionDragged,positionTarget);
                adapter.notifyItemMoved(positionDragged,positionTarget);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                switch (swipeDir) {
                    case ItemTouchHelper.RIGHT:
                        Intent intent = new Intent(EditingCategories.this, ToDoCategories.class);
                        intent.putExtra("category", categoryNames);
                        intent.putExtra("colors", colors);
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 10001);
                        adapter.notifyDataSetChanged();
                        break;
                    case ItemTouchHelper.LEFT:
                        final String deletedCategory = categoryNames.get(position);
                        final String deletedColor = colors.get(position);
                        removeThat(position);
                        if (categoryNames.size()==0){
                            TextView text = findViewById(R.id.emptyCategoryText);
                            text.setText("Categories help with organization.\n\nTry and add some now 😃");
                        }
                        adapter.notifyItemRemoved(position);
                        if(darkTheme){
                            snackbar = Snackbar.make(itemList, deletedCategory + " has been removed", Snackbar.LENGTH_LONG).setActionTextColor(getColor(R.color.white)).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    readdThat(position, deletedCategory, deletedColor);
                                    adapter.notifyItemInserted(position);
                                    TextView text = findViewById(R.id.emptyCategoryText);
                                    text.setText("");
                                }
                            });
                        }
                        else {
                            snackbar = Snackbar.make(itemList, deletedCategory + " has been removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    readdThat(position, deletedCategory, deletedColor);
                                    adapter.notifyItemInserted(position);
                                    TextView text = findViewById(R.id.emptyCategoryText);
                                    text.setText("");

                                }
                            });
                        }
                        snackbar.show();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(EditingCategories.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(EditingCategories.this, R.color.darkishGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_round_edit_24)
                        .setSwipeRightActionIconTint(ContextCompat.getColor(EditingCategories.this, R.color.white))
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(EditingCategories.this, R.color.darkishRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_round_delete_24)
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(EditingCategories.this, R.color.white))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(itemList);


    }

    public void removeThat(int whichItem) {
        categoryNames.remove(whichItem);
        colors.remove(whichItem);
    }
    public void removeItToo(String categoryDeleted, String colorDeleted){
        for(int i = 0; i < specificCategory.size();i++){
            if(specificCategory.get(i).equals(categoryDeleted)){
                specificCategory.remove(i);
                specificCategory.add(i,"None");
                specificColor.remove(i);
                specificColor.add(i,"None");
            }
        }
    }

    public void readdThat(int position, String categoryUndo, String colorUndo) {
        categoryNames.add(position, categoryUndo);
        colors.add(position, colorUndo);
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            if (data.getIntExtra("position", -1) != -1) {
                int position = data.getIntExtra("position", -1);
                for(int i = 0; i < specificCategory.size();i++){
                    if(specificCategory.get(i).equals(categoryNames.get(position))){
                        specificCategory.remove(i);
                        specificCategory.add(i,data.getStringExtra("categoriesNew"));
                        specificColor.remove(i);
                        specificColor.add(i,data.getStringExtra("colorsNew"));
                    }
                }
                categoryNames.remove(position);
                colors.remove(position);
                categoryNames.add(position, data.getStringExtra("categoriesNew"));
                colors.add(position, data.getStringExtra("colorsNew"));
            } else {
                categoryNames.add(data.getStringExtra("categoriesNew"));
                colors.add(data.getStringExtra("colorsNew"));
            }

            FileHelper.writeData(specificCategory, EditingCategories.this, 13);
            FileHelper.writeData(specificColor, EditingCategories.this, 14);
            FileHelper.writeData(categoryNames, EditingCategories.this, 16);
            FileHelper.writeData(colors, EditingCategories.this, 17);
            TextView text = findViewById(R.id.emptyCategoryText);
            text.setText("");
            adapter.notifyDataSetChanged();
        }
    }
}
