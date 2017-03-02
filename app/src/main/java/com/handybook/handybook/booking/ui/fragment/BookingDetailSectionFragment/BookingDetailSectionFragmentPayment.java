package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentPayment
        extends BookingDetailSectionFragment<BookingDetailSectionPaymentView> {

    @Override
    protected int getEntryTitleTextResourceId(Booking booking) {
        return R.string.payment;
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_booking_detail_section_payment;
    }

    @Override
    public void updateDisplay(Booking booking, User user) {
        //This one is worth having a different view for
        super.updateDisplay(booking, user);
        getSectionView().updatePaymentDisplay(booking, user);
    }
}
