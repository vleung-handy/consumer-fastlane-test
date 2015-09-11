package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingUpdateDescriptionTransaction;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsTextView;
import com.handybook.handybook.ui.widget.BookingOptionsView;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingNoteToProFragment extends BookingFlowFragment
{
    private BookingUpdateDescriptionTransaction descriptionTransaction;

    private Booking booking;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.header_text)
    TextView headerText;
    @Bind(R.id.next_button)
    Button nextButton;

    public static BookingNoteToProFragment newInstance(final Booking booking)
    {
        final BookingNoteToProFragment fragment = new BookingNoteToProFragment();

        final Bundle args = new Bundle();

        args.putParcelable(BundleKeys.BOOKING, booking);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_confirmation, container, false);

        ButterKnife.bind(this, view);

        headerText.setText(getString(R.string.pro_to_know));

        final BookingOption option = new BookingOption();
        option.setType("text");
        option.setDefaultValue(getString(R.string.additional_pro_info));

        BookingOptionsView optionsView = new BookingOptionsTextView(getActivity(), option, textUpdated);

        ((BookingOptionsTextView) optionsView).setValue(descriptionTransaction.getMessageToPro());

        optionsLayout.addView(optionsView, 0);

        nextButton.setOnClickListener(nextClicked);

        return view;
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

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            //If we send incomplete booking post info does it overwrite existing data?

            BookingUpdateDescriptionTransaction descriptionTransaction = new BookingUpdateDescriptionTransaction();

            //descriptionTransaction.setMessageToPro();

            int bookingId = Integer.parseInt(booking.getId());

            bus.post(new HandyEvent.RequestUpdateBookingNoteToPro(bookingId, descriptionTransaction));


//            dataManager.addBookingPostInfo(bookingManager.getCurrentTransaction().getBookingId(),
//                    postInfo, new DataManager.Callback<Void>()
//                    {
//                        @Override
//                        public void onSuccess(final Void response)
//                        {
//                            if (!allowCallbacks)
//                            {
//                                return;
//                            }
//                            showBookings();
//                            enableInputs();
//                            progressDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onError(final DataManager.DataManagerError error)
//                        {
//                            if (!allowCallbacks)
//                            {
//                                return;
//                            }
//
//                            enableInputs();
//                            progressDialog.dismiss();
//                            dataManagerErrorHandler.handleError(getActivity(), error);
//                        }
//                    });
        }
    };

//    private void showBookings()
//    {
//        bookingManager.clearAll();
//
//        final Intent intent = new Intent(getActivity(), BookingsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }

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
