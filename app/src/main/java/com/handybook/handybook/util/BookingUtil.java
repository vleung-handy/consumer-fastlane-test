package com.handybook.handybook.util;

import android.util.Log;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;

import java.util.List;

/**
 * Created by jtse on 2/17/16.
 */
public class BookingUtil
{

    private static final String TAG = BookingUtil.class.getName();

    /**
     * Given a booking, this returns the parent service machine name. ie., if light_fixtures is passed in,
     * the return will be: electrician
     *
     * @param booking
     * @param services
     * @return
     */
    public static String findParentService(Booking booking, List<Service> services)
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
}
