package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentNoteToPro;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProfessionalInformation;
import com.handybook.handybook.ui.view.BookingDetailView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingDetailFragment extends BookingFlowFragment
{
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    private Booking booking;
    private boolean updatedBooking;

    @Bind(R.id.booking_detail_view)
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

        ButterKnife.bind(this, view);


        bookingDetailView.updateDisplay(this.booking, userManager.getCurrentUser());

        setupClickListeners(this.booking);

        addSectionFragments();

        return view;
    }


    //In display order
    protected List<BookingDetailSectionFragment> constructSectionFragments(Booking booking)
    {
        return Lists.newArrayList(
                new BookingDetailSectionFragmentProfessionalInformation(),
                new BookingDetailSectionFragmentLaundry(),
                new BookingDetailSectionFragmentEntryInformation(),
                new BookingDetailSectionFragmentNoteToPro(),
                new BookingDetailSectionFragmentExtras(),
                new BookingDetailSectionFragmentAddress(),
                new BookingDetailSectionFragmentPayment()
                );
    }

    private void addSectionFragments()
    {
        List<BookingDetailSectionFragment> sectionFragments = constructSectionFragments(this.booking);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        for (BookingDetailSectionFragment sectionFragment : sectionFragments)
        {
            Bundle args = new Bundle();
            args.putParcelable(BundleKeys.BOOKING, this.booking);
            sectionFragment.setArguments(args);
            transaction.add(R.id.section_fragment_container, sectionFragment);
        }

        transaction.commit();
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


    //TODO: Check the results for edit pro note and edit entry information

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
        else if (resultCode == ActivityResult.RESULT_BOOKING_UPDATED)
        {
            //TODO: request update the booking shown, will have side effect of updating cache, when we move to api v4 we will be getting bookings as return values
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
