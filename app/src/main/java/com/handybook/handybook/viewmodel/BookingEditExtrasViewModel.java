package com.handybook.handybook.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.model.response.BookingEditExtrasInfoResponse;
import com.handybook.handybook.model.response.OptionPrice;
import com.handybook.handybook.model.response.PriceInfo;
import com.handybook.handybook.util.MathUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingEditExtrasViewModel
{
    private final BookingEditExtrasInfoResponse mBookingEditExtrasInfoResponse;

    private BookingEditExtrasViewModel(
            @NonNull final BookingEditExtrasInfoResponse bookingEditFrequencyInfoResponse)
    {
        mBookingEditExtrasInfoResponse = bookingEditFrequencyInfoResponse;
    }

    public static BookingEditExtrasViewModel from(
            @NonNull final BookingEditExtrasInfoResponse bookingEditFrequencyInfoResponse)
    {
        return new BookingEditExtrasViewModel(bookingEditFrequencyInfoResponse);
    }

    //builds an array of images for each option, used for options display
    public int[] getOptionImagesResourceIdArray()
    {
        int[] resourceIds = new int[mBookingEditExtrasInfoResponse.getOptionsDisplayNames().length];
        for (int i = 0; i < resourceIds.length; i++)
        {
            resourceIds[i] = Booking.getImageResourceIdForMachineName(mBookingEditExtrasInfoResponse.getOptionsMachineNames()[i]);
        }
        return resourceIds;
    }

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
        float bookingBaseHours = mBookingEditExtrasInfoResponse.getBaseHours(); //it is weird for api to return this
        String originalBookingBaseHours = getFormattedHoursForPriceTable(bookingBaseHours);
        String originalBookingBasePrice = mBookingEditExtrasInfoResponse.getPriceTable().get(originalBookingBaseHours).getTotalDueFormatted();
        return originalBookingBasePrice;
    }

    //NOTE: the only way to know what extras a user has selected is by an array of extras display names in the booking object
    //so we must map those display names to associated index in the options
    public Map<String, Integer> getExtraDisplayNameToOptionIndexMap()
    {
        Map<String, Integer> extraDisplayNameToOptionIndexMap = new HashMap<>();
        for (int i = 0; i < mBookingEditExtrasInfoResponse.getOptionsDisplayNames().length; i++)
        {
            extraDisplayNameToOptionIndexMap.put(mBookingEditExtrasInfoResponse.getOptionsDisplayNames()[i], i);
        }
        return extraDisplayNameToOptionIndexMap;
    }

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

    public float getExtraHoursForCheckedIndexes(Integer[] checkedIndexes)
    {
        float extrasHours = 0;
        for (Integer i : checkedIndexes) //build the extras details section
        {
            extrasHours += mBookingEditExtrasInfoResponse.getHourInfo()[i];
        }
        return extrasHours;
    }

    public float getTotalHoursForCheckedIndexes(Integer[] checkedIndexes)
    {
        float bookingBaseHours = mBookingEditExtrasInfoResponse.getBaseHours();
        float extrasHours = getExtraHoursForCheckedIndexes(checkedIndexes);
        return bookingBaseHours + extrasHours;
    }

    public String getFutureBillDateFormatted()
    {
        return mBookingEditExtrasInfoResponse.getPaidStatus().getFutureBillDateFormatted();
    }

    public String getTotalDueText(Integer[] checkedIndexes, Context context)
    {
        String totalHoursFormatted = getFormattedHoursForPriceTable(getTotalHoursForCheckedIndexes(checkedIndexes));
        Map<String, PriceInfo> priceTable = mBookingEditExtrasInfoResponse.getPriceTable();
        return priceTable.containsKey(
                totalHoursFormatted) ?
                priceTable.get(totalHoursFormatted).getTotalDueFormatted() :
                context.getResources().getString(R.string.no_data_indicator);
    }

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
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc

        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatNumberToAtMostOneDecimalPlace(hours);
    }

}
