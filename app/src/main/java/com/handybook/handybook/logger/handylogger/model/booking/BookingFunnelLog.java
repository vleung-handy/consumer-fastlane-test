package com.handybook.handybook.logger.handylogger.model.booking;

import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.BookingCompleteTransaction;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.user.UserLoginLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BookingFunnelLog extends EventLog
{
    // For user login logging
    public static final String AUTH_TYPE_FACEBOOK = "facebook";
    public static final String AUTH_TYPE_EMAIL = "email";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AUTH_TYPE_EMAIL,
            AUTH_TYPE_FACEBOOK
    })
    public @interface AuthType
    {
    }

    private static final String EVENT_CONTEXT = "booking_funnel";
    @SerializedName("auth_type")
    private String mAuthType;

    protected BookingFunnelLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    protected BookingFunnelLog(final String eventType, @UserLoginLog.AuthType final String authType )
    {
        super(eventType, EVENT_CONTEXT);
        mAuthType = authType;
    }

    public static class BookingZipShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "zip_shown";

        public BookingZipShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    /*
    don't like that this log nearly duped in the BookingDetailsLog class file
    since the only thing that is different is the base event context

    but putting this in each file for consistency to avoid confusion
    as each logging class file currently represents a single context
    and contains only logs that are within that context
     */
    public static class EntryMethodLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE_PREFIX = "entry_method_";

        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("is_recurring")
        private final boolean mIsBookingRecurring;

        @SerializedName("entry_method")
        private final String mEntryMethodMachineName;

        private EntryMethodLog(
                final String eventTypeSuffix,
                final String bookingId,
                final boolean isBookingRecurring,
                final String entryMethodMachineName
        )
        {
            super(EVENT_TYPE_PREFIX + eventTypeSuffix);
            mBookingId = bookingId;
            mIsBookingRecurring = isBookingRecurring;
            mEntryMethodMachineName = entryMethodMachineName;
        }

        /*
        in logging terms, whether an entry method is "recommended"
        is whether the entry method option subtitle is present
        ex. "Chosen by 13 of your neighbors"
        */
        public static class RecommendationShown extends EntryMethodLog
        {
            private static final String EVENT_TYPE_SUFFIX = "recommendation_shown";

            public RecommendationShown(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            )
            {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
        }


        /*
        in logging terms, entry method info is "submitted"
        when the user clicks next, not when the network call itself is made
        */
        public static class InfoSubmitted extends EntryMethodLog
        {
            private static final String EVENT_TYPE_SUFFIX = "info_submitted";

            public InfoSubmitted(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            )
            {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
        }
    }


    public static class BookingZipSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "zip_submitted";

        @SerializedName("zip")
        private String mZip;

        public BookingZipSubmittedLog(final String zip)
        {
            super(EVENT_TYPE);
            mZip = zip;
        }
    }


    public static class BookingZipSuccessLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "zip_success";

        @SerializedName("zip")
        private String mZip;

        public BookingZipSuccessLog(final String zip)
        {
            super(EVENT_TYPE);
            mZip = zip;
        }
    }


    public static class BookingZipErrorLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "zip_error";

        @SerializedName("zip")
        private String mZip;
        @SerializedName("error_info")
        private String mErrorInfo;

        public BookingZipErrorLog(final String zip, final String errorInfo)
        {
            super(EVENT_TYPE);
            mZip = zip;
            mErrorInfo = errorInfo;
        }
    }


    public static class BookingServiceDetailsShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "service_details_shown";

        public BookingServiceDetailsShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingServiceDetailsSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "service_details_submitted";

        @SerializedName("service_details")
        private HashMap<String, String> mServiceDetails;

        public BookingServiceDetailsSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingCommentsShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "comments_shown";

        public BookingCommentsShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingCommentsSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "comments_submitted";

        public BookingCommentsSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingSchedulerShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "scheduler_shown";

        public BookingSchedulerShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingSchedulerSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "scheduler_submitted";

        @SerializedName("date_start")
        private String mDateStart;

        public BookingSchedulerSubmittedLog(final String dateStart)
        {
            super(EVENT_TYPE);
            mDateStart = dateStart;
        }
    }


    public static class BookingWindowShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "window_shown";

        public BookingWindowShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ProTeamShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "pro_team_shown";

        public ProTeamShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ProTeamSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "pro_team_submitted";

        private static final String CLEANERS_ADDED_COUNT = "preferred_cleaning_provider_added_count";
        private static final String CLEANERS_REMOVED_COUNT = "preferred_cleaning_provider_removed_count";
        private static final String HANDYMEN_ADDED_COUNT = "preferred_handymen_provider_added_count";
        private static final String HANDYMEN_REMOVED_COUNT = "preferred_handymen_provider_removed_count";

        @SerializedName("pro_team_edit")
        HashMap<String, Integer> mProTeamEdit;

        public ProTeamSubmittedLog(
                final int cleanersToAdd, final int cleanersToRemove,
                final int handymenToAdd, final int handymenToRemove
        )
        {
            super(EVENT_TYPE);
            HashMap<String, Integer> map = new HashMap<>();
            map.put(CLEANERS_ADDED_COUNT, cleanersToAdd);
            map.put(CLEANERS_REMOVED_COUNT, cleanersToRemove);
            map.put(HANDYMEN_ADDED_COUNT, handymenToAdd);
            map.put(HANDYMEN_REMOVED_COUNT, handymenToRemove);

            mProTeamEdit = map;
        }
    }


    public static class BookingQuoteRequestSubmitted extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "quote_request_submitted";

        public BookingQuoteRequestSubmitted()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingQuoteRequestSuccess extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "quote_request_success";

        public BookingQuoteRequestSuccess()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingQuoteRequestError extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "quote_request_error";

        @SerializedName("error_info")
        private String mErrorInfo;

        public BookingQuoteRequestError(final String errorInfo)
        {
            super(EVENT_TYPE);
            mErrorInfo = errorInfo;
        }
    }


    public static class BookingQuotePageShown extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "quote_page_shown";

        public BookingQuotePageShown()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingFrequencyShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "frequency_shown";

        @SerializedName("recurring_options")
        private List<Integer> mRecurringOptions;

        public BookingFrequencyShownLog(final List<Integer> recurringOptions)
        {
            super(EVENT_TYPE);
            mRecurringOptions = recurringOptions;
        }
    }


    public static class BookingFrequencySubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "frequency_submitted";

        @SerializedName("frequency")
        private int mFrequency;
        @SerializedName("quote_price")
        private int mQuotePrice;

        public BookingFrequencySubmittedLog(final int frequency, final int quotePrice)
        {
            super(EVENT_TYPE);
            mFrequency = frequency;
            mQuotePrice = quotePrice;
        }
    }


    public static class BookingPushbackShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "pushback_shown";

        @SerializedName("original_date_start")
        private Date mOriginalDateStart;

        public BookingPushbackShownLog(final Date originalDateStart)
        {
            super(EVENT_TYPE);
            mOriginalDateStart = originalDateStart;
        }
    }


    public static class BookingPushbackSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "pushback_submitted";

        @SerializedName("original_date_start")
        private Date mOriginalDateStart;
        @SerializedName("new_date_start")
        private Date mNewDateStart;

        public BookingPushbackSubmittedLog(final Date originalDateStart, final Date newDateStart)
        {
            super(EVENT_TYPE);
            mOriginalDateStart = originalDateStart;
            mNewDateStart = newDateStart;
        }
    }


    public static class BookingExtrasShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "extras_shown";

        @SerializedName("extras_options")
        private String[] mExtrasOptions;

        public BookingExtrasShownLog(final String[] extrasOptions)
        {
            super(EVENT_TYPE);
            mExtrasOptions = extrasOptions;
        }
    }


    public static class BookingExtrasSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "extras_submitted";

        @SerializedName("extras")
        private String[] mExtras;

        public BookingExtrasSubmittedLog(final String[] extras)
        {
            super(EVENT_TYPE);
            mExtras = extras;
        }
    }


    public static class BookingPaymentShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "payment_shown";

        public BookingPaymentShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingAddressShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "address_shown";

        public BookingAddressShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingAddressSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "address_submitted";

        public BookingAddressSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingRequestSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "booking_request_submitted";

        public BookingRequestSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingRequestErrorLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "booking_request_error";

        @SerializedName("error_info")
        private String mErrorInfo;

        public BookingRequestErrorLog(final String errorInfo)
        {
            super(EVENT_TYPE);
            mErrorInfo = errorInfo;
        }
    }


    public static class BookingRequestSuccessLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "booking_request_success";

        @SerializedName("booking_id")
        private int mBookingId;

        public BookingRequestSuccessLog(final int bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class BookingRequestMadeLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "booking_made";

        @SerializedName("booking_email")
        private String mBookingEmail;
        @SerializedName("booking_id")
        private int mBookingId;
        @SerializedName("charge")
        private int mCharge;
        @SerializedName("cleaning_extras_tried")
        private boolean mIsCleaningExtrasTried;
        @SerializedName("dynamic_price_shown")
        private boolean mIsDynamicPriceShown;
        @SerializedName("extra_hours")
        private float mExtraHours;
        @SerializedName("extras")
        private String mExtras;
        @SerializedName("first_ever_booking")
        private boolean mIsFirstEverBooking;
        @SerializedName("frequency")
        private String mFrequency;
        @SerializedName("marketing_source")
        private String mMarketingSource;
        @SerializedName("price_per_hour")
        private float mPricePerHour;
        @SerializedName("repeat_freq")
        private int mRepeatFreq;
        @SerializedName("service_type")
        private String mServiceType;
        @SerializedName("zip")
        private int mZip;
        @SerializedName("coupon_code")
        private String mCouponCode;

        public BookingRequestMadeLog(
                final BookingQuote bookingQuote,
                final BookingTransaction transaction,
                final BookingCompleteTransaction completeTransaction,
                final float extraHours,
                final Service service
        )
        {
            super(EVENT_TYPE);
            mBookingEmail = transaction.getEmail();
            mBookingId = transaction.getBookingId();
            mCharge = completeTransaction.getCharge();
            mExtras = transaction.getExtraCleaningText();
            mIsCleaningExtrasTried = !TextUtils.isEmpty(mExtras);
            mIsDynamicPriceShown = bookingQuote.getPeakPriceTable() != null && bookingQuote.getPeakPriceTable()
                                                                                           .size() > 0;
            mExtraHours = extraHours;
            mIsFirstEverBooking = completeTransaction.isFirstEverBooking();
            mFrequency = getFrequencyName(transaction);
            mMarketingSource = transaction.getReferrerToken();
            mPricePerHour = bookingQuote.getHourlyAmount();
            mRepeatFreq = transaction.getRecurringFrequency();
            mServiceType = service == null ? null : service.getName();
            mZip = Integer.parseInt(transaction.getZipCode());
            mCouponCode = transaction.promoApplied();
        }

        private String getFrequencyName(BookingTransaction transaction)
        {
            switch (transaction.getRecurringFrequency())
            {
                case 4:
                    return "monthly";
                case 2:
                    return "biweekly";
                case 1:
                    return "weekly";
                default:
                    return "once";
            }
        }
    }


    public static class BookingAccessInformationShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "access_information_shown";

        public BookingAccessInformationShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingAccessInformationSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "access_information_submitted";

        @SerializedName("access_type")
        private String mAccessType;

        public BookingAccessInformationSubmittedLog(final String accessType)
        {
            super(EVENT_TYPE);
            mAccessType = accessType;
        }
    }


    public static class BookingAccessInformationDismissedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "access_information_dismissed";

        public BookingAccessInformationDismissedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingRoutineShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "routine_shown";

        public BookingRoutineShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingRoutineSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "routine_submitted";

        public BookingRoutineSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingRoutineDismissedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "routine_dismissed";

        public BookingRoutineDismissedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingShareInfoShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "share_info_shown";

        public BookingShareInfoShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingShareInfoSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "share_info_submitted";

        public BookingShareInfoSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingShareInfoDismissedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "share_info_dismissed";

        public BookingShareInfoDismissedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingPasswordShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "password_shown";

        public BookingPasswordShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingPasswordSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "password_submitted";

        public BookingPasswordSubmittedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingPasswordDismissedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "password_dismissed";

        public BookingPasswordDismissedLog()
        {
            super(EVENT_TYPE);
        }
    }

    // Referrals


    public abstract static class ReferralBookingFunnelLog extends BookingFunnelLog
    {
        @SerializedName("coupon_code")
        private String mCouponCode;

        public ReferralBookingFunnelLog(final String eventType, final String couponCode)
        {
            super(eventType);
            mCouponCode = couponCode;
        }
    }


    public static class ReferralBookingFunnelCodeEnteredLog extends ReferralBookingFunnelLog
    {
        private static final String EVENT_TYPE = "code_entered";

        public ReferralBookingFunnelCodeEnteredLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    public static class ReferralBookingFunnelCodeAppliedLog extends ReferralBookingFunnelLog
    {
        private static final String EVENT_TYPE = "code_applied";

        public ReferralBookingFunnelCodeAppliedLog(final String couponCode)
        {
            super(EVENT_TYPE, couponCode);
        }
    }


    //*************************** Dupe of UserLogingLog and UserContactLog ************************
    //NOTE: Wasn't enough time to do this right, so it's currently duplicates
    public static class UserLoginShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "shown";

        public UserLoginShownLog(@AuthType String authType)
        {
            super(EVENT_TYPE, authType);
        }
    }


    public static class UserLoginSubmittedLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "submitted";

        @SerializedName("email")
        private String mEmail;

        public UserLoginSubmittedLog(final String email, @AuthType String loginType)
        {
            super(EVENT_TYPE, loginType);
            mEmail = email;
        }
    }

    public static class UserLoginSuccessLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "success";

        public UserLoginSuccessLog(@AuthType String authType)
        {
            super(EVENT_TYPE, authType);
        }
    }

    public static class UserLoginErrorLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "error";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public UserLoginErrorLog(@AuthType String authType, String errorMessage)
        {
            super(EVENT_TYPE, authType);
            mErrorMessage = errorMessage;
        }
    }


    public static class UserContactShownLog extends BookingFunnelLog
    {
        private static final String EVENT_TYPE = "shown";

        public UserContactShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
