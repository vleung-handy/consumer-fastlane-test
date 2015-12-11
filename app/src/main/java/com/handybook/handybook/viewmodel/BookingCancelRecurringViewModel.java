package com.handybook.handybook.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.model.response.RecurringBooking;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.StringUtils;

import java.util.List;

public class BookingCancelRecurringViewModel
{
    private final List<RecurringBooking> mRecurringBookingList;

    private BookingCancelRecurringViewModel(
            @NonNull final List<RecurringBooking> recurringBookingList)
    {
        mRecurringBookingList = recurringBookingList;
    }

    public static BookingCancelRecurringViewModel from(
            @NonNull final List<RecurringBooking> bookingList)
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
        String optionStrings[] = new String[mRecurringBookingList.size()];
        String optionSubtitleStrings[] = new String[optionStrings.length];
        for (int i = 0; i < optionStrings.length; i++)
        {
            RecurringBooking recurringBooking = mRecurringBookingList.get(i);

            //server sends us "every 2 weeks" but we want to display "Every 2 weeks"
            optionStrings[i] = StringUtils.capitalizeFirstCharacter(
                    recurringBooking.getRecurringStringShort());
            optionSubtitleStrings[i] = context.getString(R.string
                            .cancel_recurring_booking_option_entry_subtitle_formatted,
                    DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER.format(recurringBooking.getNextRecurrenceDate()));
        }
        option.setOptions(optionStrings);
        option.setOptionsSubText(optionSubtitleStrings);
        return option;
    }

    public RecurringBooking getBookingForIndex(final int index)
    {
        return mRecurringBookingList.get(index);
    }
}
