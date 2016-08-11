package com.handybook.handybook.logger.mixpanel;

import android.content.Context;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.BookingFlowClearedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.logger.mixpanel.annotation.Track;
import com.handybook.handybook.logger.mixpanel.annotation.TrackField;
import com.handybook.handybook.manager.PrefsManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

import javax.inject.Inject;

public class Mixpanel
{
    private MixpanelAPI mixpanelAPI;
    private PrefsManager prefsManager;
    private HashMap<String, Boolean> calledMap;


    @Inject
    public Mixpanel(final Context context,
                    final PrefsManager prefsManager)
    {
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            mixpanelAPI = MixpanelAPI.getInstance(context, "864ccb52b900de546bb1bba717ab4fac");
        }
        else
        {
            mixpanelAPI = MixpanelAPI.getInstance(context, "5b31021d4a78ed7d57d9f19fd796f1cd");
        }
        this.calledMap = new HashMap<>();
        this.prefsManager = prefsManager;
        setSuperProps();
    }

    public void flush()
    {
        mixpanelAPI.flush();
    }

    private void setSuperProps()
    {
        mixpanelAPI.clearSuperProperties();
        final JSONObject props = new JSONObject();
        addProps(props, "mobile", true);
        addProps(props, "client", "android");
        addProps(props, "impersonating", false);
        //UPGRADE: This is a change, previously we talked to the UserManager, make sure that the prefs user_obj is always updated properly in secureprefs
        String userJson = prefsManager.getString(PrefsKey.USER);
        User user = null;
        if (userJson != null)
        {
            user = User.fromJson(userJson);
        }

        if (user == null)
        {
            addProps(props, "user_logged_in", false);
        }
        else
        {
            addProps(props, "user_logged_in", true);
            addProps(props, "name", user.getFullName());
            addProps(props, "email", user.getEmail());
            addProps(props, "user_id", user.getId());
            final User.Analytics analytics = user.getAnalytics();
            if (analytics != null)
            {
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
        }
        mixpanelAPI.registerSuperProperties(props);
    }


    //TODO dupe to internal logger
    public void trackEventLoginSuccess(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanelAPI.track("log in successful", props);
    }
    //TODO dupe to internal logger
    public void trackEventLoginFailure(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanelAPI.track("log in failure", props);
    }

    //TODO dupe to internal logger
    public void trackEventWhenPage(BookingRequest request)
    {
        trackWhenPageEvents("when page", request);
    }

    //TODO dupe to internal logger
    public void trackEventWhenPageSubmitted(BookingRequest request)
    {
        trackWhenPageEvents("when page submitted", request);
    }
    //TODO dupe to internal logger
    public void trackEventBookingMade(
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction)
    {
        final String event = "booking made";
        final Boolean called = calledMap.get(event);
        if (called != null && called)
        {
            return;
        }
        final JSONObject props = new JSONObject();
        addBookingMadeFlowProps(props, request, quote, transaction);
        mixpanelAPI.track(event, props);
        calledMap.put(event, true);
    }

    //TODO dupe to internal logger
    public void trackEventFirstTimeUse()
    {
        mixpanelAPI.track("first time use", null);
    }

    //TODO dupe to internal logger
    public void trackEventAppOpened(final boolean newOpen)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanelAPI.track("app had been opened", props);
    }

    private void addProps(final JSONObject object, final String key, final Object value)
    {
        try
        {
            if (value instanceof Collection)
            {
                JSONArray array = new JSONArray((Collection) value);
                object.put(key, array);
            }
            else
            {
                object.put(key, value);
            }
        }
        catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void trackWhenPageEvents(final String event, BookingRequest request)
    {
        final Boolean called = calledMap.get(event);
        if (called != null && called)
        {
            return;
        }
        final JSONObject props = new JSONObject();
        addWhenFlowProps(props, request);
        mixpanelAPI.track(event, props);
        calledMap.put(event, true);
    }

    private void addWhenFlowProps(final JSONObject props, BookingRequest request)
    {
        //final BookingRequest request = bookingManager.getCurrentRequest();
        String service = null, zip = null;
        if (request != null)
        {
            service = request.getUniq();
            zip = request.getZipCode();
        }
        addProps(props, "service", service);
        addProps(props, "booking_zipcode", zip);
    }

    private void addPaymentFlowProps(
            final JSONObject props,
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction
    )
    {
        addWhenFlowProps(props, request);
        String email = null;
        int bookingId = 0, repeatFreq = 0;
        float hours = 0, price = 0;
        boolean hasDynamicPricing = false, isRepeat = false;
        if (request != null)
        {
            email = request.getEmail();
        }
        if (quote != null)
        {
            bookingId = quote.getBookingId();
            hours = quote.getHours();
            hasDynamicPricing = quote.getSurgePriceTable() != null;
        }
        if (transaction != null)
        {
            repeatFreq = transaction.getRecurringFrequency();
            isRepeat = repeatFreq > 0;
            if (quote != null)
            {
                price = quote.getPricing(hours, repeatFreq)[0];
            }
        }
        addProps(props, "booking_id", bookingId);
        addProps(props, "hours", hours);
        addProps(props, "email", email);
        addProps(props, "price_before_discount", price);
        addProps(props, "repeat", isRepeat);
        addProps(props, "dynamic_price", hasDynamicPricing);
        if (repeatFreq > 0)
        {
            addProps(props, "repeat_freq", repeatFreq);
        }
    }

    private void addSubmitPaymentFlowProps(
            final JSONObject props,
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction)
    {
        addPaymentFlowProps(props, request, quote, transaction);
        String cleaningExtras = null;
        boolean cleaningExtrasSelected = false;
        float hours = 0;
        if (transaction != null)
        {
            cleaningExtras = transaction.getExtraCleaningText();
            if (cleaningExtras != null)
            {
                final String[] extrasList = cleaningExtras.split(",");
                if (extrasList.length > 0)
                {
                    cleaningExtrasSelected = true;
                }
            }
            hours = transaction.getExtraHours();
        }
        addProps(props, "cleaning_extras_tapped", cleaningExtrasSelected);
        if (cleaningExtrasSelected)
        {
            addProps(props, "extra_hours", hours);
        }
        if (cleaningExtrasSelected)
        {
            addProps(props, "extras", cleaningExtras);
        }
    }

    private void addBookingMadeFlowProps(
            final JSONObject props,
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction
    )
    {
        addSubmitPaymentFlowProps(props, request, quote, transaction);
        float hourlyPrice = 0, totalPrice = 0;
        boolean isRepeating = false;
        if (quote != null)
        {
            hourlyPrice = quote.getHourlyAmount();
        }
        if (transaction != null)
        {
            isRepeating = transaction.getRecurringFrequency() > 0;

            final float hours = transaction.getHours() + transaction.getExtraHours();
            float[] pricing = new float[]{0, 0};
            if (quote != null)
            {
                pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
            }
            if (pricing[0] == pricing[1])
            {
                totalPrice = pricing[0];
            }
            else
            {
                totalPrice = pricing[1];
            }
        }

        addProps(props, "price_per_hour", hourlyPrice);
        addProps(props, "charge", totalPrice);
        addProps(props, "converted_to_repeat", isRepeating);
    }

    @Subscribe
    public final void userAuthUpdated(final UserLoggedInEvent event)
    {
        setSuperProps();
    }

    @Subscribe
    public final void bookingFlowCleared(final BookingFlowClearedEvent event)
    {
        calledMap = new HashMap<>();
        setSuperProps();
    }

    public enum LoginType
    {
        EMAIL("email/password"), FACEBOOK("facebook");
        private String value;

        private LoginType(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }


    public enum ProRateEventType
    {
        SHOW("show"), SUBMIT("submit");
        private String value;

        private ProRateEventType(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }


    public enum LaundryEventSource
    {
        APP_OPEN("app_open");
        private String value;

        private LaundryEventSource(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }


    //UPGRADE: Bringing over from Nortal to track events
    public void track(String eventName)
    {
        track(eventName, null);
    }

    public void trackEvent(Object event)
    {
        Class eventClass = event.getClass();
        if (eventClass.isAnnotationPresent(Track.class))
        {
            Track annotation = (Track) eventClass.getAnnotation(Track.class);
            String message = annotation.value();
            getItemsToTrack(eventClass);
            track(message, getItemsToTrack(event));
        }
    }

    private void track(String eventName, JSONObject object)
    {
        mixpanelAPI.track(eventName, object);
    }

    private JSONObject getItemsToTrack(Object event)
    {
        Class eventClass = event.getClass();
        JSONObject object = new JSONObject();
        for (Field field : eventClass.getDeclaredFields())
        {
            if (field.isAnnotationPresent(TrackField.class))
            {
                TrackField annotation = field.getAnnotation(TrackField.class);
                String key = annotation.value();
                Object value = null;
                field.setAccessible(true);
                try
                {
                    value = field.get(event);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                addProps(object, key, value);
            }
        }
        return object;
    }


}
