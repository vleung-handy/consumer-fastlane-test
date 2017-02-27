package com.handybook.handybook.core;

import com.handybook.handybook.account.model.RecurringPlanWrapper;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitEndpoint;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.testutil.ModelFactory;

public class TestDataManager extends DataManager {

    public TestDataManager(
            final HandyRetrofitService service,
            final HandyRetrofitEndpoint endpoint
    ) {
        super(service, endpoint);
    }

    @Override
    public void validateBookingZip(
            final int serviceId, final String zipCode, final String userId,
            final String promoCode, final Callback<ZipValidationResponse> cb
    ) {
        cb.onSuccess(new ZipValidationResponse());
    }

    @Override
    public void getRecurringBookings(final Callback<RecurringBookingsResponse> cb) {
        cb.onSuccess(ModelFactory.createRecurringBookingsResponse());
    }

    @Override
    public void getRecurringFrequency(
            final String recurringId,
            final Callback<BookingEditFrequencyInfoResponse> cb
    ) {
        cb.onSuccess(ModelFactory.createBookingEditFrequencyInfoResponse(1));
    }

    @Override
    public void updateRecurringFrequency(
            final String recurringId,
            final BookingEditFrequencyRequest bookingEditFrequencyRequest,
            final Callback<Void> cb
    ) {
        cb.onSuccess(null);
    }

    @Override
    public void editBookingPlanAddress(
            final int planId,
            final EditAddressRequest editAddressRequest,
            final Callback<RecurringPlanWrapper> cb
    ) {
        Booking.Address address = ModelFactory.createAddress(editAddressRequest);
        RecurringPlanWrapper wrapper =
                new RecurringPlanWrapper(ModelFactory.createRecurringPlan(address));
        cb.onSuccess(wrapper);
    }

    @Override
    public void updateQuote(
            final int quoteId,
            final BookingTransaction bookingTransaction,
            final Callback<BookingQuote> cb
    ) {
        cb.onError(new DataManagerError(Type.NETWORK, "Got 500"));
    }
}
