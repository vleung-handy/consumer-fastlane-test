package com.handybook.handybook.booking.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingService;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.StringUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by jtse on 2/17/16.
 */
public class BookingUtil
{

    private static final String TAG = BookingUtil.class.getName();

    //Service to Service Icon resource id mapping
    private static final Map<String, Integer> SERVICE_ICONS;
    private static final Map<String, Integer> SERVICE_OUTLINE_ICONS;
    private static final Map<String, Integer> PAST_SERVICE_ICONS;

    static
    {
        SERVICE_ICONS = new HashMap<>();
        SERVICE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_cleaner_fill);
        SERVICE_ICONS.put(Booking.SERVICE_CLEANING, R.drawable.ic_cleaner_fill);
        SERVICE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_handyman_fill);
        SERVICE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_painter_fill);
        SERVICE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_plumber_fill);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_electrician_fill);

        SERVICE_OUTLINE_ICONS = new HashMap<>();
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_service_cleaning_outline_small);
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_CLEANING, R.drawable.ic_service_cleaning_outline_small);
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_service_handyman_outline_small);
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_service_painting_outline_small);
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_service_plumbing_outline_small);
        SERVICE_OUTLINE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_service_electrical_outline_small);

        PAST_SERVICE_ICONS = new HashMap<>();
        PAST_SERVICE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_service_cleaning_outline_gray_small);
        PAST_SERVICE_ICONS.put(Booking.SERVICE_CLEANING, R.drawable.ic_service_cleaning_outline_gray_small);
        PAST_SERVICE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_service_handyman_outline_gray_small);
        PAST_SERVICE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_service_painting_outline_gray_small);
        PAST_SERVICE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_service_plumbing_outline_gray_small);
        PAST_SERVICE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_service_electrical_outline_gray_small);
    }

    private static final Integer DEFAULT_SERVICE_ICON_RESOURCE_ID = R.drawable.ic_cleaner_fill;
    private static final Integer DEFAULT_SERVICE_OUTLINE_ICON_RESOURCE_ID = R.drawable.ic_service_cleaning_outline_small;
    private static final Integer DEFAULT_PAST_SERVICE_ICON_RESOURCE_ID = R.drawable.ic_service_cleaning_outline_gray_small;


    /**
     * Given a booking, this returns the parent service machine name. ie., if light_fixtures is passed in,
     * the return will be: electrician
     *
     * @param booking
     * @param services
     * @return
     *
     * @deprecated - use getIconForService directly, as a booking should already include the parent service
     */
    public static String findParentService(Booking booking, @Nonnull List<Service> services)
    {

        Service rootService = null;

        for (Service service : services)
        {
            if (service.getUniq().equalsIgnoreCase(booking.getServiceMachineName()))
            {
                rootService = service;
                break;
            }
            else
            {
                if (service.getServices() != null)
                {
                    for (Service s : service.getServices())
                    {
                        Service match = findMatchingService(s, booking.getServiceMachineName());
                        if (match != null)
                        {
                            rootService = service;
                            break;
                        }
                    }
                }
            }
        }

        if (rootService == null)
        {
            Log.e(TAG, "This shouldn't happen, no service was found that matches the incoming booking: " + booking.getServiceMachineName());
            return null;
        }
        else
        {
            return rootService.getUniq();
        }
    }

    /**
     * Traverses the service tree to find the service node we're looking for using BFS
     *
     * @param nameToMatch
     * @return
     */
    public static Service findMatchingService(Service service, String nameToMatch)
    {
        if (service.getUniq().equalsIgnoreCase(nameToMatch))
        {
            return service;
        }
        else
        {
            if (service.getServices() != null)
            {
                for (Service s : service.getServices())
                {
                    Service match = findMatchingService(s, nameToMatch);
                    if (match != null)
                    {
                        return match;
                    }
                }
            }
        }

        //no match was found with service as the root.
        return null;
    }

    public static final String TITLE_DATE_FORMAT = "EEEE',' MMMM d";
    public static final SimpleDateFormat SUBTITLE_DATE_FORMAT = DateTimeUtils.CLOCK_FORMATTER_12HR;
    public static final int MINUTES_PER_HOUR = 60;

    public enum IconType
    {
        FILL, OUTLINE, GRAY
    }

    /**
     * Returns in the form of 3:00 pm - 7:00 pm
     *
     * @param booking
     * @param context
     * @return
     */
    public static String getSubtitle(Booking booking, Context context)
    {
        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String startDate = StringUtils.toLowerCase(DateTimeUtils.formatDate(
                booking.getStartDate(),
                SUBTITLE_DATE_FORMAT,
                booking.getBookingTimezone()
        ));

        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.getStartDate());
        cal.add(Calendar.MINUTE, Math.round(booking.getHours() * MINUTES_PER_HOUR)); // adds booking duration
        final Date endDate = cal.getTime();

        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String end = StringUtils.toLowerCase(DateTimeUtils.formatDate(endDate,
                                                                            SUBTITLE_DATE_FORMAT,
                                                                            booking.getBookingTimezone()
        ));

        return context.getString(
                R.string.booking_card_row_hours_formatted,
                startDate,
                end
        );
    }

    /*
    formats: "3.5 hours", "1 hour"
     */
    public static String getNumHoursDisplayString(float hours, Context context)
    {
        return TextUtils.formatDecimal(hours, "#.#") + " "
                + context.getResources().getQuantityString(R.plurals.hour, (int) Math.ceil(hours));

    }

    /**
     * used for the booking list view items
     * <p>
     * Returns in the form of 3:00 pm (up to 3 hours) if the hours clarification experiment is
     * enabled Otherwise returns in the form 3:00 pm - 6:00 pm
     *
     * @param booking
     * @param context
     * @return
     */
    public static String getSubtitle(
            Booking booking,
            Context context,
            boolean bookingHoursClarificationExperimentEnabled
    )
    {
        if (bookingHoursClarificationExperimentEnabled)
        {
            return getSubtitleForHoursClarificationExperiment(booking, context);
        }
        else
        {
            return getSubtitle(booking, context);
        }
    }

    /**
     * Returns in the form of 3:00 pm (up to 3 hours)
     *
     * @param booking
     * @param context
     * @return
     */
    public static String getSubtitleForHoursClarificationExperiment(
            Booking booking,
            Context context
    )
    {
        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String startTime = StringUtils.toLowerCase(DateTimeUtils.formatDate(
                booking.getStartDate(),
                SUBTITLE_DATE_FORMAT,
                booking.getBookingTimezone()
        ));

        float hours = booking.getHours();
        String hoursDisplayString = getNumHoursDisplayString(hours, context);
        return context.getString(
                R.string.booking_details_hours_clarification_experiment_hours_formatted,
                startTime,
                hoursDisplayString
        );
    }

    /**
     * Returns in the form of "Monday, August 15"
     *
     * @param booking
     * @return
     */
    public static String getTitle(Booking booking)
    {
        return TextUtils.formatDate(booking.getStartDate(), TITLE_DATE_FORMAT);
    }

    /**
     * Return in the form of Monday, Aug 1 @ 2:00pm
     *
     * @param rb
     * @return
     */
    public static String getRecurrenceSubTitle(RecurringBooking rb)
    {
        return DateTimeUtils.getDayShortMonthDay(rb.getDateStart()) + " @ "
                + DateTimeUtils.getTime(rb.getDateStart());
    }

    /**
     * Return in the form of 2:00pm - 3 hours
     *
     * @param rb
     * @return
     */
    public static String getRecurrenceSubTitle2(RecurringBooking rb)
    {
        return DateTimeUtils.getTime(rb.getDateStart()) + " - " + rb.getHours() + " hours";
    }

    public static Integer getIconForService(Booking booking, IconType iconType)
    {
        //if the service is a painting, then we have a special case, and we want to show the
        //painting icon. Otherwise, we'll go on and fetch the correct icon.
        String serviceMachineName = booking.getServiceMachineName();
        if (!serviceMachineName.equalsIgnoreCase("painting"))
        {

            BookingService service = booking.getService();

            //keep going deeper into the tree until there is no parent
            while (service.getParent() != null)
            {
                service = service.getParent();
            }

            serviceMachineName = service.getMachineName();
        }

        if (!android.text.TextUtils.isEmpty(serviceMachineName))
        {
            switch (iconType)
            {
                case FILL:
                    if (SERVICE_ICONS.containsKey(serviceMachineName))
                    {
                        return SERVICE_ICONS.get(serviceMachineName);
                    }
                    break;
                case OUTLINE:
                    if (SERVICE_OUTLINE_ICONS.containsKey(serviceMachineName))
                    {
                        return SERVICE_OUTLINE_ICONS.get(serviceMachineName);
                    }
                    break;
                case GRAY:
                    if (PAST_SERVICE_ICONS.containsKey(serviceMachineName))
                    {
                        return PAST_SERVICE_ICONS.get(serviceMachineName);
                    }
                    break;
            }
        }

        //if it gets to this point, nothing was found, so return the default icons
        switch (iconType)
        {
            case FILL:
                return DEFAULT_SERVICE_ICON_RESOURCE_ID;
            case OUTLINE:
                return DEFAULT_SERVICE_OUTLINE_ICON_RESOURCE_ID;
            case GRAY:
                return DEFAULT_PAST_SERVICE_ICON_RESOURCE_ID;
        }

        return DEFAULT_SERVICE_ICON_RESOURCE_ID;
    }

    public static void callPhoneNumber(final String phoneNumber, Context context)
    {
        if (android.text.TextUtils.isEmpty(phoneNumber))
        {
            return;
        }

        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", phoneNumber, null)), context);
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Calling a Phone Number failed", activityException));
        }
    }

    public static void textPhoneNumber(final String phoneNumber, Context context)
    {
        if (phoneNumber == null || phoneNumber.isEmpty())
        {
            return;
        }

        try
        {
            Utils.safeLaunchIntent(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)), context);
        }
        catch (ActivityNotFoundException activityException)
        {
            Crashlytics.logException(new RuntimeException("Texting a Phone Number failed", activityException));
        }
    }



}
