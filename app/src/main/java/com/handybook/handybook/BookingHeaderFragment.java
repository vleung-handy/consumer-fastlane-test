package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingHeaderFragment extends BookingFlowFragment implements Observer {

    private BookingTransaction transaction;
    private BookingQuote quote;

    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.time_text) TextView timeText;
    @InjectView(R.id.price_text) TextView priceText;

    static BookingHeaderFragment newInstance() {
        return new BookingHeaderFragment();
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transaction = bookingManager.getCurrentTransaction();
        quote = bookingManager.getCurrentQuote();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_header,container, false);

        ButterKnife.inject(this, view);
        refreshInfo();
        return view;
    }

    @Override
    final public void onStart() {
        super.onStart();
        transaction.addObserver(this);
        quote.addObserver(this);

    }

    @Override
    final public void onStop() {
        super.onStop();
        transaction.deleteObserver(this);
        quote.deleteObserver(this);
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof BookingQuote || observable instanceof BookingTransaction)
            refreshInfo();
    }

    private void refreshInfo() {
        final float hours = transaction.getHours() + transaction.getExtraHours();
        final Date startDate = transaction.getStartDate();

        dateText.setText(TextUtils.formatDate(startDate, "EEEE',' MMMM d"));

        timeText.setText(TextUtils.formatDate(startDate, "h:mm aaa") + " - "
                + TextUtils.formatDecimal(hours, "#.#")
                + " " + getString(R.string.hours));

        priceText.setText(TextUtils
                .formatPrice((quote.getPricing(hours, transaction.getRecurringFrequency())[0]),
                quote.getCurrencyChar(), quote.getCurrencySuffix()));
    }
}
