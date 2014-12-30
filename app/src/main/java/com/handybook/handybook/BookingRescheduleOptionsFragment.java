package com.handybook.handybook;

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
                rescheduleBooking(booking, date);
                //TODO set all param here
                // TODO reschedule all subsequent (reschedule_all)

                //TODO save date when going forward then back
                //TODO better transition when leaving double fnish
                
                // TODO handle surge pricing
                //TODO refactor single option views to use framelayout instead of list
                //TODO long press date picker fields are editbale
                //TODO date screen should keep user changed value if leaving then coming back
            }
        });

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, optionIndex);
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
