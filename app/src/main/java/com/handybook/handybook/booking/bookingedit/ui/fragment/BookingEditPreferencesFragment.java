package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.widget.InstructionListView;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.BasicInputTextView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public final class BookingEditPreferencesFragment extends ProgressSpinnerFragment {

    private Booking mBooking;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload
            = new FinalizeBookingRequestPayload();

    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.edit_preferences_instructions_layout)
    InstructionListView mInstructionListView;
    @BindView(R.id.edit_preferences_scrollview)
    ScrollView mScrollView;
    @BindView(R.id.edit_preferences_note_to_pro)
    BasicInputTextView mNoteToProTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edit_preferences_apply_to_all_container)
    View mApplyToAllContainer;

    private boolean mIsPreferenceDragged, mIsPreferenceToggled;

    public static BookingEditPreferencesFragment newInstance(final Booking booking) {
        final BookingEditPreferencesFragment fragment = new BookingEditPreferencesFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        if (mBooking == null) {
            return;
        }
        mFinalizeBookingRequestPayload.setNoteToPro(mBooking.getProNote());
        if (mBooking.getInstructions() == null) {
            return;
        }
        mFinalizeBookingRequestPayload.setBookingInstructions(
                mBooking.getInstructions().getBookingInstructions()
        );

    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(
                R.layout.fragment_booking_edit_preferences,
                container,
                false
        ));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.booking_edit_preferences_title));
        if (mBooking.isRecurring()) {
            mFinalizeBookingRequestPayload.setShouldApplyToAll(true);
            mApplyToAllContainer.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mInstructionListView.setParentScrollContainer(mScrollView);
        //if there are instructions (even if they are not requested)
        if (mBooking != null && mBooking.getInstructions() != null
            && mBooking.getInstructions().getBookingInstructions() != null
            && !mBooking.getInstructions().getBookingInstructions().isEmpty()) {
            setToolbarTitle(getString(R.string.booking_edit_cleaning_routine_title));
            mInstructionListView.reflect(mBooking.getInstructions());
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
        mNoteToProTextView.setText(mFinalizeBookingRequestPayload.getNoteToPro());
        mNoteToProTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            ) {
            }

            @Override
            public void onTextChanged(
                    final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            ) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                mFinalizeBookingRequestPayload.setNoteToPro(s.toString());
            }
        });
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
    protected final void disableInputs() {
        super.disableInputs();
        mNextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs() {
        super.enableInputs();
        mNextButton.setClickable(true);
    }

    @Subscribe
    public final void onUpdateSuccess(
            BookingEditEvent.ReceiveEditPreferencesSuccess event
    ) {
        enableInputs();
        hideProgressSpinner();
        showToast(R.string.updated_preferences);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onUpdateError(
            BookingEditEvent.ReceiveEditPreferencesError event
    ) {
        enableInputs();
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        disableInputs();
        showProgressSpinner(true);
        int bookingId = Integer.parseInt(mBooking.getId());
        bus.post(new BookingEditEvent.RequestEditPreferences(
                bookingId,
                mFinalizeBookingRequestPayload
        ));
    }

    @OnCheckedChanged(R.id.edit_preferences_apply_to_all_checkbox)
    public void onApplyToAllToggled(AppCompatCheckBox checkbox) {
        mFinalizeBookingRequestPayload.setShouldApplyToAll(checkbox.isChecked());
    }

}
