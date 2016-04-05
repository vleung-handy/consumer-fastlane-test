package com.handybook.handybook.booking.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.BaseDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jtse on 3/21/16.
 */
public class RateServiceApologyDialogFragment extends BaseDialogFragment
{
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {

        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.frag_rate_service_apology, container, true);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_ok)
    public void ok()
    {
        dismiss();
    }
}
