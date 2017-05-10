package com.handybook.handybook.library.ui.view;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

public abstract class OnScrollToBottomListener extends RecyclerView.OnScrollListener {

    /**
     * note that this gets triggered when the recycler view is updated even if user didn't actually scroll
     * @param recyclerView
     * @param dx
     * @param dy
     */
    @Override
    public final void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        if(dy >= 0) //if not scrolled up
        {
            //can no longer scroll down anymore
            if(!ViewCompat.canScrollVertically(recyclerView, ViewCompat.SCROLL_AXIS_VERTICAL))
            {
                onScrollToBottom();
            }
        }
    }

    public abstract void onScrollToBottom();
}
