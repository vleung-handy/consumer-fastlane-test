package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PeakPricingFragment extends BookingFlowFragment {
    private ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> peakPriceTable;

    @InjectView(R.id.skip_button) Button skipButton;
    @InjectView(R.id.date_text) TextView dateText;

    static PeakPricingFragment newInstance() {
        final PeakPricingFragment fragment = new PeakPricingFragment();
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peakPriceTable = bookingManager.getCurrentQuote().getPeakPriceTable();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_peak_pricing,container, false);

        ButterKnife.inject(this, view);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showBookingAddress();
            }
        });

        dateText.setText(TextUtils.formatDate(peakPriceTable
                .get(getFirstRegPriceIndex()).get(0).getDate(), "EEEE',' MMMM d"));

        return view;
    }

    private int getFirstRegPriceIndex() {
        int index = 0;

        for (int i = 0; i < peakPriceTable.size(); i++) {
            for (final BookingQuote.PeakPriceInfo info : peakPriceTable.get(i)) {
                if (info.getType().equals("reg-price")) {
                    return i;
                }
            }
        }

        return index;
    }
}
