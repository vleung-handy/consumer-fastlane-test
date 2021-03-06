package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class BookingRescheduleOptionsFragment extends BookingFlowFragment {

    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_DATE = "com.handy.handy.EXTRA_RESCHEDULE_DATE";
    static final String EXTRA_RESCHEDULE_IS_INSTANT_BOOK_ENABLED
            = "com.handy.handy.EXTRA_RESCHEDULE_IS_INSTANT_BOOK_ENABLED";
    static final String EXTRA_RESCHEDULE_TYPE = "com.handy.handy.EXTRA_RESCHEDULE_TYPE";
    static final String EXTRA_PROVIDER_ID = "com.handy.handy.EXTRA_PROVIDER_ID";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int optionIndex;
    private Booking booking;
    private Date date;
    private String mProviderId;

    @BindView(R.id.options_layout)
    FrameLayout optionsLayout;
    @BindView(R.id.reschedule_button)
    Button rescheduleButton;
    BookingDetailFragment.RescheduleType mRescheduleType;
    boolean mIsInstantBookEnabled;

    public static BookingRescheduleOptionsFragment newInstance(
            final Booking booking,
            final Date date,
            @Nullable final String providerId,
            final BookingDetailFragment.RescheduleType rescheduleType,
            final boolean isInstantBookEnabled,
            @Nullable final Bundle extras
    ) {
        final BookingRescheduleOptionsFragment fragment = new BookingRescheduleOptionsFragment();
        final Bundle args = new Bundle();

        args.putParcelable(EXTRA_RESCHEDULE_BOOKING, booking);
        args.putLong(EXTRA_RESCHEDULE_DATE, date.getTime());
        args.putString(EXTRA_PROVIDER_ID, providerId);
        args.putSerializable(EXTRA_RESCHEDULE_TYPE, rescheduleType);
        args.putBoolean(EXTRA_RESCHEDULE_IS_INSTANT_BOOK_ENABLED, isInstantBookEnabled);
        if (extras != null) {
            args.putAll(extras);
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(EXTRA_RESCHEDULE_BOOKING);
        date = new Date(getArguments().getLong(EXTRA_RESCHEDULE_DATE, 0));
        mProviderId = getArguments().getString(EXTRA_PROVIDER_ID);
        mRescheduleType = (BookingDetailFragment.RescheduleType) getArguments().getSerializable(
                EXTRA_RESCHEDULE_TYPE);
        mIsInstantBookEnabled = getArguments().getBoolean(EXTRA_RESCHEDULE_IS_INSTANT_BOOK_ENABLED);

        if (savedInstanceState != null) {
            optionIndex = savedInstanceState.getInt(STATE_OPTION_INDEX, 0);
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_reschedule_options,
                                               container,
                                               false
                                       ));

        ButterKnife.bind(this, view);

        final BookingOption options = new BookingOption();
        options.setType(BookingOption.TYPE_OPTION);
        options.setOptions(new String[]{getString(R.string.no), getString(R.string.yes)});
        options.setDefaultValue(Integer.toString(optionIndex));

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(
                getActivity(),
                options,
                optionUpdated
        );

        optionsView.hideTitle();
        optionsLayout.addView(optionsView, 0);

        rescheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                rescheduleBooking(
                        booking,
                        date,
                        optionIndex == 1,
                        mProviderId,
                        mRescheduleType,
                        String.valueOf(booking.getRecurringId()),
                        mIsInstantBookEnabled
                );
            }
        });

        setupToolbar(mToolbar, getString(R.string.reschedule));

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, optionIndex);
    }

    @Override
    public final void onActivityResult(
            final int requestCode, final int resultCode,
            final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        //Pass along any valid reschedules
        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            if (data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0) != 0) {
                final long date = data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0);
                final Intent intent = new Intent();
                intent.putExtra(BundleKeys.RESCHEDULE_NEW_DATE, date);
                getActivity().setResult(ActivityResult.RESCHEDULE_NEW_DATE, intent);
            }
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
        public void onShowChildren(
                final BookingOptionsView view,
                final String[] items
        ) {
        }

        @Override
        public void onHideChildren(
                final BookingOptionsView view,
                final String[] items
        ) {
        }
    };
}
