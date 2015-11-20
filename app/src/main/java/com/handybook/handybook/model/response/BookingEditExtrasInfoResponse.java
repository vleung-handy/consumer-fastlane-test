package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.Booking;

import java.util.HashMap;
import java.util.Map;

public class BookingEditExtrasInfoResponse
{
    @SerializedName("base_hours")
    private float mBaseHours;
    @SerializedName("extras_hours")
    private float mExtrasHours;
    @SerializedName("options")
    private String[] mOptionsDisplayNames;
    @SerializedName("options_machine_names")
    private String[] mOptionsMachineNames;
    @SerializedName("options_images")
    private String[][] mOptionsImages;
    @SerializedName("options_sub_text")
    private String[] mOptionsSubText;
    @SerializedName("hour_info")
    private float[] mHourInfo;
    @SerializedName("options_prices")
    private OptionPrice[] mOptionsPrices;
    @SerializedName("total_formatted")
    private String mTotalFormatted;
    @SerializedName("paid_status")
    private PaidStatus mPaidStatus;
    @SerializedName("price_table")
    private Map<String, PriceInfo> mPriceTable;
    @SerializedName("recurring")
    private boolean mIsRecurring;

    public float getBaseHours()
    {
        return mBaseHours;
    }

    public float getExtrasHours()
    {
        return mExtrasHours;
    }

    public float[] getHourInfo()
    {
        return mHourInfo;
    }

    public String[] getOptionsDisplayNames()
    {
        return mOptionsDisplayNames;
    }

    public String[] getOptionsMachineNames()
    {
        return mOptionsMachineNames;
    }

    public String[][] getOptionsImages()
    {
        return mOptionsImages;
    }

    public String[] getOptionsSubText()
    {
        return mOptionsSubText;
    }

    public OptionPrice[] getOptionPrices()
    {
        return mOptionsPrices;
    }

    public String getTotalFormatted()
    {
        return mTotalFormatted;
    }

    public PaidStatus getPaidStatus()
    {
        return mPaidStatus;
    }

    public Map<String, PriceInfo> getPriceTable()
    {
        return mPriceTable;
    }

    public boolean isRecurring()
    {
        return mIsRecurring;
    }

    //builds an array of images for each option, used for options display
    public int[] getOptionImagesResourceIdArray()
    {
        int[] resourceIds = new int[getOptionsDisplayNames().length];
        for (int i = 0; i < resourceIds.length; i++)
        {
            resourceIds[i] = Booking.getImageResourceIdForMachineName(getOptionsMachineNames()[i]);
        }
        return resourceIds;
    }

    //NOTE: the only way to know what extras a user has selected is by an array of extras display names in the booking object
    //so we must map those display names to associated index in the options
    public Map<String, Integer> getExtraDisplayNameToOptionIndexMap()
    {
        Map<String, Integer> extraDisplayNameToOptionIndexMap = new HashMap<>();
        for (int i = 0; i < getOptionsDisplayNames().length; i++)
        {
            extraDisplayNameToOptionIndexMap.put(getOptionsDisplayNames()[i], i);
        }
        return extraDisplayNameToOptionIndexMap;
    }
}
