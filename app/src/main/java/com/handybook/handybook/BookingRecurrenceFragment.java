package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingRecurrenceFragment extends BookingFlowFragment {

    private BookingTransaction bookingTransaction;

    @InjectView(R.id.options_layout) LinearLayout optionsLayout;
    @InjectView(R.id.next_button) Button nextButton;

    static BookingRecurrenceFragment newInstance() {
        final BookingRecurrenceFragment fragment = new BookingRecurrenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);

        ButterKnife.inject(this, view);

        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();

        final BookingOption option = new BookingOption();
        option.setType("option");
        option.setDefaultValue("0");

        option.setOptions(new String[] { getString(R.string.every_week),
                getString(R.string.every_two_weeks), getString(R.string.every_four_weeks),
                getString(R.string.once)});

        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);

        optionsView.hideTitle();

        final int freq = bookingTransaction.getRecurringFrequency();

        if (savedInstanceState == null) optionsView.setCurrentIndex(1);
        else optionsView.setCurrentIndex(freq == 0 ? 3 : freq - 1);

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

        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            bookingTransaction.setRecurringFrequency(index + 1);
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
}
