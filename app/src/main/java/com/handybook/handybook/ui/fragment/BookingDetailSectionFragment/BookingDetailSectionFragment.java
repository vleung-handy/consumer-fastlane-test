package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.BookingDetailSectionView;

import butterknife.ButterKnife;
import butterknife.Bind;

public abstract class BookingDetailSectionFragment extends InjectedFragment
{
    protected Booking booking;

    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionView view;

    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section;
    }

    protected abstract int getEntryTitleTextResourceId(Booking booking);
    protected abstract int getEntryActionTextResourceId(Booking booking);
    protected abstract boolean hasEnabledAction();

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.booking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(getFragmentResourceId(), container, false);

        ButterKnife.bind(this, view);

        updateDisplay(this.booking, userManager.getCurrentUser());

        setupClickListeners(this.booking);

        return view;
    }

    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(getEntryTitleTextResourceId(booking));
        view.entryActionText.setText(getEntryActionTextResourceId(booking));
        if(!hasEnabledAction())
        {
            view.entryActionText.setVisibility(View.GONE);
        }
    }


    protected void setupClickListeners(Booking booking)
    {
        //TODO: Probably some additional constraints on this for certain edit actions?
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        view.entryActionText.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        view.entryActionText.setClickable(true);
    }

    protected View.OnClickListener actionClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            onActionClick();
        }
    };

    protected void onActionClick()
    {

    }


}
