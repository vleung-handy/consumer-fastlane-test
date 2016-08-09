package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.text.TextUtils;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;

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
            String extraEntryInfo = booking.getExtraEntryInfo();
            String entryInfoFormatted = entryInfo + " " + (extraEntryInfo != null ? extraEntryInfo : "");
            if(!TextUtils.isEmpty(booking.getLockboxCode()))
            {
                //TODO hack, refactor later
                entryInfoFormatted+=("\nAccess code: " + booking.getLockboxCode());
            }

            getSectionView().getEntryText().setText(entryInfoFormatted);
        }
        else
        {
            getSectionView().getEntryText().setText(R.string.no_information);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditEntryInformationActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
