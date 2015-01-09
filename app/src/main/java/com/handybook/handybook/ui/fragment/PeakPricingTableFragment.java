package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.User;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PeakPricingTableFragment extends BookingFlowFragment {
    static final String EXTRA_PEAK_PRICE_INDEX = "com.handy.handy.EXTRA_PEAK_PRICE_INDEX";
    static final String EXTRA_PEAK_PRICE_TABLE = "com.handy.handy.EXTRA_PEAK_PRICE_TABLE";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_ALL = "com.handy.handy.EXTRA_RESCHEDULE_ALL";

    private int index;
    private ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakPriceTable;
    private Booking rescheduleBooking;
    private boolean forReschedule;
    private boolean rescheduleAll;

    @InjectView(R.id.table_layout) LinearLayout tableLayout;

    public static PeakPricingTableFragment newInstance(final int index,
                                                final ArrayList<ArrayList<BookingQuote
                                                        .PeakPriceInfo>> peakPriceTable,
                                                final Booking rescheduleBooking,
                                                final boolean rescheduleAll) {
        final PeakPricingTableFragment fragment = new PeakPricingTableFragment();

        final Bundle args = new Bundle();
        args.putInt(EXTRA_PEAK_PRICE_INDEX, index);
        args.putSerializable(EXTRA_PEAK_PRICE_TABLE, peakPriceTable);
        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, rescheduleBooking);
        args.putBoolean(EXTRA_RESCHEDULE_ALL, rescheduleAll);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt(EXTRA_PEAK_PRICE_INDEX, 0);

        peakPriceTable = (ArrayList<ArrayList<BookingQuote.PeakPriceInfo>>)
                getArguments().getSerializable(EXTRA_PEAK_PRICE_TABLE);

        rescheduleBooking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        rescheduleAll = getArguments().getBoolean(EXTRA_RESCHEDULE_ALL);
        forReschedule = rescheduleBooking != null;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View view = layoutInflater.inflate(R.layout.fragment_peak_pricing_table,
                container, false);

        ButterKnife.inject(this, view);

        final BookingQuote quote = bookingManager.getCurrentQuote();
        final User user = userManager.getCurrentUser();
        final ArrayList<BookingQuote.PeakPriceInfo> priceList
                = peakPriceTable.get(index);

        final String currChar = forReschedule ? user.getCurrencyChar()
                : quote.getCurrencyChar();

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
            priceText.setText(TextUtils.formatPrice(info.getPrice(), currChar, null));

            final int freq = forReschedule ? -1
                    : bookingManager.getCurrentTransaction().getRecurringFrequency();

            final String type = info.getType();

            switch (type) {
                case "peak-price":
                    if (freq > 0 || forReschedule) disableRow(row);
                    else priceText.setTextColor(getResources().getColor(R.color.error_red));
                    break;

                case "reg-price":
                    priceText.setTextColor(getResources().getColor(R.color.price_green));
                    if (freq > 0 || forReschedule)
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
                        if (forReschedule) {
                            rescheduleBooking(rescheduleBooking, info.getDate(), rescheduleAll);
                        }
                        else {
                            quote.setStartDate(info.getDate());
                            continueBookingFlow();
                        }
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