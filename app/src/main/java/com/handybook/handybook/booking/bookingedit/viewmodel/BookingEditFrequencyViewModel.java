package com.handybook.handybook.booking.bookingedit.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.constant.BookingRecurrence;
import com.handybook.handybook.booking.constant.BookingRecurrence.BookingRecurrenceCode;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;

public class BookingEditFrequencyViewModel
{
    private final BookingEditFrequencyInfoResponse mEditFrequencyInfoResponse;
    private final int[] mFrequencyOptionsArray = new int[]{BookingRecurrence.WEEKLY, BookingRecurrence.BIMONTHLY, BookingRecurrence.MONTHLY};
    //allowing edit frequency only for recurring bookings

    private BookingEditFrequencyViewModel(
            @NonNull final BookingEditFrequencyInfoResponse bookingEditFrequencyInfoResponse)
    {
        mEditFrequencyInfoResponse = bookingEditFrequencyInfoResponse;
    }

    public static BookingEditFrequencyViewModel from(
            @NonNull final BookingEditFrequencyInfoResponse bookingEditFrequencyInfoResponse)
    {
        return new BookingEditFrequencyViewModel(bookingEditFrequencyInfoResponse);
    }

    /**
     *
     * @param context
     * @param booking
     * @return A BookingOption object that models the booking options view
     */
    public BookingOption getBookingOptionFromBooking(final Context context, final Booking booking)
    {
        //TODO: mostly duplicated from checkout flow fragment, should reconsider redesigning the options logic
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(getDisplayStringsArray(context));

        //update the options right-hand text views
        String optionsSubText[] = new String[mFrequencyOptionsArray.length];

        int indexForFreq = getOptionIndexForFrequencyValue();
        if(indexForFreq >= 0)
        {
            optionsSubText[indexForFreq] = context.getResources().getString(R.string.current);//highlight the current selected option
        }
        option.setOptionsSubText(optionsSubText);
        option.setOptionsRightTitleText(getFormattedPricesForFrequencyArray());

        String[] optionsRightSubText = new String[mFrequencyOptionsArray.length];
        String rightSubText = context.getResources().getString(
                R.string.booking_options_right_sub_text,
                getServiceShortNameFromBooking(context, booking));

        //set all the right sub texts to the service short name
        for (int i = 0; i < optionsRightSubText.length; i++)
        {
            optionsRightSubText[i] = rightSubText;
        }
        option.setOptionsRightSubText(optionsRightSubText);
        return option;
    }

    /**
     * gets the service short name from the given booking, to be displayed on the right hand side of the option entry
     * @param context
     * @param booking
     * @return
     */
    public final String getServiceShortNameFromBooking(final Context context, final Booking booking)
    {
        switch (booking.getServiceMachineName())
        {
            case Booking.SERVICE_CLEANING:
            case Booking.SERVICE_HOME_CLEANING:
            case Booking.SERVICE_OFFICE_CLEANING:
                return context.getResources().getString(R.string.clean);
            default:
                return context.getResources().getString(R.string.job);
        }
    }

    /**
     *
     * @return An array of formatted strings that represent frequencies, to be displayed as the main options text
     */
    private String[] getFormattedPricesForFrequencyArray()
    {
        String[] priceArray = new String[mFrequencyOptionsArray.length];
        //this is string because server returns formatted prices (let's not do that in new api)

        for (int i = 0; i < priceArray.length; i++)
        {
            priceArray[i] = getFormattedPriceForFrequency(mFrequencyOptionsArray[i]);
        }
        return priceArray;
    }

    /**
     *
     * @param recurrenceCode
     * @return The new booking price for a given booking frequency
     */
    private String getFormattedPriceForFrequency(@BookingRecurrenceCode final int recurrenceCode)
    {
        switch (recurrenceCode)
        {
            case BookingRecurrence.WEEKLY:
                return mEditFrequencyInfoResponse.getWeeklyPriceFormatted();
            case BookingRecurrence.BIMONTHLY:
                return mEditFrequencyInfoResponse.getBimonthlyPriceFormatted();
            case BookingRecurrence.MONTHLY:
                return mEditFrequencyInfoResponse.getMonthlyPriceFormatted();
            default:
                return null;
        }
    }

    /**
     *
     * @return the index of the options array entry that reflects the current frequency of the booking series. used to select a default option
     */
    public int getOptionIndexForFrequencyValue()
    {
        final int freq = mEditFrequencyInfoResponse.getCurrentFrequency();
        for (int i = 0; i < mFrequencyOptionsArray.length; i++)
        {
            if (freq == mFrequencyOptionsArray[i]) { return i; }
        }
        Crashlytics.logException(new Exception(
                "BookingEditFrequencyViewModel::getOptionIdexForFrequencyValue():" +
                        "current frequency of booking does not " +
                        "match any frequency in the options array: " + freq));
        return -1;
    }

    private String[] getDisplayStringsArray(final Context context)
    {
        String[] displayStrings = new String[mFrequencyOptionsArray.length];
        for(int i = 0; i<mFrequencyOptionsArray.length; i++)
        {
            int recurrenceCode = mFrequencyOptionsArray[i];
            displayStrings[i] = getDisplayStringForBookingFrequency(context, recurrenceCode);
        }
        return displayStrings;
    }

    private String getDisplayStringForBookingFrequency(final Context context,
                                                       @BookingRecurrenceCode final int recurrenceCode)
    {
        switch(recurrenceCode)
        {
            case BookingRecurrence.WEEKLY:
                return context.getResources().getString(R.string.every_week);
            case BookingRecurrence.BIMONTHLY:
                return context.getResources().getString(R.string.every_two_weeks);
            case BookingRecurrence.MONTHLY:
                return context.getResources().getString(R.string.every_four_weeks);
            default:
                return null;
        }
    }

    /**
     *
     * @param index
     * @return The frequency value for a given option index
     */
    public int getFrequencyOptionValue(final int index) //TODO: need better name
    {
        return mFrequencyOptionsArray[index];
    }
}
