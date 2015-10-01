package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.widget.BookingDetailSectionPaymentView;

import butterknife.Bind;

public class BookingDetailSectionFragmentPayment extends BookingDetailSectionFragment
{
    public static final String TAG= "BookingDetailSectionFragmentPayment";

    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionPaymentView view;

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.payment;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.email_receipt;
    }

    @Override
    protected boolean hasEnabledAction()
    {
        return false;
    }

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_payment;
    }

    @Override
    protected void updateDisplay(Booking booking, User user)
    {
        //This one is worth having a different view for
        super.updateDisplay(booking, user);
        view.updatePaymentDisplay(booking, user, view);
    }

    @Override
    protected void setupClickListeners(Booking booking)
    {
        //TODO: Probably some additional constraints on this for certain edit actions
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }


    @Override
    protected void onActionClick()
    {
        //TODO: request an emailed receipt
    }
}
