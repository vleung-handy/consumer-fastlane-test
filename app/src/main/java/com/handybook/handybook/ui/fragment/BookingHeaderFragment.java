package com.handybook.handybook.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingPricesForFrequenciesResponse;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.util.TextUtils;

import java.util.Date;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingHeaderFragment extends BookingFlowFragment implements Observer
{

    private BookingTransaction transaction;
    private BookingQuote quote;
    public static final String BOOKING_PRICES_FOR_FREQUENCIES_RESPONSE = "BOOKING_PRICES_FOR_FREQUENCIES_RESPONSE";

    @Bind(R.id.date_text)
    TextView dateText;
    @Bind(R.id.time_text)
    TextView timeText;
    @Bind(R.id.price_text)
    TextView priceText;
    @Bind(R.id.discount_text)
    TextView discountText;

    private Map<Integer, float[]> mPriceMap;
    private String mCurrChar;
    public static BookingHeaderFragment newInstance(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
    {
        BookingHeaderFragment fragment = new BookingHeaderFragment();
        Bundle args = new Bundle();
        args.putParcelable(BOOKING_PRICES_FOR_FREQUENCIES_RESPONSE, bookingPricesForFrequenciesResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        transaction = bookingManager.getCurrentTransaction();
        quote = bookingManager.getCurrentQuote();
        if (getArguments() != null)
        {
            BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse = getArguments().getParcelable(BOOKING_PRICES_FOR_FREQUENCIES_RESPONSE);
            mPriceMap = bookingPricesForFrequenciesResponse.getPriceMap();
            mCurrChar = bookingPricesForFrequenciesResponse.getCurrencyChar() + "";
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
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
        if (quote != null) //needed because this fragment is currently being reused by an edit fragment that does not have a BookingQuote reference
        {
            quote.addObserver(this);
        }
        refreshInfo();
    }

    @Override
    final public void onStop()
    {
        super.onStop();
        transaction.deleteObserver(this);
        if (quote != null)
        {
            quote.deleteObserver(this);
        }
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof BookingQuote || observable instanceof BookingTransaction)
        {
            refreshInfo();
        }
    }

    private void refreshInfo()
    {

        final float hours = transaction.getHours() + transaction.getExtraHours();
        final Date startDate = transaction.getStartDate();

        dateText.setText(TextUtils.formatDate(startDate, "EEEE',' MMMM d"));

        timeText.setText(TextUtils.formatDate(startDate, "h:mm aaa") + " - "
                + TextUtils.formatDecimal(hours, "#.#")
                + " " + getString(R.string.hours));


        if (quote != null)
        {
            final float[] pricing = quote.getPricing(hours, transaction.getRecurringFrequency());
            final String currChar = quote.getCurrencyChar();
            updatePriceAndDiscountText(pricing, currChar);
        }
        else
        {
            updatePriceAndDiscountText(mPriceMap.get(transaction.getRecurringFrequency()), mCurrChar);
        }
    }

    private void updatePriceAndDiscountText(final float[] pricing, String currChar)
    {
        if (pricing[0] == pricing[1])
        {
            priceText.setText(TextUtils.formatPrice(pricing[0], currChar, null));
            discountText.setVisibility(View.GONE);
        }
        else
        {
            priceText.setText(TextUtils.formatPrice(pricing[1], currChar, null));
            discountText.setText(TextUtils.formatPrice(pricing[0], currChar, null));
            discountText.setVisibility(View.VISIBLE);
        }
    }
}
