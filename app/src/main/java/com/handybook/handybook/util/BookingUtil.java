package com.handybook.handybook.util;

import android.content.Context;
import android.util.Log;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;

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

    static
    {
        SERVICE_ICONS = new HashMap<>();
        //Cleaning
        SERVICE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_cleaner_fill);
        //Handyman
        SERVICE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_handyman_fill); //there are many handyman services, not sure how they all map
        SERVICE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_painter_fill);
        SERVICE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_plumber_fill);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_electrician_fill);
    }

    private static final Integer DEFAULT_SERVICE_ICON_RESOURCE_ID = R.drawable.ic_cleaner_fill;


    /**
     * Given a booking, this returns the parent service machine name. ie., if light_fixtures is passed in,
     * the return will be: electrician
     *
     * @param booking
     * @param services
     * @return
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
    public static final String SUBTITLE_DATE_FORMAT = "h:mm aaa";
    public static final int MINUTES_PER_HOUR = 60;
    public static final String DURATION_FORMAT = "#.#";

    public static String getSubtitle(Booking booking, Context context)
    {
        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String start = DateTimeUtils.formatDate(booking.getStartDate(),
                SUBTITLE_DATE_FORMAT, booking.getBookingTimezone());

        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.getStartDate());
        cal.add(Calendar.MINUTE, Math.round(booking.getHours() * MINUTES_PER_HOUR)); // adds booking duration
        final Date endDate = cal.getTime();

        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String end = DateTimeUtils.formatDate(endDate, SUBTITLE_DATE_FORMAT,
                booking.getBookingTimezone());

        final String duration = TextUtils.formatDecimal(booking.getHours(), DURATION_FORMAT);

        final String subtitle = context.getString(
                R.string.booking_card_row_time_and_duration,
                start,
                end,
                duration
        );
        return subtitle;
    }

    public static String getTitle(Booking booking)
    {
        return TextUtils.formatDate(booking.getStartDate(), TITLE_DATE_FORMAT);
    }


    public static Integer getIconForService(String serviceMachineName)
    {
        Integer iconResourceId = DEFAULT_SERVICE_ICON_RESOURCE_ID;
        if (serviceMachineName != null && !serviceMachineName.isEmpty())
        {
            if (SERVICE_ICONS.containsKey(serviceMachineName))
            {
                return SERVICE_ICONS.get(serviceMachineName);
            }
        }
        return iconResourceId;
    }


}
