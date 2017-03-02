package com.handybook.handybook.referral.model;

import com.google.gson.annotations.SerializedName;

public class RedemptionDetails {

    @SerializedName("localization_data")
    private LocalizationData mLocalizationData;
    @SerializedName("receiver_coupon_amount")
    private int mReceiverCouponAmount;
    @SerializedName("sender")
    private Sender mSender;

    public LocalizationData getLocalizationData() {
        return mLocalizationData;
    }

    public int getReceiverCouponAmount() {
        return mReceiverCouponAmount;
    }

    public Sender getSender() {
        return mSender;
    }

    public static class LocalizationData {

        @SerializedName("currency_symbol")
        private String mCurrencySymbol;

        public String getCurrencySymbol() {
            return mCurrencySymbol;
        }
    }


    public static class Sender {

        @SerializedName("first_name")
        private String mFirstName;

        public String getFirstName() {
            return mFirstName;
        }
    }
}
