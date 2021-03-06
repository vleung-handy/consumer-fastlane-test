package com.handybook.handybook.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataManagerErrorHandler;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.util.PropertiesReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

/**
 * Created by cdavis on 4/20/15.
 */

public final class NavigationManager {

    //String consts
    private static final String WEB_AUTH_TOKEN = "slt=";
    private static final String WEB_PARAM_TOKEN = "?";
    private static final String WEB_ADDITIONAL_PARAM_TOKEN = "&";
    private static final String WEB_PARAM_DISABLE_MOBILE_SPLASH = "&disable_mobile_splash=1";

    //Injected params
    private UserManager userManager;
    private DataManager dataManager;
    private DataManagerErrorHandler dataManagerErrorHandler;

    //Valid Action Ids
    private static final String ACTION_ID_SERVICES = "services";
    private static final String ACTION_ID_RESCHEDULE = "reschedule_booking";
    private static final String ACTION_ID_CANCEL_BOOKING = "cancel_booking";
    private static final String ACTION_ID_RATE_PRO = "rate_confirmation";
    private static final String ACTION_ID_REQUEST_PRO = "request_pro";
    private static final String ACTION_ID_GO_TO_MY_BOOKINGS = "go_to_my_bookings";
    private static final String ACTION_ID_GO_TO_MY_PROFILE = "go_to_my_profile";
    private static final String ACTION_ID_GO_TO_REFERRALS = "go_to_referrals";
    private static final String ACTION_ID_EDIT_ENTRY_INFO = "edit_entry_info";
    private static final String ACTION_ID_EDIT_NOTE_TO_PRO = "edit_note_to_pro";
    private static final String ACTION_ID_EDIT_HOURS = "edit_hours";
    private static final String ACTION_ID_EDIT_FREQUENCY = "edit_frequency";
    private static final String ACTION_ID_GET_INVOICE = "get_invoice";
    private static final String ACTION_ID_ATTACH_LAUNDRY = "attach_laundry";
    private static final String ACTION_ID_SKIP_LAUNDRY = "skip_laundry";
    private static final String ACTION_ID_SCHEDULE_PICKUP = "schedule_pickup";
    private static final String ACTION_ID_SCHEDULE_DELIVERY = "schedule_delivery";
    private static final String ACTION_ID_SCHEDULE_CONFLICT = "schedule_conflict";
    private static final String ACTION_ID_MOVING_HELP = "book_moving_help";

    //Valid Deeplink ids
    private static final String DEEP_LINK_ID_SERVICES = "services/";
    private static final String DEEP_LINK_ID_PROFILE = "profile/";
    private static final String DEEP_LINK_ID_BOOKINGS = "bookings/";
    private static final String DEEP_LINK_ID_PROMOTIONS = "promotions/";
    private static final String DEEP_LINK_ID_BOOKINGS_RESCHEDULE = "bookings/reschedule";
    private static final String DEEP_LINK_ID_BOOKINGS_CANCEL = "bookings/cancel";

    //Action Id to Deeplink Id Mapping
    public static final Map<String, String> ACTION_ID_TO_DEEP_LINK_ID;

    static {
        Map<String, String> map = new HashMap<>();
        map.put(ACTION_ID_SERVICES, DEEP_LINK_ID_SERVICES);
        map.put(ACTION_ID_GO_TO_MY_PROFILE, DEEP_LINK_ID_PROFILE);
        map.put(ACTION_ID_GO_TO_MY_BOOKINGS, DEEP_LINK_ID_BOOKINGS);
        map.put(
                ACTION_ID_RATE_PRO,
                DEEP_LINK_ID_SERVICES
        ); //HACK: The services page will auto detect if a rating is required and direct user to that flow
        map.put(ACTION_ID_RESCHEDULE, DEEP_LINK_ID_BOOKINGS_RESCHEDULE);
        map.put(ACTION_ID_CANCEL_BOOKING, DEEP_LINK_ID_BOOKINGS_CANCEL);
        ACTION_ID_TO_DEEP_LINK_ID = Collections.unmodifiableMap(map);
    }

    //Member fields
    private final Properties config;
    private final Context context;

    //Move somewhere else

    public static final String PARAM_BOOKING_ID = "booking_id";

    ///////
    //Public
    ///////

    @Inject
    public NavigationManager(
            Context context,
            UserManager userManager,
            DataManager dataManager,
            DataManagerErrorHandler dataManagerErrorHandler
    ) {
        this.config = PropertiesReader.getProperties(context, "config.properties");
        this.context = context;
        this.userManager = userManager;
        this.dataManager = dataManager;
        this.dataManagerErrorHandler = dataManagerErrorHandler;
    }

    //Want to remove this once and make this functionality generic
    //Handle splash screen deep links, these may require additional callback functionality
    public void handleSplashScreenLaunch(Intent splashScreenIntent, BaseActivity callingActivity) {
        final String action = splashScreenIntent.getAction();
        final Uri data = splashScreenIntent.getData();

        if (!action.equals("android.intent.action.VIEW") || !data.getScheme().equals("handy")) {
            openServiceCategoriesActivity();
            return;
        }

        final String deepLinkId = data.getHost() + data.getPath();

        HashMap<String, String> params = new HashMap<>();
        if (deepLinkId.equals(DEEP_LINK_ID_BOOKINGS_RESCHEDULE) || deepLinkId.equals(
                DEEP_LINK_ID_BOOKINGS_CANCEL)) {
            String bookingId = data.getQueryParameter("booking_id");
            params.put(PARAM_BOOKING_ID, (bookingId != null ? bookingId : ""));
        }

        navigateToDeepLink(deepLinkId, params);
    }

    ///////
    //Private
    ///////

    private void navigateToDeepLink(String deepLinkId, Map<String, String> params) {
        //this log may be useful for debugging
        Crashlytics.log("NavigationManager::navigateToDeepLink with deepLinkId=" + deepLinkId);
        switch (deepLinkId) {
            case DEEP_LINK_ID_PROFILE: {
                openActivity(ProfileActivity.class, true);
            }
            break;
            case DEEP_LINK_ID_BOOKINGS: {
                openActivity(BookingsActivity.class, true);
            }
            break;
            case DEEP_LINK_ID_SERVICES: {
                openServiceCategoriesActivity();
            }
            break;
            case DEEP_LINK_ID_PROMOTIONS: {
                openActivity(PromosActivity.class, true);
            }
            break;

            case DEEP_LINK_ID_BOOKINGS_RESCHEDULE: {
                String bookingId = params.get(PARAM_BOOKING_ID);
                openBookingRequiredActivity(bookingId, BookingDateActivity.class);
            }
            break;

            case DEEP_LINK_ID_BOOKINGS_CANCEL: {
                String bookingId = params.get(PARAM_BOOKING_ID);
                openBookingRequiredActivity(bookingId, BookingCancelOptionsActivity.class);
            }
            break;

            default: {
                openServiceCategoriesActivity();
            }
        }
    }

    private void startActivity(final Intent intent) {
        //Don't clear the stack, currently designed to return to entry point on back
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);
    }

    //Open deep links
    private void openActivity(
            final Class<? extends Activity> targetClass,
            final boolean requiresUser
    ) {
        if (requiresUser && userManager.getCurrentUser() == null) {
            openServiceCategoriesActivity();
        }
        else {
            startActivity(new Intent(this.context, targetClass));
        }
    }

    private void openActivity(
            final Class<? extends Activity> targetClass,
            final HashMap<String, String> params
    ) {
        startActivity(new Intent(this.context, targetClass));
    }

    private void openServiceCategoriesActivity() {
        startActivity(new Intent(this.context, ServiceCategoriesActivity.class));
    }

    private void openBookingRequiredActivity(final String bookingId, final Class targetClass) {
        final Context intentContext = this.context;
        dataManager.getBooking(
                bookingId,
                new DataManager.Callback<Booking>() {
                    @Override
                    public void onSuccess(final Booking booking) {
                        onBookingRetrieved(booking, context, targetClass);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        onBookingDataError(error, intentContext);
                    }
                }
        );
    }

    //Not necessary yet, but we could implement deeplink classes that implement their onBookingRetrieved instead of adding if statements
    private void onBookingRetrieved(
            final Booking booking,
            final Context intentContext,
            Class targetClass
    ) {
        if (targetClass == BookingCancelOptionsActivity.class) {
            dataManager.getBookingCancellationData(
                    booking.getId(),
                    new DataManager.Callback<BookingCancellationData>() {
                        @Override
                        public void onSuccess(final BookingCancellationData bcd) {
                            final Intent intent = new Intent(
                                    context,
                                    BookingCancelOptionsActivity.class
                            );
                            intent.putExtra(BundleKeys.BOOKING, booking);
                            intent.putExtra(BundleKeys.BOOKING_CANCELLATION_DATA, bcd);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            onBookingDataError(error, intentContext);
                        }
                    }
            );
        }
        else if (targetClass == BookingDateActivity.class) {
            dataManager.getPreRescheduleInfo(
                    booking.getId(),
                    new DataManager.Callback<String>() {
                        @Override
                        public void onSuccess(final String notice) {
                            final Intent intent = new Intent(
                                    intentContext,
                                    BookingDateActivity.class
                            );
                            intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, booking);
                            intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, notice);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            onBookingDataError(error, intentContext);
                        }
                    }
            );
        }
    }

    private void onBookingDataError(
            final DataManager.DataManagerError error,
            final Context intentContext
    ) {
        dataManagerErrorHandler.handleError(intentContext, error);
        openServiceCategoriesActivity();
    }
}
