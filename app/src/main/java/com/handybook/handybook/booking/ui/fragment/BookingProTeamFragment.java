package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeam;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingProTeamFragment extends BookingFlowFragment
{

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private ProTeam mProTeam;

    public static BookingProTeamFragment newInstance()
    {
        final BookingProTeamFragment fragment = new BookingProTeamFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mProTeam = bookingManager.getCurrentProTeam();
        if (mProTeam == null || mProTeam.isEmpty())
        {
            continueBookingFlow();
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.title_activity_pro_team));
        return view;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @OnClick(R.id.next_button)
    void onNextClicked()
    {
        showToast("next clicked");
        continueBookingFlow();
    }

}

