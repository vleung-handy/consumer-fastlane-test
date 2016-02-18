package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.booking.ui.widget.InstructionListView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.widget.BackButtonNavBar;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditPreferencesFragment extends BookingFlowFragment
{
    private Booking mBooking;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;

    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.instructions_layout)
    InstructionListView mInstructionListView;
    @Bind(R.id.edit_preferences_scrollview)
    ScrollView mScrollView;

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
        initRequestPayload();
    }

    private void initRequestPayload()
    {
        mFinalizeBookingRequestPayload = new FinalizeBookingRequestPayload();
        mFinalizeBookingRequestPayload.setNoteToPro(mBooking.getProNote());
        mFinalizeBookingRequestPayload.setBookingInstructions(
                mBooking.getInstructions().getBookingInstructions()
        );
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
        initOptionsView();
        mInstructionListView.setParentScrollContainer(mScrollView);

        //if there are instructions (even if they are not requested)
        if (mBooking != null && mBooking.getInstructions() != null
                && mBooking.getInstructions().getBookingInstructions() != null
                && !mBooking.getInstructions().getBookingInstructions().isEmpty())
        {
            mNavBar.setText(getString(R.string.booking_edit_cleaning_routine_title));
            mInstructionListView.reflect(mBooking.getInstructions());
            mInstructionListView.setOnInstructionsChangedListener(this);
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
        return view;
    }

    private void initOptionsView()
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.additional_pro_info_hint));
        BookingOptionsView optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
        ((BookingOptionsTextView) optionsView).setValue(mFinalizeBookingRequestPayload.getNoteToPro());
        mOptionsLayout.addView(optionsView, 0);
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
        requestUpdateBookingNoteToPro();
    }

    private void requestUpdateBookingNoteToPro()
    {
        disableInputs();
        progressDialog.show();
        int bookingId = Integer.parseInt(mBooking.getId());
        bus.post(new BookingEditEvent.RequestEditPreferences(bookingId, mFinalizeBookingRequestPayload));
    }

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            mFinalizeBookingRequestPayload.setNoteToPro(view.getCurrentValue());
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


}
