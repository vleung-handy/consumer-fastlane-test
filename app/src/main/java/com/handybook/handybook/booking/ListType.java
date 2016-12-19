package com.handybook.handybook.booking;

import com.handybook.handybook.booking.model.Booking;

/**
 */
public enum ListType
{
    UPCOMING(Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING),
    PAST(Booking.List.VALUE_ONLY_BOOKINGS_PAST);

    private String value;

    ListType(final String past)
    {
        this.value = past;
    }

    public String getValue()
    {
        return value;
    }

    public static ListType fromValue(String value)
    {
        for (final ListType listType : ListType.values())
        {
            if (listType.getValue().equals(value))
            {
                return listType;
            }
        }

        return null;
    }
}
