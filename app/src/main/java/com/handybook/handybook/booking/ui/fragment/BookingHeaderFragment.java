package com.handybook.handybook.booking.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.TextUtils;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingHeaderFragment extends BookingFlowFragment implements Observer
{

    private static final String TIME_FORMAT = "h:mm aaa";
    private static final String DATE_FORMAT = "EEEE',' MMMM d";
    private static final String DECIMAL_FORMAT = "#.#";

    private BookingTransaction transaction;
    private BookingQuote quote;

    @Bind(R.id.date_text)
    TextView dateText;
    @Bind(R.id.time_text)
    TextView timeText;
    @Bind(R.id.price_text)
    TextView priceText;
    @Bind(R.id.discount_text)
    TextView discountText;


    static BookingHeaderFragment newInstance()
    {
        return new BookingHeaderFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transaction = bookingManager.getCurrentTransaction();
        quote = bookingManager.getCurrentQuote();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(R.layout.fragment_booking_header, container, false);

        ButterKnife.bind(this, view);
        discountText.setPaintFlags(discountText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return view;
    }

    @Override
    final public void onStart()
    {
        super.onStart();
        transaction.addObserver(this);
        quote.addObserver(this);
        refreshInfo();
    }

    @Override
    final public void onStop()
    {
        super.onStop();
        transaction.deleteObserver(this);
        quote.deleteObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof BookingQuote || observable instanceof BookingTransaction)
        { refreshInfo(); }
    }

    private void refreshInfo()
    {
        final float hours = transaction.getHours() + transaction.getExtraHours();
        final Date startDate = transaction.getStartDate();

        String timeZone = null;
        if (bookingManager.getCurrentRequest() != null)
        {
            timeZone = bookingManager.getCurrentRequest().getTimeZone();
        }
        else
        {
            Crashlytics.logException(new RuntimeException(
                    "refreshInfo: bookingManager.getCurrentRequest() IS NULL!!!!"));
        }

        //we want to display the time using the booking location's time zone
        dateText.setText(DateTimeUtils.formatDate(startDate, DATE_FORMAT, timeZone));

        timeText.setText(DateTimeUtils.formatDate(startDate, TIME_FORMAT, timeZone)
                                 + " - "
                                 + TextUtils.formatDecimal(hours, DECIMAL_FORMAT)
                                 + " " + getString(R.string.hours)
        );

        final float[] pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
        final String currChar = quote.getCurrencyChar();

        priceText.setText(TextUtils.formatPrice(pricing[1], currChar, null));
        discountText.setText(TextUtils.formatPrice(pricing[0], currChar, null));
        discountText.setVisibility(pricing[1] < pricing[0] ? View.VISIBLE : View.GONE);
    }
}
