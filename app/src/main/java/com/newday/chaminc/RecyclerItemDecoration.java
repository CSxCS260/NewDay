package com.newday.chaminc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.todolist.mynewday.R;

public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
    Context context;
    int headerOffset;
    boolean sticky;
    SectionCallback sectionCallback;
    View headerView;
    TextView tvTitle;

    public RecyclerItemDecoration (Context context, int headerHeight, boolean isSticky, SectionCallback callback){
        this.context = context;
        this.headerOffset = headerHeight;
        this.sticky = isSticky;
        this.sectionCallback = callback;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if(sectionCallback.isSectionHeader(pos)){
            outRect.top = headerOffset;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (headerView == null) {
            headerView = inflateHeader(parent);
            tvTitle = headerView.findViewById(R.id.textViewHeader);
            fixLayoutSize(headerView, parent);
        }
        String prevTitle = "";

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int childPos = parent.getChildAdapterPosition(child);
            String title = sectionCallback.getSectionHeaderName(childPos);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean euroDate = sharedPref.getBoolean("onOrOff",false);
            if(euroDate){
                String temp = title;
                int firstSlash = temp.indexOf("/");
                int secondSlash = temp.substring(firstSlash+1).indexOf("/")+firstSlash+1;
                String month = temp.substring(0,firstSlash);
                String day = temp.substring(firstSlash+1,secondSlash);
                String year = temp.substring(secondSlash+1);
                title = day+"/"+month+"/"+year;
            }
            if (prevTitle.equalsIgnoreCase(title)) {
                tvTitle.setText(title);
                drawHeader(c, child, headerView);
                prevTitle = title;
            }
            else if (sectionCallback.isSectionHeader(childPos)) {
                tvTitle.setText(title);
                drawHeader(c, child, headerView);
            }
        }
    }



    private void drawHeader(Canvas c, View child, View headerView) {
        c.save();
        if (sticky) {
            c.translate(0, Math.max(0, child.getTop() - headerView.getHeight()));
        } else {
            c.translate(0, child.getTop() - headerView.getHeight());
        }
        headerView.draw(c);
        c.restore();
    }

    private void fixLayoutSize(View headerView, RecyclerView parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft()+parent.getPaddingRight(),headerView.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,parent.getPaddingTop()+parent.getPaddingBottom(),headerView.getLayoutParams().height);

        headerView.measure(childWidth,childHeight);
        headerView.layout(0,0,headerView.getMeasuredWidth(),headerView.getMeasuredHeight());
    }

    private View inflateHeader(RecyclerView recyclerView){
        View view = LayoutInflater.from(context).inflate(R.layout.row_section_header,recyclerView,false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean darkTheme = sharedPref.getBoolean("onOrOffAppTheme",false);
        if (darkTheme){
            view.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
        }
        return view;
    }
    public interface SectionCallback{
        public boolean isSectionHeader(int pos);
        public boolean isSectionHeader2(int pos);
        public String getSectionHeaderName(int pos);

    }
}
