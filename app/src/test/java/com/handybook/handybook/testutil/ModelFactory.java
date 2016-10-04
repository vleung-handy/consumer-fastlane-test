package com.handybook.handybook.testutil;

import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;

import java.util.ArrayList;
import java.util.Date;

public class ModelFactory
{
    public static RecurringBooking createRecurringPlan()
    {
        return createRecurringPlan(createAddress());
    }

    public static RecurringBooking createRecurringPlan(Booking.Address address)
    {
        return new RecurringBooking(
                1,
                "x",
                3,
                2,
                new Date(),
                "full address",
                "Every week",
                1,
                "url",
                address
        );
    }


    public static ArrayList<RecurringBooking> createRecurringPlans(int n)
    {
        ArrayList<RecurringBooking> plans = new ArrayList<>();
        for (int i = 0; i < n; ++i)
        {
            plans.add(createRecurringPlan());
        }
        return plans;
    }

    public static RecurringBookingsResponse createRecurringBookingsResponse()
    {
        return new RecurringBookingsResponse(createRecurringPlans(2));

    }

    public static Booking.Address createAddress()
    {
        return new Booking.Address("address1", "address2", "city", "state", "10001", 0, 0);
    }

    public static Booking.Address createAddress(EditAddressRequest request)
    {
        return new Booking.Address(
                request.getAddress1(),
                request.getAddress2(),
                "city",
                "state",
                request.getZipcode(),
                0,
                0
        );
    }

    public static BookingEditFrequencyInfoResponse createBookingEditFrequencyInfoResponse(int current)
    {
        return new BookingEditFrequencyInfoResponse("$75", "$81", "$87", current);
    }
}
