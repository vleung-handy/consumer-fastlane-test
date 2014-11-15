package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingHeaderFragment extends BookingFlowFragment {

    @InjectView(R.id.date_text) TextView dateText;
    @InjectView(R.id.time_text) TextView timeText;
    @InjectView(R.id.price_text) TextView priceText;

    static BookingHeaderFragment newInstance() {
        return new BookingHeaderFragment();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_header,container, false);

        ButterKnife.inject(this, view);

        final BookingQuote quote = bookingManager.getCurrentQuote();
        final BookingTransaction transaction = bookingManager.getCurrentTransaction();

        dateText.setText(TextUtils.formatDate(transaction.getStartDate(), "EEEE',' MMMM d"));

        timeText.setText(TextUtils.formatDate(transaction.getStartDate(), "h:mm aaa") + " - "
                + TextUtils.formatDecimal(transaction.getHours(), "#.#")
                + " " + getString(R.string.hours));

        priceText.setText(TextUtils.formatPrice(quote.getPriceTableMap()
                        .get(transaction.getHours()).getPrice(),
                quote.getCurrencyChar(), quote.getCurrencySuffix()));

        return view;
    }
}
