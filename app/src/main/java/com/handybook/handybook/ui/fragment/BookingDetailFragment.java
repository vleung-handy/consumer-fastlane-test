package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingDetailFragment extends InjectedFragment
{
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    @Bind(R.id.booking_detail_view)

    BookingDetailView bookingDetailView;
    private Booking booking;
    private boolean updatedBooking;
    private LinkedHashMap<String, BookingDetailSectionFragment> sectionFragments = new LinkedHashMap<>();
    private FragmentManager childFragmentManager;
    //The on screen back button works as the softkey back button
    private View.OnClickListener backButtonClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            getActivity().onBackPressed();
        }
    };

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
        childFragmentManager = getChildFragmentManager();
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_UPDATED_BOOKING))
            {
                setUpdatedBookingResult();
            }
        }
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        bookingDetailView.backButton.setClickable(false);
        setSectionFragmentInputsEnabled(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        bookingDetailView.backButton.setClickable(true);
        setSectionFragmentInputsEnabled(true);
    }

    private void setSectionFragmentInputsEnabled(boolean enabled)
    {
        bus.post(new HandyEvent.SetBookingDetailSectionFragmentActionControlsEnabled(enabled));
    }

    private void setUpdatedBookingResult()
    {
        updatedBooking = true;
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.UPDATED_BOOKING, booking);
        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, intent);
    }

    @Override
    public final void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO: Should be checking and setting results codes not just request code in case we have
        // functionality that returns to this page on failure
        if (resultCode == ActivityResult.RESULT_RESCHEDULE_NEW_DATE)
        {
            if (data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0) != 0)
            {
                Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
                //TODO: We are manually updating the booking, which is something we should strive
                // to avoid as the client is directly changing the model. API v4 should return the
                // updated booking model
                bookingDetailView.updateDateTimeInfoText(booking, newDate);
                setUpdatedBookingResult();
            }
        } else if (resultCode == ActivityResult.RESULT_BOOKING_CANCELED)
        {
            setCanceledBookingResult();
            getActivity().finish();
        } else if (resultCode == ActivityResult.RESULT_BOOKING_UPDATED)
        {
            //various fields could have been updated like note to pro or entry information, request
            // booking details for this booking and redisplay them
            postBlockingEvent(new HandyEvent.RequestBookingDetails(booking.getId()));
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_detail, container, false);
        ButterKnife.bind(this, view);
        initialize();
        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, updatedBooking);
    }

    private void initialize()
    {
        bookingDetailView.updateDisplay(booking, userManager.getCurrentUser());
        setupClickListeners();
        constructSectionFragments();
        addSectionFragments();
    }

    private void setupClickListeners()
    {
        bookingDetailView.backButton.setOnClickListener(backButtonClicked);
    }

    //Section fragments to display, In display order
    protected void constructSectionFragments()
    {
        sectionFragments.put(
                BookingDetailSectionFragmentProInformation.TAG,
                new BookingDetailSectionFragmentProInformation()
        );
        sectionFragments.put(
                BookingDetailSectionFragmentLaundry.TAG,
                new BookingDetailSectionFragmentLaundry()
        );
        sectionFragments.put(
                BookingDetailSectionFragmentEntryInformation.TAG,
                new BookingDetailSectionFragmentEntryInformation());
        sectionFragments.put(
                BookingDetailSectionFragmentNoteToPro.TAG,
                new BookingDetailSectionFragmentNoteToPro()
        );
        sectionFragments.put(
                BookingDetailSectionFragmentExtras.TAG,
                new BookingDetailSectionFragmentExtras()
        );
        sectionFragments.put(
                BookingDetailSectionFragmentAddress.TAG,
                new BookingDetailSectionFragmentAddress());
        sectionFragments.put(
                BookingDetailSectionFragmentPayment.TAG,
                new BookingDetailSectionFragmentPayment()
        );
        sectionFragments.put(
                BookingDetailSectionFragmentBookingActions.TAG,
                new BookingDetailSectionFragmentBookingActions()
        );

    }

    private void addSectionFragments()
    {
        final FragmentTransaction ft = childFragmentManager.beginTransaction();
        for (Map.Entry<String, BookingDetailSectionFragment> eachEntry : sectionFragments.entrySet())
        {
            final BookingDetailSectionFragment sectionFragment = eachEntry.getValue();
            final String sectionFragmentTag = eachEntry.getKey();
            Bundle args = new Bundle();
            args.putParcelable(BundleKeys.BOOKING, this.booking);
            sectionFragment.setArguments(args);
            ft.add(R.id.section_fragment_container, sectionFragment, sectionFragmentTag);
        }
        ft.commit();
    }

    private void setCanceledBookingResult()
    {
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.CANCELLED_BOOKING, booking);
        getActivity().setResult(ActivityResult.RESULT_BOOKING_CANCELED, intent);
    }

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

    @Subscribe
    public void onReceivePreCancellationInfoSuccess(HandyEvent.ReceivePreCancelationInfoSuccess event)
    {
        enableInputs();
        progressDialog.dismiss();
        Pair<String, List<String>> result = event.result;
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
        booking = event.booking;
        getArguments().putParcelable(BundleKeys.BOOKING, event.booking);
        setUpdatedBookingResult();
        update();
    }

    private void update()
    {
        bookingDetailView.updateDisplay(booking, userManager.getCurrentUser());
        for (BookingDetailSectionFragment eachFragment : sectionFragments.values())
        {
            eachFragment.updateDisplay(booking, userManager.getCurrentUser());
        }
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        enableInputs();
        progressDialog.dismiss();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }
}
