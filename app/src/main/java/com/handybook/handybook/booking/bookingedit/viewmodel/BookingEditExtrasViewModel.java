package com.handybook.handybook.booking.bookingedit.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.model.OptionPrice;
import com.handybook.handybook.booking.model.PriceInfo;
import com.handybook.handybook.util.MathUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingEditExtrasViewModel
{
    private final BookingEditExtrasInfoResponse mBookingEditExtrasInfoResponse;

    private BookingEditExtrasViewModel(
            @NonNull final BookingEditExtrasInfoResponse bookingEditExtrasInfoResponse)
    {
        mBookingEditExtrasInfoResponse = bookingEditExtrasInfoResponse;
    }

    public static BookingEditExtrasViewModel from(
            @NonNull final BookingEditExtrasInfoResponse bookingEditExtrasInfoResponse)
    {
        return new BookingEditExtrasViewModel(bookingEditExtrasInfoResponse);
    }

    /**
     * @return an array of images for each option, used for options display
     */
    public int[] getOptionImagesResourceIdArray()
    {
        int[] resourceIds = new int[mBookingEditExtrasInfoResponse.getOptionsDisplayNames().length];
        for (int i = 0; i < resourceIds.length; i++)
        {
            resourceIds[i] = Booking.getImageResourceIdForMachineName(
                    mBookingEditExtrasInfoResponse.getOptionsMachineNames()[i]);
        }
        return resourceIds;
    }

    /**
     * //TODO: options view widget expects Integer rather than int, but seems not necessary
     * @param booking
     * @return an array of the indexes that should be checked, based on the booking extras
     */
    public Integer[] getCheckedIndexesForBooking(Booking booking)
    {
        ArrayList<Booking.ExtraInfo> extrasInfo = booking.getExtrasInfo();

        Map<String, Integer> extraDisplayNameToOptionIndexMap = getExtraDisplayNameToOptionIndexMap();
        Integer checkedIndexes[] = new Integer[extrasInfo.size()];
        for (int i = 0; i < checkedIndexes.length; i++)
        {
            checkedIndexes[i] = extraDisplayNameToOptionIndexMap.get(extrasInfo.get(i).getLabel());
        }
        return checkedIndexes;
    }

    public float getBookingBaseHours()
    {
        return mBookingEditExtrasInfoResponse.getBaseHours();
    }

    public String getOriginalBookingBasePriceFormatted()
    {
        float bookingBaseHours = mBookingEditExtrasInfoResponse.getBaseHours();
        //it is weird for api to return this
        String originalBookingBaseHours = getFormattedHoursForPriceTable(bookingBaseHours);
        String originalBookingBasePrice = mBookingEditExtrasInfoResponse.getPriceTable().
                get(originalBookingBaseHours).getTotalDueFormatted();
        return originalBookingBasePrice;
    }

    /**
     * NOTE: the only way to know what extras a user has selected
     * is by an array of extras display names in the booking object
     * so we must map those display names to associated index in the options
     * @return
     */
    public Map<String, Integer> getExtraDisplayNameToOptionIndexMap()
    {
        Map<String, Integer> extraDisplayNameToOptionIndexMap = new HashMap<>();
        for (int i = 0; i < mBookingEditExtrasInfoResponse.getOptionsDisplayNames().length; i++)
        {
            extraDisplayNameToOptionIndexMap.put(
                    mBookingEditExtrasInfoResponse.getOptionsDisplayNames()[i], i);
        }
        return extraDisplayNameToOptionIndexMap;
    }

    /**
     * @return the BookingOption model to be used to render the options view
     */
    public BookingOption getBookingOption()
    {
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_CHECKLIST);
        bookingOption.setOptions(mBookingEditExtrasInfoResponse.getOptionsDisplayNames());
        bookingOption.setOptionsSubText(mBookingEditExtrasInfoResponse.getOptionsSubText());
        bookingOption.setImageResourceIds(getOptionImagesResourceIdArray());
        OptionPrice[] optionPrices = mBookingEditExtrasInfoResponse.getOptionPrices();
        String[] optionsRightStrings = new String[optionPrices.length];
        for (int i = 0; i < optionPrices.length; i++)
        {
            optionsRightStrings[i] = optionPrices[i].getFormattedPrice();
        }

        bookingOption.setOptionsRightTitleText(optionsRightStrings);
        return bookingOption;
    }

    /**
     * //TODO: investigate why options view returns Integer[]
     * @param checkedIndexes the indexes of the booking extra options selected
     * @return the total number of hours for the booking extras selected
     */
    public float getExtraHoursForCheckedIndexes(Integer[] checkedIndexes)
    {
        float extrasHours = 0;
        for (Integer i : checkedIndexes) //build the extras details section
        {
            extrasHours += mBookingEditExtrasInfoResponse.getHourInfo()[i];
        }
        return extrasHours;
    }

    /**
     * //TODO: investigate why options view returns Integer[]
     * @param checkedIndexes the indexes of the booking extra options selected
     * @return the total number of hours for the booking, including the extras selected
     */
    public float getTotalHoursForCheckedIndexes(Integer[] checkedIndexes)
    {
        float bookingBaseHours = mBookingEditExtrasInfoResponse.getBaseHours();
        float extrasHours = getExtraHoursForCheckedIndexes(checkedIndexes);
        return bookingBaseHours + extrasHours;
    }

    /**
     * @return the formatted date that the customer is expected to be charged for this
     */
    public String getFutureBillDateFormatted()
    {
        return mBookingEditExtrasInfoResponse.getPaidStatus().getFutureBillDateFormatted();
    }

    /**
     * @param checkedIndexes
     * @param context
     * @return the string to display as "Total Due"
     */
    public String getTotalDueText(Integer[] checkedIndexes, Context context)
    {
        String totalHoursFormatted = getFormattedHoursForPriceTable(
                getTotalHoursForCheckedIndexes(checkedIndexes));
        Map<String, PriceInfo> priceTable = mBookingEditExtrasInfoResponse.getPriceTable();
        return priceTable.containsKey(
                totalHoursFormatted) ?
                priceTable.get(totalHoursFormatted).getTotalDueFormatted() :
                context.getResources().getString(R.string.no_data_indicator);
    }

    /**
     * @return the number of options available to choose from
     */
    public int getNumberOfOptions()
    {
        return mBookingEditExtrasInfoResponse.getOptionsDisplayNames().length;
    }

    public String getOptionMachineName(int optionIndex)
    {
        return mBookingEditExtrasInfoResponse.getOptionsMachineNames()[optionIndex];
    }

    public String getOptionDisplayName(int optionIndex)
    {
        return mBookingEditExtrasInfoResponse.getOptionsDisplayNames()[optionIndex];
    }

    public String getFormattedOptionPrice(int optionIndex)
    {
        return mBookingEditExtrasInfoResponse.getOptionPrices()[optionIndex].getFormattedPrice();
    }

    public float getHourInfo(int optionIndex)
    {
        return mBookingEditExtrasInfoResponse.getHourInfo()[optionIndex];
    }

    private String getFormattedHoursForPriceTable(float hours)
    {
        //have to do this because the price table returned from the api has key values
        // like 2, 2.5, 3, 3.5, etc

        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatNumberToAtMostOneDecimalPlace(hours);
    }

}
