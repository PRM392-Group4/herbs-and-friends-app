package com.group4.herbs_and_friends_app.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridRowSpacingDecoration extends RecyclerView.ItemDecoration {
    private int spacing;
    private int spanCount;

    public GridRowSpacingDecoration(int spacing, int spanCount) {
        this.spacing = spacing;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        // Add top spacing to items that are NOT in the first row
        if (position >= spanCount) {
            outRect.top = spacing;
        }
    }
}