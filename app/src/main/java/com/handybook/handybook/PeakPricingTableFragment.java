package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PeakPricingTableFragment extends BookingFlowFragment {
    static final String EXTRA_PEAK_PRICE_INDEX = "com.handy.handy.EXTRA_PEAK_PRICE_INDEX";
    static final String EXTRA_PEAK_PRICE_TABLE = "com.handy.handy.EXTRA_PEAK_PRICE_TABLE";

    private int index;
    private ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakPriceTable;

    @InjectView(R.id.table_layout) LinearLayout tableLayout;

    static PeakPricingTableFragment newInstance(final int index,
                                                final ArrayList<ArrayList<BookingQuote
                                                        .PeakPriceInfo>> peakPriceTable) {
        final PeakPricingTableFragment fragment = new PeakPricingTableFragment();

        final Bundle args = new Bundle();
        args.putInt(EXTRA_PEAK_PRICE_INDEX, index);
        args.putSerializable(EXTRA_PEAK_PRICE_TABLE, peakPriceTable);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt(EXTRA_PEAK_PRICE_INDEX, 0);
        peakPriceTable = (ArrayList<ArrayList<BookingQuote.PeakPriceInfo>>)
                getArguments().getSerializable(EXTRA_PEAK_PRICE_TABLE);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View view = layoutInflater.inflate(R.layout.fragment_peak_pricing_table,
                container, false);

        ButterKnife.inject(this, view);

        final BookingQuote quote = bookingManager.getCurrentQuote();
        final ArrayList<BookingQuote.PeakPriceInfo> priceList
                = peakPriceTable.get(index);

        final String currChar = quote.getCurrencyChar();
        final String currSuffix = quote.getCurrencySuffix();

        final LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 0, 0, Utils.toDP(1, getActivity()));

        int i = 0;
        for (final BookingQuote.PeakPriceInfo info : priceList) {
            final View row = layoutInflater.inflate(R.layout.table_item_price, container, false);
            final TextView dateText = (TextView) row.findViewById(R.id.date_text);
            final TextView priceText = (TextView) row.findViewById(R.id.price_text);

            dateText.setText(TextUtils.formatDate(info.getDate(), "h:mm aaa"));
            priceText.setText(TextUtils.formatPrice(info.getPrice(), currChar, currSuffix));

            final int freq = bookingManager.getCurrentTransaction().getRecurringFrequency();
            final String type = info.getType();

            switch (type) {
                case "peak-price":
                    if (freq > 0) disableRow(row);
                    else priceText.setTextColor(getResources().getColor(R.color.error_red));
                    break;

                case "reg-price":
                    priceText.setTextColor(getResources().getColor(R.color.price_green));
                    if (freq > 0)
                        priceText.setText(getString(R.string.available));
                    break;

                default:
                    disableRow(row);
                    break;
            }

            if ((freq == 0 && type.equals("peak-price"))
                    || type.equals("reg-price")) {
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        quote.setStartDate(info.getDate());
                        continueBookingFlow();
                    }
                });
            }

            tableLayout.addView(row, i++, layoutParams);
        }

        return view;
    }

    private void disableRow(final View row) {
        final TextView dateText = (TextView) row.findViewById(R.id.date_text);
        final TextView priceText = (TextView) row.findViewById(R.id.price_text);
        priceText.setTextColor(getResources().getColor(R.color.black_pressed));
        priceText.setText(getString(R.string.unavailable));
        dateText.setTextColor(getResources().getColor(R.color.black_pressed));
    }
}
