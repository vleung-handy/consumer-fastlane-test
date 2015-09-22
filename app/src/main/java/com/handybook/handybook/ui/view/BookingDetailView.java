package com.handybook.handybook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public final class BookingDetailView extends InjectedRelativeLayout
{
    @Bind(R.id.date_text)
    TextView dateText;
    @Bind(R.id.time_text)
    TextView timeText;
    @Bind(R.id.freq_text)
    TextView freqText;
    @Bind(R.id.freq_layout)
    View freqLayout;
    @Bind(R.id.action_buttons_layout)
    public LinearLayout actionButtonsLayout;
    @Bind(R.id.service_icon)
    ImageView serviceIcon;

    //TODO: Dynamically generated action buttons a la Portal allowed_actions
//    @Bind(R.id.reschedule_button)
//    public Button rescheduleButton;
//    @Bind(R.id.cancel_button)
//    public Button cancelButton;

    @Bind(R.id.booking_text)
    TextView bookingText;
    @Bind(R.id.nav_text)
    TextView navText;
    @Bind(R.id.section_fragment_container)
    public LinearLayout sectionFragmentContainer;
    @Bind(R.id.back_button)
    public ImageButton backButton;

    public BookingDetailView(final Context context)
    {
        super(context);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(final Booking booking, final User user)
    {
        navText.setText(booking.getServiceName());
        bookingText.setText(getContext().getString(R.string.booking_number) + booking.getId());

        updateDateTimeInfoText(booking);

        updateFrequencySectionDisplay(booking);

        updateServiceIcon(booking);

        if (booking.isPast())
        {
            actionButtonsLayout.setVisibility(View.GONE);
        }
    }

    //TODO: don't like having an exception the fragment should talk to the view in as few ways as possible, this view is going to be supplanted by new sub fragments
    public void updateDateTimeInfoText(final Booking booking)
    {
        updateDateTimeInfoText(booking, booking.getStartDate());
    }

    public void updateDateTimeInfoText(final Booking booking, final Date startDate)
    {
        final float hours = booking.getHours();
        final int minutes = (int) (60 * hours); //hours is a float may come back as something like 3.5, and can't add float hours to a calendar
        final Calendar endDate = Calendar.getInstance();

        endDate.setTime(startDate);
        endDate.add(Calendar.MINUTE, minutes);

        timeText.setText(TextUtils.formatDate(startDate, "h:mm aaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mm aaa (") + TextUtils.formatDecimal(hours, "#.#") + " "
                + getResources().getQuantityString(R.plurals.hour, (int) Math.ceil(hours)) + ")");

        dateText.setText(TextUtils.formatDate(startDate, "EEEE',' MMM d',' yyyy"));
    }

    private void updateFrequencySectionDisplay(final Booking booking)
    {
        final String recurringInfo = booking.getRecurringInfo();
        if (recurringInfo == null)
        {
            freqLayout.setVisibility(View.GONE);
        }
        else
        {
            freqText.setText(booking.getRecurringInfo());
        }
    }

    private void updateServiceIcon(Booking booking)
    {
        Integer iconResourceId = getIconForService(booking.getServiceMachineName());
        serviceIcon.setImageResource(iconResourceId);
    }

    //Service icon at top of page
    private static final Map<String, Integer> SERVICE_ICONS;
    static
    {
        SERVICE_ICONS = new HashMap<>();
        //Cleaning
        SERVICE_ICONS.put(Booking.SERVICE_CLEANING, R.drawable.ic_clean_fill);
        SERVICE_ICONS.put(Booking.SERVICE_HOME_CLEANING, R.drawable.ic_clean_fill);
        SERVICE_ICONS.put(Booking.SERVICE_OFFICE_CLEANING, R.drawable.ic_clean_fill);
        //Handyman
        SERVICE_ICONS.put(Booking.SERVICE_HANDYMAN, R.drawable.ic_handy_fill); //there are many handyman services, not sure how they all map
        SERVICE_ICONS.put(Booking.SERVICE_PAINTING, R.drawable.ic_paint_fill);
        SERVICE_ICONS.put(Booking.SERVICE_PLUMBING, R.drawable.ic_plumber_fill);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICAL, R.drawable.ic_elec_fill);
        SERVICE_ICONS.put(Booking.SERVICE_ELECTRICIAN, R.drawable.ic_elec_fill);
    }

    private static final Integer DEFAULT_SERVICE_ICON_RESOURCE_ID = R.drawable.ic_clean_fill;

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
