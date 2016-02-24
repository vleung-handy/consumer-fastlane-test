package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.widget.InstructionListView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.widget.BackButtonNavBar;
import com.handybook.handybook.ui.widget.BasicInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditPreferencesFragment extends BookingFlowFragment
{
    private Booking mBooking;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload
            = new FinalizeBookingRequestPayload();

    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.edit_preferences_instructions_layout)
    InstructionListView mInstructionListView;
    @Bind(R.id.edit_preferences_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.edit_preferences_note_to_pro)
    BasicInputTextView mNoteToProTextView;

    @Bind(R.id.nav_bar)
    BackButtonNavBar mNavBar;

    public static BookingEditPreferencesFragment newInstance(final Booking booking)
    {
        final BookingEditPreferencesFragment fragment = new BookingEditPreferencesFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        if (mBooking != null)
        {
            mFinalizeBookingRequestPayload.setNoteToPro(mBooking.getProNote());
            mFinalizeBookingRequestPayload.setBookingInstructions(
                    mBooking.getInstructions().getBookingInstructions()
            );
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_preferences, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mInstructionListView.setParentScrollContainer(mScrollView);
        //if there are instructions (even if they are not requested)
        if (mBooking != null && mBooking.getInstructions() != null
                && mBooking.getInstructions().getBookingInstructions() != null
                && !mBooking.getInstructions().getBookingInstructions().isEmpty())
        {
            mNavBar.setText(getString(R.string.booking_edit_cleaning_routine_title));
            mInstructionListView.reflect(mBooking.getInstructions());
            mInstructionListView.setVisibility(View.VISIBLE);
        }
        else
        {
            mInstructionListView.setVisibility(View.GONE);
        }
        mInstructionListView.reflect(mBooking.getInstructions());
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
        mNoteToProTextView.setText(mFinalizeBookingRequestPayload.getNoteToPro());
        mNoteToProTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(
                    final CharSequence s,
                    final int start,
                    final int count,
                    final int after
            )
            {
            }

            @Override
            public void onTextChanged(
                    final CharSequence s,
                    final int start,
                    final int before,
                    final int count
            )
            {
            }

            @Override
            public void afterTextChanged(final Editable s)
            {
                mFinalizeBookingRequestPayload.setNoteToPro(s.toString());
            }
        });
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

    @Subscribe
    public final void onUpdateSuccess(
            BookingEditEvent.ReceiveEditPreferencesSuccess event
    )
    {
        enableInputs();
        progressDialog.dismiss();
        showToast(R.string.updated_preferences);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onUpdateError(
            BookingEditEvent.ReceiveEditPreferencesError event
    )
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick()
    {
        disableInputs();
        progressDialog.show();
        int bookingId = Integer.parseInt(mBooking.getId());
        bus.post(new BookingEditEvent.RequestEditPreferences(bookingId, mFinalizeBookingRequestPayload));
    }

}
