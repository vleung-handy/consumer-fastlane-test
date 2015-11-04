package com.handybook.handybook.viewmodel;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.EditExtrasInfo;
import com.handybook.handybook.model.response.EditHoursInfoResponse;
import com.handybook.handybook.util.MathUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class BookingEditHoursViewModel
{
    //TODO: create a view model for other edit screens and move some of these methods to a super
    private final EditHoursInfoResponse mEditHoursInfo;
    /**
     * A comparator that compares strings as numbers
     */
    private static final Comparator<String> COMPARATOR_STRING_NUMERIC_ASCENDING = new Comparator<String>()
    {
        @Override
        public int compare(final String lhs, final String rhs)
        {
            //note that this is less computationally efficient than other methods, but more readable
            Float f1 = Float.parseFloat(lhs);
            Float f2 = Float.parseFloat(rhs);
            return f1.compareTo(f2);
        }
    };

    private BookingEditHoursViewModel(@NonNull final EditHoursInfoResponse editHoursInfo)
    {
        mEditHoursInfo = editHoursInfo;
    }

    public static BookingEditHoursViewModel from(@NonNull final EditHoursInfoResponse editHoursInfo)
    {
        return new BookingEditHoursViewModel(editHoursInfo);
    }

    /**
     * @return A formatted string that indicates the number of base hours in the original booking
     */
    public String getBaseHoursFormatted()
    {
        return getFormattedHoursForPriceTable(mEditHoursInfo.getBaseHours());
    }

    /**
     * @return A formatted string that indicates the base price of the original booking
     */
    public String getBasePriceFormatted()
    {
        return getFormattedPriceFromTotalPriceTable(getBaseHoursFormatted());
    }

    /**
     * @return A formatted string that indicates the number of hours allocated for service extras in the original booking
     */
    public String getExtrasHoursFormatted()
    {
        return getFormattedHoursForPriceTable(mEditHoursInfo.getExtrasHours());
    }

    /**
     * @return A formatted string that indicates the total price of the service extras in the original booking
     */
    public String getExtrasPriceFormatted()
    {
        return mEditHoursInfo.getExtrasPrice() == null ? null : mEditHoursInfo.getExtrasPrice().getFormattedPrice();
    }

    public boolean isSelectedHoursLessThanBaseHours(final float selectedHours)
    {
        return selectedHours < mEditHoursInfo.getBaseHours();
    }

    /**
     *
     * @param selectedHours
     * @return A formatted string that indicates the number of hours the user has selected
     */
    public String getSelectedHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(selectedHours);
    }

    /**
     *
     * @param selectedHours
     * @return A formatted string that indicates the difference between the selected hours and the base hours in the original booking
     */
    public String getAddedHoursPriceFormatted(final float selectedHours)
    {
        return getFormattedPriceDifference(getSelectedHoursFormatted(selectedHours));
    }

    /**
     * @return A formatted string that indicates the new total price of the booking
     */
    public String getTotalDuePriceFormatted(final float selectedHours)
    {
        return getFormattedPriceFromTotalPriceTable(getTotalHoursFormatted(selectedHours));
    }

    /**
     * @return A formatted string that indicates the number of hours that the user is adding to the original booking
     */
    public String getAddedHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(getAddedHours(selectedHours));
    }

    /**
     * @return A formatted string that indicates the new total hours of the booking
     */
    public String getTotalHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(
                mEditHoursInfo.getBaseHours()
                        + mEditHoursInfo.getExtrasHours()
                        + getAddedHours(selectedHours));
    }

    public String getFutureBillDateFormatted()
    {
        return mEditHoursInfo.getPaidStatus().getFutureBillDateFormatted();
    }

    private float getAddedHours(final float selectedHours)
    {
        return selectedHours - mEditHoursInfo.getBaseHours();
    }

    public float getBaseHours()
    {
        return mEditHoursInfo.getBaseHours();
    }

    //used to create the options array
    public String[] getSortedHoursFromPriceTable()
    {
        String optionHourStrings[] = mEditHoursInfo.getPriceTable().keySet().toArray(new String[]{});
        //BookingOption.setOptions() only accepts an array of strings
        Arrays.sort(optionHourStrings, COMPARATOR_STRING_NUMERIC_ASCENDING);
        return optionHourStrings;
    }

    //TODO: will rename these later
    private String getFormattedHoursForPriceTable(float hours)
    {
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc
        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatNumberToAtMostOneDecimalPlace(hours);
    }

    private String getFormattedPriceFromTotalPriceTable(final String key)
    {
        Map<String, EditExtrasInfo.PriceInfo> priceMap = mEditHoursInfo.getTotalPriceTable();
        return mapKeyEntryValid(key, priceMap) ? priceMap.get(key).getTotalDueFormatted() : null;
    }

    private String getFormattedPriceDifference(final String key)
    {
        Map<String, EditExtrasInfo.PriceInfo> priceMap = mEditHoursInfo.getPriceTable();
        return mapKeyEntryValid(key, priceMap) ? priceMap.get(key).getPriceDifferenceFormatted() : null;
    }

    //TODO: put in util function
    //TODO: move priceinfo class
    //checks to see if given key, map, and map.get(key) are non-null
    private boolean mapKeyEntryValid(final Object key, final Map<?, ?> map)
    {
        return key != null && map != null && map.get(key) != null;
    }
}
