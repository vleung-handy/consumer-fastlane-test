package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptiableRecyclerView extends RecyclerView
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

    public EmptiableRecyclerView(final Context context)
    {
        super(context);
    }

    public EmptiableRecyclerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }


    public EmptiableRecyclerView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty()
    {
        if (mEmptyView != null && getAdapter() != null)
        {
            final boolean emptyViewVisible = 0 == getAdapter().getItemCount();
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
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
