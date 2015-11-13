package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsTextView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditNoteToProFragment extends BookingFlowFragment
{
    private BookingUpdateNoteToProTransaction descriptionTransaction;

    private Booking booking;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    TextView headerText;
    @Bind(R.id.next_button)
    Button nextButton;

    public static BookingEditNoteToProFragment newInstance(final Booking booking)
    {
        final BookingEditNoteToProFragment fragment = new BookingEditNoteToProFragment();
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
                .inflate(R.layout.fragment_booking_confirmation, container, false);   //TODO: Make this its own fragment?

        ButterKnife.bind(this, view);

        headerText.setText(getString(R.string.pro_to_know));

        initOptionsView();

        nextButton.setText(R.string.update);
        nextButton.setOnClickListener(nextClicked);

        return view;
    }

    private void initOptionsView()
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_TEXT);
        option.setDefaultValue(getString(R.string.additional_pro_info));
        BookingOptionsView optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);
        ((BookingOptionsTextView) optionsView).setValue(descriptionTransaction.getMessageToPro());
        optionsLayout.addView(optionsView, 0);
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }


    @Subscribe
    public final void onReceiveUpdateBookingNoteToProSuccess(HandyEvent.ReceiveUpdateBookingNoteToProSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();
        showToast(R.string.updated_note_to_pro);

        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingNoteToProError(HandyEvent.ReceiveUpdateBookingNoteToProError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            disableInputs();
            progressDialog.show();
            int bookingId = Integer.parseInt(booking.getId());
            bus.post(new HandyEvent.RequestUpdateBookingNoteToPro(bookingId, descriptionTransaction));
        }
    };

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