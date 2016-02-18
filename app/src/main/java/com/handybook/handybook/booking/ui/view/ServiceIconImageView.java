package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.util.BookingUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cdavis on 9/22/15.
 */
public class ServiceIconImageView extends ImageView
{
    public ServiceIconImageView(final Context context) {
        super(context);
    }

    public ServiceIconImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ServiceIconImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

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

    public void updateServiceIconByBooking(Booking booking, List<Service> services)
    {
        Integer iconResourceId = getIconForService(BookingUtil.findParentService(booking, services));
        setImageResource(iconResourceId);
    }

    private Integer getIconForService(String serviceMachineName)
    {
        Integer iconResourceId = DEFAULT_SERVICE_ICON_RESOURCE_ID;
        if(serviceMachineName != null && !serviceMachineName.isEmpty())
        {
            if (SERVICE_ICONS.containsKey(serviceMachineName))
            {
                return SERVICE_ICONS.get(serviceMachineName);
            }
        }
        return iconResourceId;
    }

}
