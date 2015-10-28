package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditEntryInformationActivity;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    public static final String TAG= "BookingDetailSectionFragmentEntryInformation";

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.entry_info;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return true;
    }

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            view.entryText.setText(entryInfo + " " + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
        }
        else
        {
            view.entryText.setText(R.string.no_information);
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
