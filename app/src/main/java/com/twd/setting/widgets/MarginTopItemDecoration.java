package com.twd.setting.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;

public class MarginTopItemDecoration
        extends RecyclerView.ItemDecoration {
    private int topMargin;

    public MarginTopItemDecoration(int paramInt) {
        topMargin = paramInt;
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State paramState) {
        int i;
        if (recyclerView.getChildAdapterPosition(view) == 0) {
            i = 0;
        } else {
            i = topMargin;
        }
        rect.top = i;
    }
}
