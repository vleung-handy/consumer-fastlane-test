package com.handybook.handybook.library.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handybook.handybook.R;

public abstract class ProgressSpinnerFragment extends InjectedFragment {

    private ProgressBar mProgressSpinner;
    private View mOverlay;

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_progress_spinner, container, false);
        mProgressSpinner = (ProgressBar) view.findViewById(R.id.progress_spinner);
        mOverlay = view.findViewById(R.id.progress_overlay);
        return view;
    }

    protected void showBlockingProgressSpinner() {
        mOverlay.bringToFront();
        mOverlay.setVisibility(View.VISIBLE);
        mProgressSpinner.bringToFront();
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    protected void showProgressSpinner() {
        mProgressSpinner.bringToFront();
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    protected void hideProgressSpinner() {
        mProgressSpinner.setVisibility(View.GONE);
        mOverlay.setVisibility(View.GONE);
    }
}
