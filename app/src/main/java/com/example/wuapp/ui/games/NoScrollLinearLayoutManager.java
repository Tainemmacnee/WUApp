package com.example.wuapp.ui.games;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class NoScrollLinearLayoutManager extends LinearLayoutManager {
    public NoScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    // it will always pass false to RecyclerView when calling "canScrollVertically()" method.
    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
