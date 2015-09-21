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
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentNoteToPro;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
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

        setupForBooking(this.booking);

        return view;
    }


    //In display order
    protected List<BookingDetailSectionFragment> constructSectionFragments(Booking booking)
    {
        return Lists.newArrayList(
                new BookingDetailSectionFragmentProInformation(),
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
        clearSectionFragments();

        List<BookingDetailSectionFragment> sectionFragments = constructSectionFragments(this.booking);

        //These are fragments nested inside this fragment, must use getChildFragmentManager instead of getFragmentManager
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

    private void clearSectionFragments()
    {
        bookingDetailView.sectionFragmentContainer.removeAllViews();
    }

    private void setupClickListeners(Booking booking)
    {
        bookingDetailView.backButton.setOnClickListener(backButtonClicked);
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
        bookingDetailView.backButton.setClickable(false);
        bookingDetailView.rescheduleButton.setClickable(false);
        bookingDetailView.cancelButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        bookingDetailView.backButton.setClickable(true);
        bookingDetailView.rescheduleButton.setClickable(true);
        bookingDetailView.cancelButton.setClickable(true);
    }

    @Override
    public final void onActivityResult(final int requestCode,
                                       final int resultCode,
                                       final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO: Should be checking and setting results codes not just request code in case we have functionality that returns to this page on failure

        if (resultCode == ActivityResult.RESULT_RESCHEDULE_NEW_DATE)
        {
           Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
            //TODO: We are manually updating the booking, which is something we should strive to avoid as the client is directly changing the model. API v4 should return the updated booking model
           bookingDetailView.updateDateTimeInfoText(booking, newDate);
           setUpdatedBookingResult();
        }
        else if (resultCode == ActivityResult.RESULT_BOOKING_CANCELED)
        {
            setCanceledBookingResult();
            getActivity().finish();
        }
        else if (resultCode == ActivityResult.RESULT_BOOKING_UPDATED)
        {
            //various fields could have been updated like note to pro or entry information, request booking details for this booking and redisplay them
            postBlockingEvent(new HandyEvent.RequestBookingDetails(booking.getId()));
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, updatedBooking);
    }

    private View.OnClickListener rescheduleClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            postBlockingEvent(new HandyEvent.RequestPreRescheduleInfo(booking.getId()));
        }
    };

    private View.OnClickListener backButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            getActivity().onBackPressed();
        }
    };


    @Subscribe
    public void onReceivePreRescheduleInfoSuccess(HandyEvent.ReceivePreRescheduleInfoSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, this.booking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        startActivityForResult(intent, ActivityResult.RESULT_RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(HandyEvent.ReceivePreRescheduleInfoError event)
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
            postBlockingEvent(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
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
        startActivityForResult(intent, ActivityResult.RESULT_BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancelationInfoError(HandyEvent.ReceivePreCancelationInfoError event)
    {
        enableInputs();
        progressDialog.dismiss();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();

        this.booking = event.booking;
        getArguments().putParcelable(BundleKeys.BOOKING, event.booking);
        setUpdatedBookingResult();
        setupForBooking(event.booking);
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        enableInputs();
        progressDialog.dismiss();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private final void setUpdatedBookingResult()
    {
        updatedBooking = true;
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.UPDATED_BOOKING, booking);
        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, intent);
    }

    private final void setCanceledBookingResult()
    {
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.CANCELLED_BOOKING, booking);
        getActivity().setResult(ActivityResult.RESULT_BOOKING_CANCELED, intent);
    }

    private void setupForBooking(Booking booking)
    {
        bookingDetailView.updateDisplay(booking, userManager.getCurrentUser());
        setupClickListeners(booking);
        addSectionFragments();
    }

}
