package com.handybook.handybook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class BookingOptionsFragment extends InjectedFragment {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";

    private ArrayList<BookingOption> options;

    @Inject BookingRequestManager requestManager;

    static BookingOptionsFragment newInstance(final ArrayList<BookingOption> options) {
        final BookingOptionsFragment fragment = new BookingOptionsFragment();
        final Bundle args = new Bundle();
        args.putParcelableArrayList(EXTRA_OPTIONS, options);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = getArguments().getParcelableArrayList(EXTRA_OPTIONS);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_booking_options, container, false);
        ButterKnife.inject(this, view);

        for (BookingOption option : options) {
            System.out.println("ADD VIEW " + option.getUniq());
        }

        return view;
    }
}
