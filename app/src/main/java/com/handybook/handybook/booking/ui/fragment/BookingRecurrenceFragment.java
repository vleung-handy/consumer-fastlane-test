package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingRecurrenceFragment extends BookingFlowFragment {
    private BookingTransaction bookingTransaction;
    private int[] recurValues;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static BookingRecurrenceFragment newInstance() {
        final BookingRecurrenceFragment fragment = new BookingRecurrenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
        mixpanel.trackEventAppTrackFrequency();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.how_often));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setDefaultValue("0");

        option.setOptions(new String[] { getString(R.string.every_week),
                getString(R.string.every_two_weeks), getString(R.string.every_four_weeks),
                getString(R.string.once)});

        recurValues = new int[] { 1, 2, 4, 0 };

        option.setOptionsSubText(new String[]
                {null, getString(R.string.most_popular), null, null});

        option.setOptionsRightSubText(getSavingsInfo());

        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);

        optionsView.hideTitle();

        if (savedInstanceState == null) optionsView.setCurrentIndex(1);
        else optionsView.setCurrentIndex(indexForFreq(bookingTransaction.getRecurringFrequency()));

        optionsLayout.addView(optionsView, 0);

        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            continueBookingFlow();
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            bookingTransaction.setRecurringFrequency(recurValues[index]);
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items) {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items) {
        }
    };

    private String[] getSavingsInfo() {
        final String[] info = new String[4];
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final float hours = bookingTransaction.getHours();
        final float prices[] = quote.getPricing(hours, 0);
        final float price = prices[0];
        final float discount = prices[1];

        for (int i = 0; i < 3; i++) {
            final float recurPrices[] = quote.getPricing(hours, freqForIndex(i));
            final float recurPrice = recurPrices[0];
            final float recurDiscount = recurPrices[1];

            int percent;
            if (recurPrice != recurDiscount)
                percent = (int)((discount - recurDiscount) / discount * 100);
            else percent = (int)((price - recurPrice) / price * 100);

            if (percent > 0) info[i] = getString(R.string.save).toUpperCase() + " " + percent + "%";
        }
        return info;
    }

    private int indexForFreq(final int freq) {
        switch (freq) {
            case 1:
                return 0;

            case 2:
                return 1;

            case 4:
                return 2;

            default:
                return 3;
        }
    }

    private int freqForIndex(final int index) {
        switch (index) {
            case 0:
                return 1;

            case 1:
                return 2;

            case 2:
                return 4;

            default:
                return 0;
        }
    }
}
