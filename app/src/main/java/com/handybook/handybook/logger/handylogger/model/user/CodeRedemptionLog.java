package com.handybook.handybook.logger.handylogger.model.user;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;


public class CodeRedemptionLog extends EventLog
{
    private static final String EVENT_CONTEXT = "code_redemption";

    public CodeRedemptionLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class CodeRedemptionOpenedLog extends CodeRedemptionLog
    {
        private static final String EVENT_TYPE = "opened";

        public CodeRedemptionOpenedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public abstract static class CodeRedemptionCouponLog extends CodeRedemptionLog
    {
        @SerializedName("coupon_code")
        private String mCouponCode;

        public CodeRedemptionCouponLog(final String eventType, final String couponCode)
        {
            super(eventType);
            mCouponCode = couponCode;
        }
    }


    public static class CodeRedemptionPromoSubmittedLog extends CodeRedemptionCouponLog
    {
        private static final String EVENT_TYPE = "promo_submitted";

        public CodeRedemptionPromoSubmittedLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    public static class CodeRedemptionPromoSuccessLog extends CodeRedemptionCouponLog
    {
        private static final String EVENT_TYPE = "promo_success";

        @SerializedName("voucher_code")
        private String mVoucherCode;

        public CodeRedemptionPromoSuccessLog(
                final String couponCode,
                @Nullable final String voucherCode
        )
        {
            super(EVENT_TYPE, couponCode);
            if (voucherCode != null)
            {
                mVoucherCode = voucherCode;
            }
        }
    }


    public static class CodeRedemptionPromoErrorLog extends CodeRedemptionCouponLog
    {
        private static final String EVENT_TYPE = "promo_error";

        public CodeRedemptionPromoErrorLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }
}
