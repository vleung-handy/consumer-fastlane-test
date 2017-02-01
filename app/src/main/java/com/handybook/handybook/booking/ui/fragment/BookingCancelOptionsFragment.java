package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingCancelOptionsFragment extends BookingFlowFragment
{
    public static final String EXTRA_BOOKING_CANCELLATION_DATA
            = "com.handy.handy.EXTRA_BOOKING_CANCELLATION_DATA";
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    private static final String STATE_OPTION_INDEX = "OPTION_INDEX";

    private int mOptionIndex = -1;
    private Booking mBooking;
    private BookingCancellationData mBookingCancellationData;

    @Bind(R.id.booking_cancel_options_title)
    TextView mTitle;
    @Bind(R.id.options_layout)
    FrameLayout mOptionsLayout;
    @Bind(R.id.notice_text)
    TextView mNoticeText;
    @Bind(R.id.booking_cancel_options_button)
    Button mButton;

    public static BookingCancelOptionsFragment newInstance(
            final Booking booking,
            final BookingCancellationData bookingCancellationData
    )
    {
        final BookingCancelOptionsFragment fragment = new BookingCancelOptionsFragment();
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
        final boolean isRecurring = mBooking != null && mBooking.isRecurring();
        @StringRes final int strRes = isRecurring ? R.string.skip_booking : R.string.cancel_booking;
        mTitle.setText(strRes);
        mButton.setText(strRes);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                disableInputs();
                progressDialog.show();
                final User user = userManager.getCurrentUser();
                bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.SkipBooking(
                                 BookingDetailsLog.EventType.SUBMITTED,
                                 mBooking.getId()
                         ))
                );
                if (user == null)
                {
                    Crashlytics.logException(new NullPointerException("User is null"));
                    showToast(R.string.default_error_string);
                    return;
                }
                final FragmentSafeCallback<String> cb = new FragmentSafeCallback<String>(
                        BookingCancelOptionsFragment.this
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
                        if (TextUtils.isEmpty(message)) { showToast(message); }
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
                dataManager.cancelBooking(mBooking.getId(), mOptionIndex, user.getId(), cb);
            }
        });

        final BookingOption options = new BookingOption();
        options.setType(BookingOption.TYPE_OPTION);

        final BookingCancellationData.CancellationReason[] reasons = mBookingCancellationData
                .getCancellationInfo().getReasons();
        final String[] stringReasons = new String[reasons.length];
        for (int i = 0; i < reasons.length; i++)
        {
            stringReasons[i] = reasons[i].getLabel();
        }
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
        mOptionsLayout.addView(optionsView, 0);
        if (mBookingCancellationData.hasWarning())
        {
            mNoticeText.setText(mBookingCancellationData.getWarningMessage());
            mNoticeText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_OPTION_INDEX, mOptionIndex);
    }

}
