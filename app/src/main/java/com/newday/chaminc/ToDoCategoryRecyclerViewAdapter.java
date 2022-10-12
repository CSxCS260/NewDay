package com.newday.chaminc;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;

import java.util.ArrayList;

public class ToDoCategoryRecyclerViewAdapter extends RecyclerView.Adapter<ToDoCategoryRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> categories;
    private ArrayList<String> colors;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private String colorChoice;
    private ImageView handle;

    // data is passed into the constructor
    ToDoCategoryRecyclerViewAdapter(Context context, ArrayList<String> data1, ArrayList<String> data2) {
        this.mInflater = LayoutInflater.from(context);
        this.categories = data1;
        this.colors = data2;
        this.context = context;
    }

    // inflates the rowz layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.categoriesrow, parent, false);
        mInflater.inflate(R.layout.categoriesrow, parent, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        TextView textView1 = view.findViewById(R.id.titleBoldCategory);
        TextView textView2 = view.findViewById(R.id.oneLetterCategory);
        ImageView imageView = view.findViewById(R.id.handle);
        if (darkTheme) {
            view.setBackgroundColor(context.getResources().getColor(R.color.black));
            textView1.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
            textView2.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
            imageView.setColorFilter(context.getResources().getColor(R.color.colorLightGrey));
        }
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each rowz
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String theTitle = categories.get(position);
        String theColor = colors.get(position);
        holder.titleView.setText(theTitle);
        categories.remove(position);
        categories.add(position,theTitle);
        if (theColor != null){
            if (theColor.equals("Dark Red")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkredcircle);
            }
            else if (theColor.equals("Red")){
                holder.myTextView4.setBackgroundResource(R.drawable.aredcircle);
            }
            else if (theColor.equals("Dark Orange")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkorangecircle);
            }
            else if (theColor.equals("Orange")){
                holder.myTextView4.setBackgroundResource(R.drawable.aorangecircle);
            }
            else if (theColor.equals("Yellow")){
                holder.myTextView4.setBackgroundResource(R.drawable.ayellowcircle);
            }
            else if (theColor.equals("Dark Green")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkgreencircle);
            }
            else if (theColor.equals("Green")){
                holder.myTextView4.setBackgroundResource(R.drawable.agreencircle);
            }
            else if (theColor.equals("Dark Blue")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkbluecircle);
            }
            else if (theColor.equals("Blue")){
                holder.myTextView4.setBackgroundResource(R.drawable.abluecircle);
            }
            else if (theColor.equals("Dark Purple")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkpurplecircle);
            }
            else if (theColor.equals("Purple")){
                holder.myTextView4.setBackgroundResource(R.drawable.apurplecircle);
            }
            else if (theColor.equals("Dark Pink")){
                holder.myTextView4.setBackgroundResource(R.drawable.adarkpinkcircle);
            }
            else if (theColor.equals("Pink")){
                holder.myTextView4.setBackgroundResource(R.drawable.apinkcircle);
            }
        }
        else {
            holder.myTextView4.setBackgroundResource(R.drawable.ic_baseline_brightness_1_24);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Context getContext() {
        return context;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView;
        TextView myTextView4;
        ImageView handle;

        ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleBoldCategory);
            myTextView4 = itemView.findViewById(R.id.oneLetterCategory);
            handle = itemView.findViewById(R.id.handle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return categories.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}