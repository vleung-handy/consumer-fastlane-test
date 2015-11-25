package com.handybook.handybook.viewmodel;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.StringUtils;

import java.util.List;

public class BookingCancelRecurringViewModel
{
    private final List<Booking> mBookingList;

    private BookingCancelRecurringViewModel(
            @NonNull final List<Booking> bookingList)
    {
        mBookingList = bookingList;
    }

    public static BookingCancelRecurringViewModel from(
            @NonNull final List<Booking> bookingList)
    {
        return new BookingCancelRecurringViewModel(bookingList);
    }

    public BookingOption getBookingOption()
    {
        //TODO: put stuff in strings.xml
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        String optionStrings[] =  new String[mBookingList.size()];
        String optionSubtitleStrings[] = new String[optionStrings.length];
        for(int i = 0; i<optionStrings.length; i++)
        {
            Booking booking = mBookingList.get(i);

            //server sends us "every 2 weeks" but we want to display "Every 2 weeks"
            optionStrings[i] = StringUtils.capitalizeFirstCharacter(
                    booking.getRecurringInfoShort());
            optionSubtitleStrings[i] = "Next recurrence:\n" + DateTimeUtils.getFormattedDate
                    (booking.getStartDate(), DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER);
        }
        option.setOptions(optionStrings);
        option.setOptionsSubText(optionSubtitleStrings);
        return option;
    }

    public Booking getBookingForIndex(int index)
    {
        return mBookingList.get(index);
    }
}
