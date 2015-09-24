package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.annotation.TrackField;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.BookingFlowClearedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
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
        } else
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
        // UPGRADE: This is a change, previously we talked to the UserManager, make sure that the
        // prefs user_obj is always updated properly in secureprefs
        final User user = User.fromJson(prefsManager.getString(PrefsKey.USER));
        if (user == null)
        {
            addProps(props, "user_logged_in", false);
        } else
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

    public void trackOnboardingShown()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "type", "default");
        mixpanelAPI.track("Onboarding Show", props);
    }

    public void trackOnboardingActionLogin(final int page)
    {
        trackOnboardingActions("login", page);
    }

    public void trackOnboardingActionSkip(final int page)
    {
        trackOnboardingActions("skip", page);
    }

    private void trackOnboardingActions(final String action, final int page)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "type", "default");
        addProps(props, "action", action);
        addProps(props, "page", page);
        mixpanelAPI.track("Onboarding Action", props);
    }

    public void trackPageLogin()
    {
        mixpanelAPI.track("log in page view", null);
    }

    public void trackEventLoginSuccess(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanelAPI.track("log in successful", props);
    }

    public void trackEventLoginFailure(final LoginType type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "log_in_type", type.getValue());
        mixpanelAPI.track("log in failure", props);
    }

    public void trackEventWhenPage(BookingRequest request)
    {
        trackWhenPageEvents("when page", request);
    }

    public void trackEventWhenPageSubmitted(BookingRequest request)
    {
        trackWhenPageEvents("when page submitted", request);
    }

    public void trackEventPaymentPage(
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction
    )
    {
        final String event = "payment page";
        final Boolean called = calledMap.get(event);
        if (called != null && called)
        {
            return;
        }

        final JSONObject props = new JSONObject();
        addPaymentFlowProps(props, request, quote, transaction);

        mixpanelAPI.track(event, props);
        calledMap.put(event, true);
    }

    public void trackEventSubmitPayment(
            BookingRequest request,
            BookingQuote quote,
            BookingTransaction transaction
    )
    {
        final String event = "submit payment";
        final Boolean called = calledMap.get(event);
        if (called != null && called)
        {
            return;
        }

        final JSONObject props = new JSONObject();
        addSubmitPaymentFlowProps(props, request, quote, transaction);

        mixpanelAPI.track(event, props);
        calledMap.put(event, true);
    }

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

    public void trackEventYozioInstall(final HashMap<String, Object> metaData)
    {
        final JSONObject props = new JSONObject();
        addProps(props, metaData);
        mixpanelAPI.track("Yozio Install", props);
    }

    public void trackEventYozioOpen(final HashMap<String, Object> metaData)
    {
        final JSONObject props = new JSONObject();
        addProps(props, metaData);
        mixpanelAPI.track("Yozio Open", props);
    }

    public void trackEventFirstTimeUse()
    {
        mixpanelAPI.track("first time use", null);
    }

    public void trackEventAppOpened(final boolean newOpen)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "new_open", newOpen);
        mixpanelAPI.track("app had been opened", props);
    }

    public void trackEventHelpCenterOpened()
    {
        final JSONObject props = new JSONObject();
        mixpanelAPI.track("ssc_open", props);
    }

    public void trackEventHelpCenterNavigation(final String location)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "selection", location);
        mixpanelAPI.track("ssc_navigation_enter", props);
    }

    public void trackEventHelpCenterLeaf(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanelAPI.track("ssc_enter_leaf", props);
    }

    public void trackEventHelpCenterNeedHelpClicked(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanelAPI.track("ssc_still_need_help_clicked", props);
    }

    public void trackEventHelpCenterSubmitTicket(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanelAPI.track("ssc_submit_ticket", props);
    }

    public void trackEventHelpCenterDeepLinkClicked(final String nodeId, final String label)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "node_id", nodeId);
        addProps(props, "label", label);
        mixpanelAPI.track("ssc_deep_link_clicked", props);
    }

    public void trackEventProRate(final ProRateEventType type, final int bookingId,
            final String proName, final int rating)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "dialog_event", type.getValue());
        addProps(props, "booking_id", bookingId);
        addProps(props, "provider_name", proName);
        addProps(props, "rating_range", "1 to 5");

        if (type == ProRateEventType.SUBMIT)
        {
            addProps(props, "app_rating", rating);
        }

        mixpanelAPI.track("app pro rate event", props);
    }

    public void trackPageAddLaundryIntro(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanelAPI.track("show add laundry intro page", props);
    }

    public void trackPageAddLaundryConfirm(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanelAPI.track("show add laundry confirm page", props);
    }

    public void trackEventLaundryAdded(final LaundryEventSource source)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        mixpanelAPI.track("submit add laundry confirm page", props);
    }

    public void trackPageScheduleLaundry(final LaundryEventSource source, final String type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        addProps(props, "type", type);
        mixpanelAPI.track("show schedule laundry page", props);
    }

    public void trackEventLaundryScheduled(final LaundryEventSource source, final String type)
    {
        final JSONObject props = new JSONObject();
        addProps(props, "source", source.getValue());
        addProps(props, "type", type);
        mixpanelAPI.track("submit schedule laundry page", props);
    }

    // APP_TRACK_INSTALL ("App Track Install"),
    public void trackEventAppTrackInstall()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Install");
    }

    // APP_TRACK_LOCATION ("App Track Location"),
    public void trackEventAppTrackLocation()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Location");
    }

    // APP_TRACK_DETAILS ("App Track Details"),
    public void trackEventAppTrackDetails()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Details");
    }

    // APP_TRACK_TIME ("App Track Time"),
    public void trackEventAppTrackTime()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Time");
    }

    // APP_TRACK_CONTACT ("App Track Contact"),
    public void trackEventAppTrackContact()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("");
    }

    // APP_TRACK_LOG_IN ("App Track Log In"),
    public void trackEventAppTrackLogIn()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("");
    }

    // APP_TRACK_REQUEST_PRO ("App Track Request Pro"),
    public void trackEventAppTrackRequestPro()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Request Pro");
    }

    // APP_TRACK_COMMENTS ("App Track Comments"),
    public void trackEventAppTrackComments()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Comments");
    }

    // APP_TRACK_FREQUENCY ("App Track Frequency"),
    public void trackEventAppTrackFrequency()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Frequency");
    }

    // APP_TRACK_EXTRAS ("App Track Extras"),
    public void trackEventAppTrackExtras()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Extras");
    }

    // APP_TRACK_ADDRESS ("App Track Address"),
    public void trackEventAppTrackAddress()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Address");
    }

    // APP_TRACK_PAYMENT ("App Track Payment"),
    public void trackEventAppTrackPayment()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Payment");
    }

    // APP_TRACK_CONFIRMATION ("App Track Confirmation");
    public void trackEventAppTrackConfirmation()
    {
        final JSONObject props = new JSONObject();
        addProps(props, "device", "Android");
        mixpanelAPI.track("App Track Confirmation");
    }


    private void addProps(final JSONObject object, final String key, final Object value)
    {
        try
        {
            object.put(key, value);
        } catch (final JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void addProps(final JSONObject object, final HashMap<String, Object> props)
    {
        try
        {
            for (final String key : props.keySet())
            {
                object.put(key, props.get(key));
            }
        } catch (final JSONException e)
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
            } else
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
                } catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                addProps(object, key, value);
            }
        }
        return object;
    }


}
