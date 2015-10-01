package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditNoteToProActivity;

public class BookingDetailSectionFragmentNoteToPro extends BookingDetailSectionFragment
{

    public static final String TAG= "BookingDetailSectionFragmentNoteToPro";

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
    protected boolean hasEnabledAction()
    {
        return true;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final String proNote = booking.getProNote();
        if (proNote != null)
        {
            view.entryText.setVisibility(View.VISIBLE);
            view.entryText.setText(proNote);
        }
        else
        {
            view.entryText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditNoteToProActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
    }
}
