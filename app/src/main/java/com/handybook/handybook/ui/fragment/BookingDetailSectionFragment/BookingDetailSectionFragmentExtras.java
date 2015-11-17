package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.ui.widget.BookingDetailSectionExtrasView;

import butterknife.Bind;

public class BookingDetailSectionFragmentExtras extends BookingDetailSectionFragment
{
    public static final String TAG= "BookingDetailSectionExtras";

    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionExtrasView view;

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_extras;
    }

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.extras;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return booking.canEditExtras();
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        view.updateExtrasDisplay(booking);
    }

    @Override
    protected void setupClickListeners(Booking booking)
    {
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditExtrasActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
    }
}
