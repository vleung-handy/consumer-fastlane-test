package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.view.BookingDetailView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class BookingDetailFragment extends BookingFlowFragment
{
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    private Booking booking;
    private boolean updatedBooking;

    @InjectView(R.id.booking_detail_view)
    BookingDetailView bookingDetailView;

    public static BookingDetailFragment newInstance(final Booking booking)
    {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.booking = getArguments().getParcelable(BundleKeys.BOOKING);

        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_UPDATED_BOOKING))
            {
                setUpdatedBookingResult();
            }
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_detail, container, false);

        ButterKnife.inject(this, view);

        bookingDetailView.updateDisplay(this.booking, userManager.getCurrentUser());

        setupClickListeners(this.booking);

        return view;
    }

    private void setupClickListeners(Booking booking)
    {
        if (!booking.isPast())
        {
          bookingDetailView.rescheduleButton.setOnClickListener(rescheduleClicked);
          bookingDetailView.cancelButton.setOnClickListener(cancelClicked);
        }
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();

        bookingDetailView.rescheduleButton.setClickable(false);
        bookingDetailView.cancelButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();

        bookingDetailView.rescheduleButton.setClickable(true);
        bookingDetailView.cancelButton.setClickable(true);
    }

    @Override
    public final void onActivityResult(final int requestCode,
                                       final int resultCode,
                                       final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE)
        {
           Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
           bookingDetailView.updateDateTimeInfoText(booking, newDate);
           setUpdatedBookingResult();
        }
        else if (resultCode == BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED)
        {
            setCanceledBookingResult();
            getActivity().finish();
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, updatedBooking);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(HandyEvent.ReceivePreRescheduleInfoSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, this.booking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        startActivityForResult(intent, BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(HandyEvent.ReceivePreRescheduleInfoError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private View.OnClickListener rescheduleClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            disableInputs();
            progressDialog.show();
            bus.post(new HandyEvent.RequestPreRescheduleInfo(booking.getId()));
        }
    };

    @Subscribe
    public void onReceivePreCancelationInfoSuccess(HandyEvent.ReceivePreCancelationInfoSuccess event)
    {
        Pair<String, List<String>> result = event.result;

        enableInputs();
        progressDialog.dismiss();

        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>(result.second));
        intent.putExtra(BundleKeys.NOTICE, result.first);
        intent.putExtra(BundleKeys.BOOKING, booking);
        startActivityForResult(intent, BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancelationInfoError(HandyEvent.ReceivePreCancelationInfoError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }


    private View.OnClickListener cancelClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            disableInputs();
            progressDialog.show();

            bus.post(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
        }
    };



    private final void setUpdatedBookingResult()
    {
        updatedBooking = true;

        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, booking);
        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_UPDATED, intent);
    }

    private final void setCanceledBookingResult()
    {
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.CANCELLED_BOOKING, booking);
        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_CANCELED, intent);
    }

}
