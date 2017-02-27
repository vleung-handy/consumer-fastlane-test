package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.OptionPrice;
import com.handybook.handybook.booking.model.PaidStatus;
import com.handybook.handybook.booking.model.PriceInfo;

import java.util.Map;

public class BookingEditExtrasInfoResponse {

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

    public float getBaseHours() {
        return mBaseHours;
    }

    public float getExtrasHours() {
        return mExtrasHours;
    }

    public float[] getHourInfo() {
        return mHourInfo;
    }

    public String[] getOptionsDisplayNames() {
        return mOptionsDisplayNames;
    }

    public String[] getOptionsMachineNames() {
        return mOptionsMachineNames;
    }

    public String[][] getOptionsImages() {
        return mOptionsImages;
    }

    public String[] getOptionsSubText() {
        return mOptionsSubText;
    }

    public OptionPrice[] getOptionPrices() {
        return mOptionsPrices;
    }

    public String getTotalFormatted() {
        return mTotalFormatted;
    }

    public PaidStatus getPaidStatus() {
        return mPaidStatus;
    }

    public Map<String, PriceInfo> getPriceTable() {
        return mPriceTable;
    }

    public boolean isRecurring() {
        return mIsRecurring;
    }
}
