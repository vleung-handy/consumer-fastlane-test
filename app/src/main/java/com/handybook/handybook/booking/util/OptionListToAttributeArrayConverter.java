package com.handybook.handybook.booking.util;

import android.support.annotation.NonNull;

import com.handybook.handybook.booking.model.Option;

import java.util.List;

/**
 * utils for creating lists of a specific option attribute
 *
 * unfortunately we have to build arrays from recurrence options,
 * each of just a particular options attribute,
 * because of the way the BookingOption model is structured
 */
public class OptionListToAttributeArrayConverter
{
    @NonNull
    public static <T extends Option> String[] getOptionsTitleTextArray(
            @NonNull List<T> options)
    {
        String[] optionsTitleTextArray = new String[options.size()];
        for(int i = 0; i<optionsTitleTextArray.length; i++)
        {
            optionsTitleTextArray[i] = options.get(i).getTitleText();
        }
        return optionsTitleTextArray;
    }

    @NonNull
    public static <T extends Option> String[] getOptionsSubTextArray(
            @NonNull List<T> options)
    {
        String[] optionsSubTextArray = new String[options.size()];
        for(int i = 0; i<optionsSubTextArray.length; i++)
        {
            optionsSubTextArray[i] = options.get(i).getSubtitleText();
        }
        return optionsSubTextArray;
    }

}
