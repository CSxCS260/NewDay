package com.newday.chaminc;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;

import java.util.ArrayList;

public class ToDoMyRecyclerViewAdapter extends RecyclerView.Adapter<ToDoMyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> titles;
    private ArrayList<String> descriptions;
    private ArrayList<String> categories;
    private ArrayList<String> colors;
    private ArrayList<String> times;
    private ArrayList<String> trueTimes;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private ItemClickListener mItemClickListener;

    // data is passed into the constructor
    ToDoMyRecyclerViewAdapter(ItemClickListener itemClickListener, Context context, ArrayList<String> data1, ArrayList<String> data2, ArrayList<String> data3, ArrayList<String> data4, ArrayList<String> data5, ArrayList<String> data6) {
        this.mInflater = LayoutInflater.from(context);
        this.titles = data1;
        this.descriptions = data2;
        this.categories = data3;
        this.colors = data4;
        this.times = data5;
        this.trueTimes = data6;
        this.context = context;
        this.mItemClickListener = itemClickListener;
    }

    // inflates the rowz layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean switchPref = sharedPref.getBoolean("onOrOffSize", false);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        final View view;
        final TextView textView1;
        if (switchPref) {
            view = mInflater.inflate(R.layout.row2, parent, false);
            textView1 = view.findViewById(R.id.titleBold);
            TextView textView2 = view.findViewById(R.id.descriptionOfDate);
            TextView textView3 = view.findViewById(R.id.category);
            View topView = view.findViewById(R.id.topView);
            View bottomView = view.findViewById(R.id.bottomView);
            if (darkTheme){
                view.setBackgroundColor(context.getResources().getColor(R.color.black));
                textView1.setTextColor(context.getColor(R.color.white));
                textView2.setTextColor(context.getColor(R.color.colorLightGrey));
                textView3.setTextColor(context.getColor(R.color.colorLightGrey));
                topView.setBackgroundColor(context.getColor(R.color.colorDarkGrey));
                bottomView.setBackgroundColor(context.getColor(R.color.colorDarkGrey));
            }
        }
        else {
            view = mInflater.inflate(R.layout.row, parent, false);
            textView1 = view.findViewById(R.id.titleBold);
            TextView textView2 = view.findViewById(R.id.descriptionOfDate);
            TextView textView3 = view.findViewById(R.id.category);
            View topView = view.findViewById(R.id.topView);
            View bottomView = view.findViewById(R.id.bottomView);

            if (darkTheme) {
                view.setBackgroundColor(context.getResources().getColor(R.color.black));
                textView1.setTextColor(context.getColor(R.color.white));
                textView2.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                textView3.setTextColor(context.getResources().getColor(R.color.colorLightGrey));
                topView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
                bottomView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            }
        }
        return new ViewHolder(view,mItemClickListener);
    }
    public interface OnItemClicked {
        void onItemClick(int position);
    }

    // binds the data to the TextView in each rowz
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String theTitle = titles.get(position);
        String tempDescription = descriptions.get(position);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean euroDate = sharedPref.getBoolean("onOrOff",false);
        if(euroDate){
            String temp = tempDescription;
            int firstSlash = temp.indexOf("/");
            int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
            String month = temp.substring(0,firstSlash);
            String day = temp.substring(firstSlash+1,secondSlash);
            String year = temp.substring(secondSlash+1);
            tempDescription = day+"/"+month+"/"+year;
        }
        String theDescription;
        String postTime;
        if(!times.get(position).substring(0,2).equals("%^&")) {
            boolean twentyFourTime = sharedPref.getBoolean("onOrOffTime", false);
            if (twentyFourTime) {
                String tempTrue = trueTimes.get(position);
                if (tempTrue.substring(tempTrue.indexOf(":") + 1).length() < 2) {
                    tempTrue = tempTrue.substring(0, tempTrue.indexOf(":") + 1) + 0 + tempTrue.substring(tempTrue.indexOf(":") + 1);
                }
                postTime = tempTrue;
            } else {
                postTime = times.get(position);
            }
        }
        else{
            postTime = times.get(position).substring(3);
        }
        theDescription = tempDescription + " @ " + postTime;

        int index = theDescription.indexOf("%^&*");
        if (index>-1){
            theDescription = theDescription.substring(0,index);
        }
        String theCategory = categories.get(position);
        String theColor = colors.get(position);
        holder.myTextView.setText(theTitle);
        holder.myTextView2.setText(theDescription);
        holder.myTextView3.setText(theCategory);
        holder.myTextView4.setText(theTitle.substring(0,1));
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
            else {
                holder.myTextView4.setBackgroundResource(R.drawable.ic_baseline_brightness_1_24);
            }
        }
        else {
            holder.myTextView4.setBackgroundResource(R.drawable.ic_baseline_brightness_1_24);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return titles.size();
    }

    public Context getContext() {
        return context;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;
        TextView myTextView3;
        TextView myTextView4;

        ItemClickListener itemClickListener;

        ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.titleBold);
            myTextView2 = itemView.findViewById(R.id.descriptionOfDate);
            myTextView3 = itemView.findViewById(R.id.category);
            myTextView4 = itemView.findViewById(R.id.oneLetter);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) mItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return titles.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}