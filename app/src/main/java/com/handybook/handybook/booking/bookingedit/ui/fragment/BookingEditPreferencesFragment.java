package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsTextView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.InstructionsLayout;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditPreferencesFragment extends BookingFlowFragment
{
    private BookingUpdateNoteToProTransaction descriptionTransaction;

    private Booking booking;

    @Bind(R.id.options_layout)
    LinearLayout mOptionsLayout;
    @Bind(R.id.next_button)
    Button mNextButton;
    @Bind(R.id.instructions_layout)
    InstructionsLayout mInstructionsLayout;

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
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        initTransaction();
    }

    private void initTransaction()
    {
        descriptionTransaction = new BookingUpdateNoteToProTransaction();
        descriptionTransaction.setMessageToPro(booking.getProNote());
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_edit_preferences, container, false);   //TODO: Make this its own fragment?

        ButterKnife.bind(this, view);
        initOptionsView();
        mInstructionsLayout.init(booking.getInstructions());
        return view;
    }

    private void initOptionsView()
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.additional_pro_info_hint));
        BookingOptionsView optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
        ((BookingOptionsTextView) optionsView).setValue(descriptionTransaction.getMessageToPro());
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
    public final void onReceiveUpdateBookingNoteToProSuccess(HandyEvent.ReceiveUpdateBookingNoteToProSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();
        showToast(R.string.updated_preferences);

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingNoteToProError(HandyEvent.ReceiveUpdateBookingNoteToProError event)
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
        int bookingId = Integer.parseInt(booking.getId());
        bus.post(new HandyEvent.RequestUpdateBookingNoteToPro(bookingId, descriptionTransaction));
    }

    private final BookingOptionsView.OnUpdatedListener textUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            descriptionTransaction.setMessageToPro(view.getCurrentValue());
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }
    };
}
