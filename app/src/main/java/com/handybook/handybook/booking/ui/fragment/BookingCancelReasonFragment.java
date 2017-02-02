package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingCancelReasonFragment extends BookingFlowFragment
{
    public static final String EXTRA_BOOKING_CANCELLATION_DATA
            = "com.handy.handy.EXTRA_BOOKING_CANCELLATION_DATA";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int mOptionIndex = -1;
    private Booking mBooking;
    private BookingCancellationData mBookingCancellationData;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.booking_cancel_reason_title)
    TextView mTitle;
    @Bind(R.id.booking_cancel_reason_warning)
    TextView mWarning;
    @Bind(R.id.booking_cancel_reason_button)
    Button mButton;
    @Bind(R.id.booking_cancel_reason_options)
    FrameLayout mOptionsContainer;

    public static BookingCancelReasonFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final BookingCancellationData bookingCancellationData
    )
    {
        final BookingCancelReasonFragment fragment = new BookingCancelReasonFragment();
        final Bundle args = new Bundle();
        args.putSerializable(EXTRA_BOOKING_CANCELLATION_DATA, bookingCancellationData);
        args.putParcelable(EXTRA_BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookingCancellationData = (BookingCancellationData) getArguments()
                .getSerializable(EXTRA_BOOKING_CANCELLATION_DATA);
        mBooking = getArguments().getParcelable(EXTRA_BOOKING);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity()
                .getLayoutInflater()
                .inflate(R.layout.fragment_booking_cancel_options, container, false);
        ButterKnife.bind(this, view);
        initUI();
        return view;
    }

    private void initUI()
    {
        setupToolbar(
                mToolbar,
                mBookingCancellationData.getCancellationInfo().getNavigationTitle(),
                true
        );
        mTitle.setText(mBookingCancellationData.getCancellationInfo().getTitle());
        if (mBookingCancellationData.hasWarning())
        {
            mWarning.setText(mBookingCancellationData.getWarningMessage());
            mWarning.setVisibility(View.VISIBLE);
        }
        initButton();
        initOptions();
    }

    private void initButton()
    {
        mButton.setText(mBookingCancellationData.getCancellationInfo().getButtonLabel());
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                disableInputs();
                progressDialog.show();
                bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.SkipBooking(
                                 BookingDetailsLog.EventType.SUBMITTED,
                                 mBooking.getId()
                         ))
                );
                final FragmentSafeCallback<String> cb = new FragmentSafeCallback<String>(
                        BookingCancelReasonFragment.this
                )
                {
                    @Override
                    public void onCallbackSuccess(final String message)
                    {
                        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.SkipBooking(
                                BookingDetailsLog.EventType.SUCCESS,
                                mBooking.getId()
                        )));
                        if (!allowCallbacks) { return; }
                        progressDialog.dismiss();
                        enableInputs();
                        if (!TextUtils.isEmpty(message)) { showToast(message); }
                        getActivity().setResult(ActivityResult.BOOKING_CANCELED, new Intent());
                        getActivity().finish();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error)
                    {
                        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.SkipBooking(
                                         BookingDetailsLog.EventType.ERROR,
                                         mBooking.getId()
                                 ))
                        );

                        if (!allowCallbacks) { return; }
                        progressDialog.dismiss();
                        enableInputs();
                        dataManagerErrorHandler.handleError(getActivity(), error);
                    }
                };
                Integer reasonId = null;
                if (mOptionIndex >= 0)
                {
                    reasonId = mBookingCancellationData
                            .getCancellationInfo()
                            .getReasons()[mOptionIndex]
                            .getId();
                }
                dataManager.cancelBooking(mBooking.getId(), reasonId, cb);
            }
        });
    }

    private void initOptions()
    {
        final BookingCancellationData.CancellationReason[] reasons = mBookingCancellationData
                .getCancellationInfo()
                .getReasons();
        final String[] stringReasons = new String[reasons.length];
        for (int i = 0; i < reasons.length; i++)
        {
            stringReasons[i] = reasons[i].getLabel();
        }

        final BookingOption options = new BookingOption();
        options.setType(BookingOption.TYPE_OPTION);
        options.setOptions(stringReasons);
        options.setDefaultValue(Integer.toString(mOptionIndex));

        final BookingOptionsSelectView optionsView = new BookingOptionsSelectView(
                getActivity(),
                options,
                new BookingOptionsView.OnUpdatedListener()
                {
                    @Override
                    public void onUpdate(final BookingOptionsView view)
                    {
                        mOptionIndex = ((BookingOptionsSelectView) view).getCurrentIndex();
                    }

                    @Override
                    public void onShowChildren(
                            final BookingOptionsView view,
                            final String[] items
                    )
                    {
                    }

                    @Override
                    public void onHideChildren(
                            final BookingOptionsView view,
                            final String[] items
                    )
                    {
                    }
                }
        );
        optionsView.hideTitle();
        mOptionsContainer.removeAllViews();
        mOptionsContainer.addView(optionsView);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, mOptionIndex);
    }

}
