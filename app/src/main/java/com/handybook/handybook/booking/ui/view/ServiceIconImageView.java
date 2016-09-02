package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.util.BookingUtil;

import java.util.List;

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

    public void updateServiceIconByBooking(Booking booking, List<Service> services)
    {
        Integer iconResourceId = BookingUtil.getIconForService(booking, BookingUtil.IconType.FILL);
        setImageResource(iconResourceId);
    }

}
