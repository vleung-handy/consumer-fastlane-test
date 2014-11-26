package com.handybook.handybook;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

class Mixpanel {
    private MixpanelAPI mixpanel;
    private UserManager userManager;
    private BookingManager bookingManager;
    private Bus bus;
    private HashMap<String, Boolean> calledMap;

    @Inject
    Mixpanel(final Context context, final UserManager userManager,
             final BookingManager bookingManager, final Bus bus)   {
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            mixpanel = MixpanelAPI.getInstance(context, "864ccb52b900de546bb1bba717ab4fac");
        }
        else mixpanel = MixpanelAPI.getInstance(context, "5b31021d4a78ed7d57d9f19fd796f1cd");

        this.userManager = userManager;
        this.bookingManager = bookingManager;
        this.bus = bus;
        this.bus.register(this);
        this.calledMap = new HashMap<>();

        setSuperProps();
    }

    void flush() {
       mixpanel.flush();
   }

    private void setSuperProps() {
        mixpanel.clearSuperProperties();

        final JSONObject props = new JSONObject();
        addProps(props, "mobile", true);
        addProps(props, "impersonating", false);

        final User user = userManager.getCurrentUser();

        if (user == null) addProps(props, "user_logged_in", false);
        else {
            addProps(props, "user_logged_in", true);
            addProps(props, "name", user.getFullName());
            addProps(props, "email", user.getEmail());
            addProps(props, "user_id", user.getId());

            final User.Analytics analytics = user.getAnalytics();
            addProps(props, "last_booking_end", analytics.getLastBookingEnd());
            addProps(props, "partner", analytics.getPartner());
            addProps(props, "bookings", analytics.getBookings());
            addProps(props, "past_bookings_count", analytics.getPastBookings());
            addProps(props, "upcoming_bookings_count", analytics.getUpcomingBookings());
            addProps(props, "total_bookings_count", analytics.getTotalBookings());
            addProps(props, "recurring_bookings_count", analytics.getRecurringBookings());
            addProps(props, "provider", analytics.isProvider());
            addProps(props, "vip", analytics.isVip());
            addProps(props, "facebook_login", analytics.isFacebookLogin());
        }
        mixpanel.registerSuperProperties(props);
    }

    private void addProps(final JSONObject object, final String key, final Object value) {
        try { object.put(key, value); }
        catch (final JSONException e) { throw new RuntimeException(e); }
    }

    void trackEventWhenPage() {
        trackWhenPageEvents("when page");
    }

    void trackEventWhenPageSubmitted() {
        trackWhenPageEvents("when page submitted");
    }

    private void trackWhenPageEvents(final String event) {
        final Boolean called = calledMap.get(event);
        if (called != null && called) return;

        final JSONObject props = new JSONObject();
        final BookingRequest request = bookingManager.getCurrentRequest();
        String service = null, zip = null;

        if (request != null) {
            service = request.getUniq();
            zip = request.getZipCode();
        }

        addProps(props, "service", service);
        addProps(props, "booking_zipcode", zip);

        mixpanel.track(event, props);
        calledMap.put(event, true);
    }
    
    @Subscribe
    public final void bookingFlowCleared(final BookingFlowClearedEvent event) {
        calledMap = new HashMap<>();
    }
}
