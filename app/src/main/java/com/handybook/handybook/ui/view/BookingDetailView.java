package com.handybook.handybook.ui.view;

import android.content.Context;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.InjectView;

public final class BookingDetailView extends InjectedRelativeLayout
{
    @InjectView(R.id.date_text)
    TextView dateText;
    @InjectView(R.id.time_text)
    TextView timeText;
    @InjectView(R.id.freq_text)
    TextView freqText;
    @InjectView(R.id.freq_layout)
    View freqLayout;
    @InjectView(R.id.pro_section)
    View proSection;
    @InjectView(R.id.pro_text)
    TextView proText;
    @InjectView(R.id.laundry_section)
    View laundrySection;
    @InjectView(R.id.entry_section)
    View entrySection;
    @InjectView(R.id.entry_text)
    TextView entryText;
    @InjectView(R.id.pro_note_section)
    View proNoteSection;
    @InjectView(R.id.pro_note_text)
    TextView proNoteText;
    @InjectView(R.id.extras_section)
    View extrasSection;
    @InjectView(R.id.extras_text)
    TextView extrasText;
    @InjectView(R.id.addr_text)
    TextView addrText;
    @InjectView(R.id.total_text)
    TextView totalText;
    @InjectView(R.id.pay_lines_section)
    LinearLayout paymentLinesSection;
    @InjectView(R.id.billed_text)
    TextView billedText;
    @InjectView(R.id.options_layout)
    View optionsLayout;
    @InjectView(R.id.reschedule_button)
    public Button rescheduleButton;
    @InjectView(R.id.cancel_button)
    public Button cancelButton;
    @InjectView(R.id.booking_text)
    TextView bookingText;
    @InjectView(R.id.nav_text)
    TextView navText;

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
        final ViewGroup container = this;

        navText.setText(booking.getService());
        bookingText.setText("Booking #" + booking.getId());

        updateDateTimeInfoText(booking);

        updateFrequencySectionDisplay(booking);

        updateProSectionDisplay(booking, user);

        updateLaundrySectionDisplay(booking);

        updateEntryInformation(booking);

        updateProNoteSectionDisplay(booking);

        updateExtraSectionDisplay(booking);

        updatePaymentDisplay(booking, user, container);

        final Booking.Address address = booking.getAddress();
        addrText.setText(TextUtils.formatAddress(address.getAddress1(), address.getAddress2(),
                address.getCity(), address.getState(), address.getZip()));

        if (booking.isPast())
        {
            optionsLayout.setVisibility(View.GONE);
        }
    }

    //TODO: don't like having an exception the fragment should talk to the view in as few ways as possible
    public void updateDateTimeInfoText(final Booking booking)
    {
        updateDateTimeInfoText(booking, booking.getStartDate());
    }

    public void updateDateTimeInfoText(final Booking booking, final Date startDate)
    {
        final float hours = booking.getHours();
        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        endDate.add(Calendar.HOUR, (int) hours);

        timeText.setText(TextUtils.formatDate(startDate, "h:mmaaa - ")
                + TextUtils.formatDate(endDate.getTime(), "h:mmaaa (") + TextUtils.formatDecimal(hours, "#.#") + " "
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


    private void updateProSectionDisplay(final Booking booking, final User user)
    {
        final Booking.Provider pro = booking.getProvider();
        if (pro.getStatus() == Booking.Provider.PROVIDER_STATUS_ASSIGNED)
        {
            proText.setText(pro.getFirstName() + " "
                    + pro.getLastName()
                    + (pro.getPhone() != null ? "\n"
                    + TextUtils.formatPhone(pro.getPhone(), user.getPhonePrefix()) : ""));
            Linkify.addLinks(proText, Linkify.PHONE_NUMBERS);
            TextUtils.stripUnderlines(proText);
        }
        else
        {
            proSection.setVisibility(View.GONE);
        }
    }

    private void updateLaundrySectionDisplay(final Booking booking)
    {
        if (booking.getLaundryStatus() == null
                || booking.getLaundryStatus() == Booking.LaundryStatus.SKIPPED)
        {
            laundrySection.setVisibility(View.GONE);
        }
    }

    private void updateEntryInformation(final Booking booking)
    {
        final String entryInfo = booking.getEntryInfo();
        if (entryInfo != null)
        {
            entryText.setText(entryInfo + " " + (booking.getExtraEntryInfo() != null ? booking.getExtraEntryInfo() : ""));
        }
        else
        {
            entrySection.setVisibility(View.GONE);
        }
    }

    private void updateProNoteSectionDisplay(final Booking booking)
    {
        final String proNote = booking.getProNote();
        if (proNote != null)
        {
            proNoteText.setText(proNote);
        }
        else
        {
            proNoteSection.setVisibility(View.GONE);
        }
    }

    private void updateExtraSectionDisplay(final Booking booking)
    {
        final ArrayList<Booking.ExtraInfo> extras = booking.getExtrasInfo();
        if (extras != null && extras.size() > 0)
        {
            String extraInfo = "";

            for (int i = 0; i < extras.size(); i++)
            {
                final Booking.ExtraInfo info = extras.get(i);
                extraInfo += info.getLabel();

                if (i < extras.size() - 1)
                {
                    extraInfo += ", ";
                }
            }

            extrasText.setText(extraInfo);
        }
        else
        {
            extrasSection.setVisibility(View.GONE);
        }
    }

    private void updatePaymentDisplay(final Booking booking, final User user, final ViewGroup container)
    {
        final String price = TextUtils.formatPrice(booking.getPrice(),
                user.getCurrencyChar(), null);
        totalText.setText(price);

        final ArrayList<Booking.LineItem> paymentInfo = booking.getPaymentInfo();
        Collections.sort(paymentInfo, new Comparator<Booking.LineItem>()
        {
            @Override
            public int compare(final Booking.LineItem lhs, final Booking.LineItem rhs)
            {
                return lhs.getOrder() - rhs.getOrder();
            }
        });

        if (paymentInfo != null && paymentInfo.size() > 0)
        {
            paymentLinesSection.setVisibility(View.VISIBLE);

            View paymentLineView;

            for (int i = 0; i < paymentInfo.size(); i++)
            {
                paymentLineView = inflate(R.layout.view_payment_line, container);

                final TextView labelText = (TextView) paymentLineView.findViewById(R.id.label_text);
                final TextView amountText = (TextView) paymentLineView.findViewById(R.id.amount_text);
                final Booking.LineItem line = paymentInfo.get(i);

                labelText.setText(line.getLabel());
                amountText.setText(line.getAmount());

                //Adjust padding for the payment line view for any that are not the last
                if (i < paymentInfo.size() - 1)
                {
                    paymentLineView.setPadding(0, 0, 0, Utils.toDP(10, getContext()));
                }

                paymentLinesSection.addView(paymentLineView);
            }
        }

        final String billedStatus = booking.getBilledStatus();
        if (billedStatus != null)
        {
            billedText.setText(billedStatus);
        }
        else
        {
            billedText.setVisibility(View.GONE);
        }
    }

}
