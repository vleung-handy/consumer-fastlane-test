package com.handybook.handybook.core;

import android.app.Activity;

import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditAddressActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditExtrasActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditPreferencesActivity;
import com.handybook.handybook.booking.ui.activity.BookingAddressActivity;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.BookingLocationActivity;
import com.handybook.handybook.booking.ui.activity.BookingOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingPaymentActivity;
import com.handybook.handybook.booking.ui.activity.BookingRecurrenceActivity;
import com.handybook.handybook.booking.ui.activity.BookingRescheduleOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.CancelRecurringBookingActivity;
import com.handybook.handybook.booking.ui.activity.PeakPricingActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.activity.BlockingActivity;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.OldDeeplinkSplashActivity;
import com.handybook.handybook.core.ui.activity.UpdatePaymentActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.notifications.ui.activity.NotificationsActivity;
import com.handybook.handybook.onboarding.OnboardActivity;
import com.handybook.handybook.referral.ui.RedemptionActivity;
import com.handybook.handybook.referral.ui.ReferralActivity;

import java.util.HashMap;

/**
 * ScreenName will map app locations to their names analytics purposes
 */
public class ScreenName {

    private static final HashMap<String, String> sScreenNames = new HashMap<>();

    static {
        sScreenNames.put(BookingEditAddressActivity.class.getCanonicalName(), "Edit: Address");
        sScreenNames.put(
                BookingEditEntryInformationActivity.class.getCanonicalName(),
                "Edit: EntryInformation"
        );
        sScreenNames.put(BookingEditExtrasActivity.class.getCanonicalName(), "Edit: Extras");
        sScreenNames.put(BookingEditHoursActivity.class.getCanonicalName(), "Edit: Hours");
        sScreenNames.put(
                BookingEditPreferencesActivity.class.getCanonicalName(),
                "Edit: Preferences"
        );
        sScreenNames.put(BookingAddressActivity.class.getCanonicalName(), "Booking: Address");
        sScreenNames.put(
                BookingCancelOptionsActivity.class.getCanonicalName(),
                "Booking: CancelOptions"
        );
        sScreenNames.put(BookingDateActivity.class.getCanonicalName(), "Booking: Date");
        sScreenNames.put(BookingDetailActivity.class.getCanonicalName(), "Booking: Detail");
        sScreenNames.put(BookingExtrasActivity.class.getCanonicalName(), "Booking: Extras");
        sScreenNames.put(BookingFinalizeActivity.class.getCanonicalName(), "Booking: Finalize");
        sScreenNames.put(BookingLocationActivity.class.getCanonicalName(), "Booking: Location");
        sScreenNames.put(BookingOptionsActivity.class.getCanonicalName(), "Booking: Options");
        sScreenNames.put(BookingPaymentActivity.class.getCanonicalName(), "Booking: Payment");
        sScreenNames.put(BookingRecurrenceActivity.class.getCanonicalName(), "Booking: Recurrence");
        sScreenNames.put(
                BookingRescheduleOptionsActivity.class.getCanonicalName(),
                "Booking: RescheduleOptions"
        );
        sScreenNames.put(BookingsActivity.class.getCanonicalName(), "Bookings");
        sScreenNames.put(
                CancelRecurringBookingActivity.class.getCanonicalName(),
                "Cancel Recurring"
        );
        sScreenNames.put(PeakPricingActivity.class.getCanonicalName(), "Booking: Peak Pricing");
        sScreenNames.put(PromosActivity.class.getCanonicalName(), "Promos");
        sScreenNames.put(ServiceCategoriesActivity.class.getCanonicalName(), "Service Categories");
        sScreenNames.put(ServicesActivity.class.getCanonicalName(), "Services");
        sScreenNames.put(HelpActivity.class.getCanonicalName(), "Help");
        sScreenNames.put(NotificationsActivity.class.getCanonicalName(), "Notifications");
        sScreenNames.put(RedemptionActivity.class.getCanonicalName(), "Redemption");
        sScreenNames.put(ReferralActivity.class.getCanonicalName(), "Referral");
        sScreenNames.put(BaseActivity.class.getCanonicalName(), "Base Activity");
        sScreenNames.put(BlockingActivity.class.getCanonicalName(), "Blocking Screen");
        sScreenNames.put(LoginActivity.class.getCanonicalName(), "Login");
        sScreenNames.put(OnboardActivity.class.getCanonicalName(), "Onboard");
        sScreenNames.put(ProfileActivity.class.getCanonicalName(), "Profile");
        sScreenNames.put(OldDeeplinkSplashActivity.class.getCanonicalName(), "Splash");
        sScreenNames.put(UpdatePaymentActivity.class.getCanonicalName(), "Update Payment");


    }


    /**
     * Returns defined name for provided activity or it's canonical name by default
     *
     * @param activity The activity whose name we will provide
     * @return the name defined for activity or it's canonical name
     */
    public static String from(final Activity activity) {
        final String canonicalName = activity.getClass().getCanonicalName();
        final String screenName = sScreenNames.get(canonicalName);
        if (screenName == null) {
            return canonicalName;
        }
        return screenName;
    }
}
