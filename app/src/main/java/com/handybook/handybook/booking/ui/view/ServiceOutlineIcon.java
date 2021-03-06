package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;

import java.util.HashMap;
import java.util.Map;

public class ServiceOutlineIcon extends ImageView {

    public ServiceOutlineIcon(final Context context) {
        super(context);
    }

    public ServiceOutlineIcon(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ServiceOutlineIcon(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    //Service to Service Icon resource id mapping
    private static final Map<String, Integer> SERVICE_ICONS;

    static {
        SERVICE_ICONS = new HashMap<>();
        //Cleaning
        SERVICE_ICONS.put(Booking.SERVICE_CLEANING, R.drawable.ic_service_cleaning_outline);
        SERVICE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_service_cleaning_outline);
        SERVICE_ICONS.put(Booking.SERVICE_OFFICE_CLEANING, R.drawable.ic_service_cleaning_outline);
        //Handyman
        SERVICE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_service_handyman_outline);
        SERVICE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_service_painting_outline);
        SERVICE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_service_plumbing_outline);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICAL, R.drawable.ic_service_electrical_outline);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_service_electrical_outline);
    }

    private static final Integer DEFAULT_SERVICE_ICON_RESOURCE_ID
            = R.drawable.ic_service_handyman_outline;

    public void updateServiceIconByBooking(Booking booking) {
        Integer iconResourceId = getIconForService(booking.getServiceMachineName());
        setImageResource(iconResourceId);
    }

    private Integer getIconForService(String serviceMachineName) {
        Integer iconResourceId = DEFAULT_SERVICE_ICON_RESOURCE_ID;
        if (serviceMachineName != null && !serviceMachineName.isEmpty()) {
            if (SERVICE_ICONS.containsKey(serviceMachineName)) {
                return SERVICE_ICONS.get(serviceMachineName);
            }
        }
        return iconResourceId;
    }

}
