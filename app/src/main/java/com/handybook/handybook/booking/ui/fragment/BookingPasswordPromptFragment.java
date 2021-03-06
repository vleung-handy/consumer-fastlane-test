package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.core.ui.widget.PasswordInputTextView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class BookingPasswordPromptFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {

    private static final String STATE_PWD_HIGHLIGHT = "PWD_HIGHLIGHT";
    public static final int PASSWORD_MIN_CHARS = 8;

    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;

    @BindView(R.id.header_text)
    TextView mHeaderText;
    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.password_text)
    PasswordInputTextView mPasswordText;

    public static BookingPasswordPromptFragment newInstance(@Nullable final Bundle extras) {
        final BookingPasswordPromptFragment fragment = new BookingPasswordPromptFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingPasswordShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_booking_password_prompt, container, false));

        ButterKnife.bind(this, view);

        if (bookingManager.getCurrentFinalizeBookingPayload() == null) {
            bookingManager.setCurrentFinalizeBookingRequestPayload(
                    new FinalizeBookingRequestPayload()
            );
        }
        mFinalizeBookingRequestPayload = bookingManager.getCurrentFinalizeBookingPayload();
        mHeaderText.setText(getString(R.string.use_your_pwd));
        mNextButton.setText(getString(R.string.finish));
        mPasswordText.setVisibility(View.VISIBLE);
        mNextButton.setOnClickListener(nextClicked);
        mToolbar.setTitle(R.string.confirmation);
        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(STATE_PWD_HIGHLIGHT)) {
                mPasswordText.highlight();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_PWD_HIGHLIGHT, mPasswordText.isHighlighted());
    }

    @Override
    protected void setupProgressBar() {
        mProgressBar.setVisibility(mShowProgress ? View.VISIBLE : View.GONE);
        mProgressBar.setProgress(MAX_PROGRESS);
    }

    @Override
    protected final void disableInputs() {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    private boolean validateFields() {
        boolean validate = true;

        if (!mPasswordText.validate()) {
            validate = false;
        }
        if (mPasswordText.getPassword().length() < PASSWORD_MIN_CHARS) {
            validate = false;
            mPasswordText.highlight();
            toast.setText(getString(R.string.pwd_length_error));
            toast.show();
        }

        return validate;
    }

    @Override
    public final void onBack() {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingPasswordDismissedLog()));
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (!validateFields() ||
                bookingManager.getCurrentTransaction() == null)
                    /*
                    hot fix to prevent NPE caused by rapid multi-click
                    of the next button
                     */ {
                return;
            }

            //discourage user from pressing button twice
            //note that this doesn't prevent super fast clicks
            disableInputs();
            showProgressSpinner(true);
            mFinalizeBookingRequestPayload.setPassword(mPasswordText.getPassword());
            bus.post(
                    new BookingEvent.RequestFinalizeBooking(
                            bookingManager.getCurrentTransaction().getBookingId(),
                            mFinalizeBookingRequestPayload
                    )
            );

            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingPasswordSubmittedLog()));

            //            dataManager.addBookingPostInfo(bookingManager.getCurrentTransaction().getBookingId(),
            //                    mPostInfo, new FragmentSafeCallback<Void>(BookingPasswordPromptFragment.this)
            //                    {
            //                        @Override
            //                        public void onCallbackSuccess(final Void response)
            //                        {
            //                            if (!allowCallbacks ||
            //                                    bookingManager.getCurrentTransaction() == null)
            //                                    /*
            //                                    hot fix to prevent NPE caused by rapid multi-click
            //                                    of the next button
            //                                     */
            //                            {
            //                                return;
            //                            }
            //                            String bookingId = Integer.toString(bookingManager.getCurrentTransaction().getBookingId());
            //                            showBookingDetails(bookingId);
            //                            enableInputs();
            //                            progressDialog.dismiss();
            //                        }
            //
            //                        @Override
            //                        public void onCallbackError(final DataManager.DataManagerError error)
            //                        {
            //                            if (!allowCallbacks)
            //                            {
            //                                return;
            //                            }
            //                            enableInputs();
            //                            progressDialog.dismiss();
            //                            dataManagerErrorHandler.handleError(getActivity(), error);
            //                        }
            //                    });
            //        }
        }
    };

    @Subscribe()
    public void onFinalizedSuccess(final BookingEvent.FinalizeBookingSuccess event) {
        if (!allowCallbacks || bookingManager.getCurrentTransaction() == null) {
            /*
            hot fix to prevent NPE caused by rapid multi-click
            of the next button
             */
            return;
        }
        String bookingId = Integer.toString(bookingManager.getCurrentTransaction().getBookingId());
        showBookingDetails(bookingId);
        enableInputs();
        hideProgressSpinner();
    }

    @Subscribe()
    public void onFinalizedError(final BookingEvent.FinalizeBookingError event) {
        if (!allowCallbacks) {
            return;
        }
        showToast(R.string.error_setting_password);
        enableInputs();
        hideProgressSpinner();
    }

    private void showBookingDetails(String bookingId) {
        bookingManager.clearAll();
        dataManager.getBooking(
                bookingId,
                new FragmentSafeCallback<Booking>(this) {
                    @Override
                    public void onCallbackSuccess(final Booking booking) {
                        final Intent intent = new Intent(
                                getActivity(),
                                BookingDetailActivity.class
                        );
                        intent.putExtra(BundleKeys.IS_FROM_BOOKING_FLOW, true);
                        intent.putExtra(BundleKeys.BOOKING, booking);
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
                    }
                }
        );
    }

}
