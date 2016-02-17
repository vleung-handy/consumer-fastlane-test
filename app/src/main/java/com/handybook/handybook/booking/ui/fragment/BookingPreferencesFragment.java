package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingFinalizeActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
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

    private BookingPostInfo mPostInfo;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;
    private boolean mIsNewUser;
    private Instructions mInstructions;

    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.preferences_note_to_pro)
    BasicInputTextView mNoteToProTextView;
    @Bind(R.id.instructions_layout)
    InstructionListView mInstructionListView;

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
        mNoteToProTextView.setMinLength(2);
        mNoteToProTextView.setHint(getString(R.string.preferences_note_to_pro_placeholder));
        mNoteToProTextView.addTextChangedListener(mNoteToProWatcher);
        mPostInfo = bookingManager.getCurrentPostInfo();
        if (bookingManager.getCurrentFinalizeBookingPayload() == null)
        {
            bookingManager.setCurrentFinalizeBookingRequestPayload(
                    new FinalizeBookingRequestPayload()
            );
        }
        mFinalizeBookingRequestPayload = bookingManager.getCurrentFinalizeBookingPayload();
        mHeaderText.setText(getString(R.string.booking_edit_preferences_subtitle));
        if (!mIsNewUser)
        {
            mNextButton.setText(getString(R.string.finish));
        }
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.additional_pro_info_hint));
        mInstructionListView.reflect(mInstructions);
        mNextButton.setOnClickListener(nextClicked);
        return view;
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

    private final View.OnClickListener nextClicked = new View.OnClickListener()
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
            mFinalizeBookingRequestPayload.setNoteToPro(mPostInfo.getExtraMessage());
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

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            mPostInfo.setExtraMessage(view.getCurrentValue());
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
    };

    private final TextWatcher mNoteToProWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(
                final CharSequence charSequence, final int start,
                final int count, final int after
        )
        {

        }

        @Override
        public void onTextChanged(
                final CharSequence charSequence, final int start,
                final int before, final int count
        )
        {
        }

        @Override
        public void afterTextChanged(final Editable editable)
        {
            mPostInfo.setGetInText(mNoteToProTextView.getInput());
        }
    };

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
