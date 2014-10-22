package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.simonvt.menudrawer.MenuDrawer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDetailFragment extends InjectedFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking booking;

    @InjectView(R.id.service_text) TextView serviceText;
    @InjectView(R.id.job_text) TextView jobText;

    static BookingDetailFragment newInstance(Booking booking) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(EXTRA_BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        ButterKnife.inject(this, view);

        serviceText.setText(booking.getService());
        jobText.setText(booking.getId());

        return view;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        final MenuDrawer menuDrawer = activity.getMenuDrawer();
        menuDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(final View view, final int i, final int i2, final int i3) {
                return true;
            }
        });
    }
}
