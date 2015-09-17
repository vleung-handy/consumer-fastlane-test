package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.activity.BookingEditNoteToProActivity;

public class BookingDetailSectionFragmentNoteToPro extends BookingDetailSectionFragment
{

    @Override
    protected int getEntryTitleTextResourceId()
    {
        return R.string.pro_note;
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
        final String proNote = booking.getProNote();
        if (proNote != null)
        {
            view.entryText.setText(proNote);
        }
        else
        {
            view.entryText.setText("");
        }
    }

    @Override
    protected void onActionClick()
    {
        //TODO: Edit the note to pro

        //BookingNoteToProActivity

        System.out.println("zzzz starting note to pro activity for result booking updated");


        final Intent intent = new Intent(getActivity(), BookingEditNoteToProActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);




        //startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);


        getParentFragment().startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);

    }



    @Override
    public final void onActivityResult(final int requestCode,
                                       final int resultCode,
                                       final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("ZZZZ fragment note to pro hears activity result : " + requestCode + " : "+  resultCode + " : " + data);

        if (resultCode == ActivityResult.RESULT_BOOKING_UPDATED)
        {
            System.out.println("ZZZZ hears updated, notify parent fragment or just directly post to bus?");

            //passing along to parent fragment? Introduces a dependency on their being a parent fragment

            if(getParentFragment() != null) //is null if our parent is actually an activity and we are not a nested fragment
            {
                System.out.println("ZZZZ padding it along to parent frag");
                getParentFragment().onActivityResult(requestCode, resultCode, data);
            }




//            disableInputs();
//            progressDialog.show();
//            bus.post(new HandyEvent.RequestBookingDetails(booking.getId()));

        }
    }


    }
