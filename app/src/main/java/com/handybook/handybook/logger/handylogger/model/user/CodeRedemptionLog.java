package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;


public class CodeRedemptionLog extends EventLog
{
    private static final String EVENT_CONTEXT = "code_redemption";

    @SerializedName("coupon_code")
    private String mCouponCode;

    public CodeRedemptionLog(final String eventType, final String couponCode)
    {
        super(eventType, EVENT_CONTEXT);
        mCouponCode = couponCode;
    }

    public static class CodeRedemptionOpenedLog extends CodeRedemptionLog
    {
        private static final String EVENT_TYPE = "opened";

        public CodeRedemptionOpenedLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    public static class CodeRedemptionPromoSubmittedLog extends CodeRedemptionLog
    {
        private static final String EVENT_TYPE = "promo_submitted";

        public CodeRedemptionPromoSubmittedLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    public static class CodeRedemptionPromoSuccessLog extends CodeRedemptionLog
    {
        private static final String EVENT_TYPE = "promo_success";

        public CodeRedemptionPromoSuccessLog(
                final String couponCode
        )
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    public static class CodeRedemptionPromoErrorLog extends CodeRedemptionLog
    {
        private static final String EVENT_TYPE = "promo_error";

        public CodeRedemptionPromoErrorLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }
}
