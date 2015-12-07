package com.handybook.handybook.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
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

    /**
     * @param context needed to resolve string resource ids
     * @return the BookingOption model that the cancel recurring fragment will use to render an
     * options view
     */
    public BookingOption getBookingOption(final Context context)
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        String optionStrings[] = new String[mBookingList.size()];
        String optionSubtitleStrings[] = new String[optionStrings.length];
        for (int i = 0; i < optionStrings.length; i++)
        {
            Booking booking = mBookingList.get(i);

            //server sends us "every 2 weeks" but we want to display "Every 2 weeks"
            optionStrings[i] = StringUtils.capitalizeFirstCharacter(
                    booking.getRecurringInfoShort());
            optionSubtitleStrings[i] = context.getString(R.string
                            .cancel_recurring_booking_option_entry_subtitle_formatted,
                    DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER.format(booking.getStartDate()));
        }
        option.setOptions(optionStrings);
        option.setOptionsSubText(optionSubtitleStrings);
        return option;
    }

    public Booking getBookingForIndex(final int index)
    {
        return mBookingList.get(index);
    }
}
