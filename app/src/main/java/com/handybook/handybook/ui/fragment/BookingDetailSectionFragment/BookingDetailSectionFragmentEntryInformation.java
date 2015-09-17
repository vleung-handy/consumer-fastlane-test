package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditEntryInformationActivity;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    @Override
    protected int getEntryTitleTextResourceId()
    {
        return R.string.entry_info;
    }

    @Override
    protected int getEntryActionTextResourceId()
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction()
    {
        return true;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            view.entryText.setText(entryInfo + " " + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditEntryInformationActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
    }
}
