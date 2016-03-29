package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;

/**
 * Created by jtse on 3/29/16.
 */
public class RateImprovementDialogFragment extends BaseDialogFragment
{

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_rate_improvement, container, true);
        return view;
    }
}
