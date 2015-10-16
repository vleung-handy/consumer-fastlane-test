package com.handybook.handybook.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView
{
    private View mEmptyView;

    private final AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(final Context context)
    {
        super(context);
    }

    public EmptyRecyclerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }


    public EmptyRecyclerView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty()
    {
        if (mEmptyView != null && getAdapter() != null)
        {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
        {
            oldAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null)
        {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }

        checkIfEmpty();
    }

    public void setEmptyView(View emptyView)
    {
        mEmptyView = emptyView;
        checkIfEmpty();
    }
}
