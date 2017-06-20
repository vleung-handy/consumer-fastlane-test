package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.User;

import javax.inject.Inject;

public class BookingDetailSectionFragmentPayment
        extends BookingDetailSectionFragment<BookingDetailSectionPaymentView> {

    @Inject
    ConfigurationManager mConfigurationManager;

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
        String priceSubText = null;
        if (mConfigurationManager.getPersistentConfiguration().isUkVatIndicatorEnabled()) {
            priceSubText = getString(R.string.value_added_tax_included_indicator);
        }
        getSectionView().updatePaymentDisplay(booking, user, priceSubText);
    }
}
