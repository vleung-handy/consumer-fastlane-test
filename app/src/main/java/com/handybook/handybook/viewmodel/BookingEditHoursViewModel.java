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

    private BookingEditHoursViewModel(@NonNull final EditHoursInfoResponse editHoursInfo)
    {
        mEditHoursInfo = editHoursInfo;
    }

    public static BookingEditHoursViewModel from(@NonNull final EditHoursInfoResponse editHoursInfo)
    {
        return new BookingEditHoursViewModel(editHoursInfo);
    }

    public String getBaseHoursFormatted()
    {
        return getFormattedHoursForPriceTable(mEditHoursInfo.getBaseHours());
    }

    public String getBasePriceFormatted()
    {
        return getFormattedPriceFromTotalPriceTableForDisplay(getBaseHoursFormatted());
    }

    public String getExtrasHoursFormatted()
    {
        return getFormattedHoursForPriceTable(mEditHoursInfo.getExtrasHours());
    }

    public String getExtrasPriceFormatted()
    {
        return mEditHoursInfo.getExtrasPrice() == null ? null : mEditHoursInfo.getExtrasPrice().getFormattedPrice();
    }

    public boolean isSelectedHoursLessThanBaseHours(final float selectedHours)
    {
        return selectedHours < mEditHoursInfo.getBaseHours();
    }

    public String getSelectedHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(selectedHours);
    }

    public String getAddedHoursPriceFormatted(final float selectedHours)
    {
        return getFormattedPriceDifferenceFromPriceTableForDisplay(getSelectedHoursFormatted(selectedHours));
    }

    public String getTotalDuePriceFormatted(final float selectedHours)
    {
        return getFormattedPriceFromTotalPriceTableForDisplay(getTotalHoursFormatted(selectedHours));
    }

    public String getAddedHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(getAddedHours(selectedHours));
    }

    public String getTotalHoursFormatted(final float selectedHours)
    {
        return getFormattedHoursForPriceTable(mEditHoursInfo.getBaseHours() + mEditHoursInfo.getExtrasHours() + getAddedHours(selectedHours));
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
        String optionHourStrings[] = mEditHoursInfo.getPriceTable().keySet().toArray(new String[]{}); //BookingOption.setOptions() only accepts an array of strings
        Arrays.sort(optionHourStrings, new Comparator<String>()
        {
            @Override
            public int compare(final String lhs, final String rhs)
            {
                //note that this is less computationally efficient than other methods, but more readable
                Float f1 = Float.parseFloat(lhs);
                Float f2 = Float.parseFloat(rhs);
                return f1.compareTo(f2);
            }
        });
        return optionHourStrings;
    }

    private String getFormattedHoursForPriceTable(float hours)
    {
        //have to do this because the price table returned from the api has key values like 2, 2.5, 3, 3.5, etc

        //round to one decimal place in case there are floating point rounding errors
        hours = MathUtils.roundToDecimalPlaces(hours, 1);
        return TextUtils.formatNumberToAtMostOneDecimalPlace(hours);
    }

    private String getFormattedPriceFromTotalPriceTableForDisplay(final String key)
    {
        return getFormattedTotalPriceFromPriceTable(key, mEditHoursInfo.getTotalPriceTable());
    }

    private String getFormattedPriceDifferenceFromPriceTableForDisplay(final String key)
    {
        return getFormattedPriceDifferenceFromPriceTable(key, mEditHoursInfo.getPriceTable());
    }

    //TODO: put in util function
    //TODO: move priceinfo class
    //checks to see if given key, map, and map.get(key) are non-null
    private boolean mapKeyEntryValid(final Object key, final Map<?, ?> map)
    {
        return key != null && map != null && map.get(key)!=null;
    }

    private String getFormattedTotalPriceFromPriceTable(final String key, final Map<String, EditExtrasInfo.PriceInfo> priceMap)
    {
        return mapKeyEntryValid(key, priceMap) ? priceMap.get(key).getTotalDueFormatted() : null;
    }

    private String getFormattedPriceDifferenceFromPriceTable(final String key, final Map<String, EditExtrasInfo.PriceInfo> priceMap)
    {
        return mapKeyEntryValid(key, priceMap) ? priceMap.get(key).getPriceDifferenceFormatted() : null;
    }

}
