package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingRescheduleOptionsFragment extends BookingFlowFragment {
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_DATE = "com.handy.handy.EXTRA_RESCHEDULE_DATE";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int optionIndex;
    private Booking booking;
    private Date date;

    @InjectView(R.id.options_layout) FrameLayout optionsLayout;
    @InjectView(R.id.reschedule_button) Button rescheduleButton;

    static BookingRescheduleOptionsFragment newInstance(final Booking booking, final Date date) {
        final BookingRescheduleOptionsFragment fragment = new BookingRescheduleOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, booking);
        args.putLong(EXTRA_RESCHEDULE_DATE, date.getTime());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        date = new Date(getArguments().getLong(EXTRA_RESCHEDULE_DATE, 0));

        if (savedInstanceState != null) {
            optionIndex = savedInstanceState.getInt(STATE_OPTION_INDEX, 0);
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_reschedule_options, container, false);

        ButterKnife.inject(this, view);

        final BookingOption options = new BookingOption();
        options.setType("option");
        options.setOptions(new String[]{getString(R.string.no), getString(R.string.yes)});
        options.setDefaultValue(Integer.toString(optionIndex));

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(getActivity(),
                options, optionUpdated);

        optionsView.hideTitle();
        optionsLayout.addView(optionsView, 0);

        rescheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                rescheduleBooking(booking, date, optionIndex == 1);
            }
        });

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, optionIndex);
    }

    @Override
    public final void onActivityResult(final int requestCode, final int resultCode,
                                       final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PeakPricingActivity.RESULT_RESCHEDULE_NEW_DATE) {
            final long date = data
                    .getLongExtra(PeakPricingActivity.EXTRA_RESCHEDULE_NEW_DATE, 0);

            final Intent intent = new Intent();
            intent.putExtra(BookingRescheduleOptionsActivity.EXTRA_RESCHEDULE_NEW_DATE, date);

            getActivity().setResult(BookingRescheduleOptionsActivity.RESULT_RESCHEDULE_NEW_DATE, intent);
            getActivity().finish();
        }
    }

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener() {
        @Override
        public void onUpdate(final BookingOptionsView view) {
            optionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
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
