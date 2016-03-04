package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;


public final class BookingPreferencesFragment extends BookingFlowFragment
        implements BaseActivity.OnBackPressedListener
{
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

    @Bind(R.id.nav_text)
    TextView mNavText;

    {

        mOnNextClickedListener = new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                //discourage user from pressing button twice
                //note that this doesn't prevent super fast clicks
                showUiBlockers();
                if (mInstructions != null)
                {
                    mFinalizeBookingRequestPayload.setBookingInstructions(
                            mInstructions.getBookingInstructions()
                    );
                }
                mFinalizeBookingRequestPayload.setNoteToPro(mNoteToProTextView.getInput());
                if (mIsNewUser) // Prompt the user to create a pasword
                {
                    final Intent intent = new Intent(getActivity(), BookingFinalizeActivity.class);
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
                else
                {
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
    )
    {
        final BookingPreferencesFragment fragment = new BookingPreferencesFragment();
        final Bundle args = new Bundle();
        args.putBoolean(EXTRA_NEW_USER, isNewUser);
        args.putParcelable(EXTRA_INSTRUCTIONS, instructions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mIsNewUser = getArguments().getBoolean(EXTRA_NEW_USER, false);
        mInstructions = getArguments().getParcelable(EXTRA_INSTRUCTIONS);
        mixpanel.trackEventAppTrackPreferences();
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_preferences, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (bookingManager.getCurrentFinalizeBookingPayload() == null)
        {
            bookingManager.setCurrentFinalizeBookingRequestPayload(
                    new FinalizeBookingRequestPayload()
            );
        }
        mFinalizeBookingRequestPayload = bookingManager.getCurrentFinalizeBookingPayload();
        if (!mIsNewUser)
        {
            mNextButton.setText(getString(R.string.finish));
        }
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.preferences_note_to_pro_placeholder));
        if (mInstructions != null && mInstructions.getBookingInstructions() != null &&
                !mInstructions.getBookingInstructions().isEmpty())
        {

            mNavText.setText(getString(R.string.cleaning_routine));
            mInstructionListView.reflect(mInstructions);
            mInstructionListView.setOnInstructionsChangedListener(
                    new InstructionListView.OnInstructionsChangedListener()
                    {
                        @Override
                        public void onInstructionsChanged(final Instructions instructions)
                        {
                            mFinalizeBookingRequestPayload.setBookingInstructions(
                                    instructions.getBookingInstructions()
                            );
                        }
                    });
            mInstructionListView.setVisibility(View.VISIBLE);
        }
        else
        {
            mInstructionListView.setVisibility(View.GONE);
        }
        mNextButton.setOnClickListener(mOnNextClickedListener);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    @Override
    public final void onBack()
    {
    }


    @Subscribe()
    public void onFinalizedSuccess(final BookingEvent.FinalizeBookingSuccess event)
    {
        if (!allowCallbacks ||
                bookingManager.getCurrentTransaction() == null)
                                    /*
                                    hot fix to prevent NPE caused by rapid multi-click
                                    of the next button
                                     */
        {
            return;
        }
        String bookingId = Integer.toString(
                bookingManager.getCurrentTransaction().getBookingId()
        );
        showBookingDetails(bookingId);
        removeUiBlockers();
    }

    @Subscribe()
    public void onFinalizedError(final BookingEvent.FinalizeBookingError event)
    {
        if (!allowCallbacks)
        {
            return;
        }
        showToast(R.string.error_sending_preferences);
        removeUiBlockers();
    }


    private void showBookingDetails(String bookingId)
    {
        bookingManager.clearAll();
        dataManager.getBooking(bookingId,
                new DataManager.Callback<Booking>()
                {
                    @Override
                    public void onSuccess(final Booking booking)
                    {
                        final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                        intent.putExtra(BundleKeys.BOOKING, booking);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        dataManagerErrorHandler.handleError(getActivity(), error);
                        startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
                    }
                });
    }

}