package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.widget.InstructionListView;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.ui.view.BasicInputTextView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingPreferencesFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener {

    static final String EXTRA_NEW_USER = "com.handy.handy.EXTRA_NEW_USER";
    static final String EXTRA_INSTRUCTIONS = "com.handy.handy.EXTRA_INSTRUCTIONS";

    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;
    private boolean mIsNewUser;
    private Instructions mInstructions;
    private final View.OnClickListener mOnNextClickedListener;

    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.preferences_note_to_pro)
    BasicInputTextView mNoteToProTextView;
    @Bind(R.id.instructions_layout)
    InstructionListView mInstructionListView;

    private boolean mIsPreferenceDragged, mIsPreferenceToggled;

    {

        mOnNextClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (bookingManager.getCurrentTransaction() == null) {
                    //quick-fix for current transaction being null when user clicks "finish" super fast
                    Crashlytics.logException(new Exception(
                            "current booking transaction is null on next click"));
                    return;
                }
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingShareInfoSubmittedLog()));
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingRoutineSubmittedLog()));

                //discourage user from pressing button twice
                //note that this doesn't prevent super fast clicks
                showUiBlockers();
                if (mInstructions != null) {
                    mFinalizeBookingRequestPayload.setBookingInstructions(
                            mInstructions.getBookingInstructions()
                    );
                }
                mFinalizeBookingRequestPayload.setNoteToPro(mNoteToProTextView.getInput());
                mFinalizeBookingRequestPayload.setShouldApplyToAll(
                        // Yeah I don't like this either, but see BookingRecurrenceFragment...
                        bookingManager.getCurrentTransaction().getRecurringFrequency() > 0
                );
                if (mIsNewUser) // Prompt the user to create a pasword
                {
                    final Intent intent = new Intent(getActivity(), BookingFinalizeActivity.class);
                    intent.putExtras(createProgressBundle());
                    intent.putExtra(
                            BookingFinalizeActivity.EXTRA_PAGE,
                            BookingFinalizeActivity.PAGE_PASSWORD_PROMPT
                    );
                    intent.putExtra(
                            BookingFinalizeActivity.EXTRA_NEW_USER,
                            mIsNewUser
                    );
                    startActivity(intent);
                    removeUiBlockers();
                }
                else {
                    bus.post(
                            new BookingEvent.RequestFinalizeBooking(
                                    bookingManager.getCurrentTransaction().getBookingId(),
                                    mFinalizeBookingRequestPayload
                            )
                    );
                }
            }
        };
    }

    public static BookingPreferencesFragment newInstance(
            final boolean isNewUser,
            final Instructions instructions
    ) {
        final BookingPreferencesFragment fragment = new BookingPreferencesFragment();
        final Bundle args = new Bundle();
        args.putBoolean(EXTRA_NEW_USER, isNewUser);
        args.putParcelable(EXTRA_INSTRUCTIONS, instructions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mInstructions = getArguments().getParcelable(EXTRA_INSTRUCTIONS);

        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingShareInfoShownLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingRoutineShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = getActivity().getLayoutInflater()
                                       .inflate(
                                               R.layout.fragment_booking_preferences,
                                               container,
                                               false
                                       );

        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.job_details));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bookingManager.getCurrentFinalizeBookingPayload() == null) {
            bookingManager.setCurrentFinalizeBookingRequestPayload(
                    new FinalizeBookingRequestPayload()
            );
        }
        mFinalizeBookingRequestPayload = bookingManager.getCurrentFinalizeBookingPayload();
        if (!mIsNewUser) {
            mNextButton.setText(getString(R.string.finish));
        }
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.preferences_note_to_pro_placeholder));
        if (mInstructions != null && mInstructions.getBookingInstructions() != null &&
            !mInstructions.getBookingInstructions().isEmpty()) {

            setToolbarTitle(getString(R.string.cleaning_routine));
            mInstructionListView.reflect(mInstructions);
            mInstructionListView.setOnInstructionsChangedListener(
                    new InstructionListView.OnInstructionsChangedListener() {
                        @Override
                        public void onInstructionsChanged(
                                final Instructions instructions,
                                InstructionListView.ChangeType changeType
                        ) {
                            switch (changeType) {
                                case UNKNOWN:
                                    break;
                                case POSITION_CHANGE:
                                    mIsPreferenceDragged = true;
                                    break;
                                case STATE_CHANGE:
                                    mIsPreferenceToggled = true;
                            }
                            mFinalizeBookingRequestPayload.setBookingInstructions(
                                    instructions.getBookingInstructions()
                            );
                        }
                    });
            mInstructionListView.setVisibility(View.VISIBLE);
        }
        else {
            mInstructionListView.setVisibility(View.GONE);
        }
        mNextButton.setOnClickListener(mOnNextClickedListener);
    }

    @Override
    protected void setupProgressBar() {
        mProgressBar.setVisibility(mShowProgress ? View.VISIBLE : View.GONE);
        mProgressBar.setProgress(mIsNewUser ? mProgress : MAX_PROGRESS);
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

    @Override
    public final void onBack() {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingShareInfoDismissedLog()));
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingRoutineDismissedLog()));
    }

    @Subscribe()
    public void onFinalizedSuccess(final BookingEvent.FinalizeBookingSuccess event) {
        if (!allowCallbacks ||
            bookingManager.getCurrentTransaction() == null)
                                    /*
                                    hot fix to prevent NPE caused by rapid multi-click
                                    of the next button
                                     */ {
            return;
        }
        String bookingId = Integer.toString(
                bookingManager.getCurrentTransaction().getBookingId()
        );
        showBookingDetails(bookingId);
        removeUiBlockers();
    }

    @Subscribe()
    public void onFinalizedError(final BookingEvent.FinalizeBookingError event) {
        if (!allowCallbacks) {
            return;
        }
        showToast(R.string.error_sending_preferences);
        removeUiBlockers();
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
