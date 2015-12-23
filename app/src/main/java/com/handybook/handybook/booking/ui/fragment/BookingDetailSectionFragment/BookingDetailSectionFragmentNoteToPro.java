package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditNoteToProActivity;

public class BookingDetailSectionFragmentNoteToPro extends BookingDetailSectionFragment
{
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.pro_note;
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
        final String proNote = booking.getProNote();
        if (proNote != null)
        {
            getSectionView().getEntryText().setVisibility(View.VISIBLE);
            getSectionView().getEntryText().setText(proNote);
        }
        else
        {
            getSectionView().getEntryText().setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditNoteToProActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
