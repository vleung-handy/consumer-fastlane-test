package com.handybook.handybook.vegas.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;

public class PreGameFragment extends Fragment {

    public PreGameFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_pre_game, container, false);
    }

}
