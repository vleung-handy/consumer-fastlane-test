package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.booking.ui.activity.BookingCancelOptionsActivity;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentAddress;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentBookingActions;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentEntryInformation;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentExtras;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentLaundry;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentNoteToPro;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentPayment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment.BookingDetailSectionFragmentProInformation;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.booking.ui.view.BookingDetailView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingDetailFragment extends InjectedFragment implements PopupMenu.OnMenuItemClickListener
{
    private static final String STATE_UPDATED_BOOKING = "STATE_UPDATED_BOOKING";

    private Booking mBooking;
    private String mBookingId;
    private boolean mBookingUpdated;

    @Bind(R.id.booking_detail_view)
    BookingDetailView mBookingDetailView;
    @Bind(R.id.nav_help)
    TextView mHelp;

    public static BookingDetailFragment newInstance(final Booking booking)
    {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookingDetailFragment newInstance(final String bookingId)
    {
        final BookingDetailFragment fragment = new BookingDetailFragment();
        final Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackDetails();
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mBookingId = getArguments().getString(BundleKeys.BOOKING_ID);

        if (savedInstanceState != null)
        {
            if (savedInstanceState.getBoolean(STATE_UPDATED_BOOKING))
            {
                setUpdatedBookingResult();
            }
        }
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        ButterKnife.bind(this, view);
        if (mBooking != null)
        {
            setupForBooking(mBooking);
        }
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mBooking == null)
        {
            showUiBlockers();
            bus.post(new HandyEvent.RequestBookingDetails(mBookingId));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.panic_menu, menu);
    }

    @Override
    public final void onActivityResult(final int requestCode,
                                       final int resultCode,
                                       final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO: Should be checking and setting results codes not just request code in case we have functionality that returns to this page on failure

        if (resultCode == ActivityResult.RESCHEDULE_NEW_DATE)
        {
            if (data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0) != 0)
            {
                Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
                //TODO: We are manually updating the booking, which is something we should strive to avoid as the client is directly changing the model. API v4 should return the updated booking model
                mBookingDetailView.updateDateTimeInfoText(mBooking, newDate);
                setUpdatedBookingResult();
            }
        }
        else if (resultCode == ActivityResult.BOOKING_CANCELED)
        {
            setCanceledBookingResult();
            getActivity().finish();
        }
        else if (resultCode == ActivityResult.BOOKING_UPDATED)
        {
            //various fields could have been updated like note to pro or entry information, request booking details for this booking and redisplay them
            postBlockingEvent(new HandyEvent.RequestBookingDetails(mBooking.getId()));
            //setting the updated result with the new booking when we receive the new booking data
        }
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_UPDATED_BOOKING, mBookingUpdated);
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        mBookingDetailView.backButton.setClickable(false);
        setSectionFragmentInputsEnabled(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        mBookingDetailView.backButton.setClickable(true);
        setSectionFragmentInputsEnabled(true);
    }


    @OnClick(R.id.nav_help)
    void onHelpClicked(final View view)
    {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.panic_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }


    private void setSectionFragmentInputsEnabled(boolean enabled)
    {
        bus.post(new HandyEvent.SetBookingDetailSectionFragmentActionControlsEnabled(enabled));
    }

    private void setupForBooking(Booking booking)
    {
        mHelp.setVisibility(shouldShowPanicButtons(mBooking) ? View.VISIBLE : View.INVISIBLE);
        mBookingDetailView.updateDisplay(booking, userManager.getCurrentUser());
        setupClickListeners();
        addSectionFragments();
    }

    private void setupClickListeners()
    {
        mBookingDetailView.backButton.setOnClickListener(backButtonClicked);
    }

    //Section fragments to display, In display order
    protected List<BookingDetailSectionFragment> constructSectionFragments()
    {
        return Lists.newArrayList(
                new BookingDetailSectionFragmentProInformation(),
                new BookingDetailSectionFragmentLaundry(),
                new BookingDetailSectionFragmentEntryInformation(),
                new BookingDetailSectionFragmentNoteToPro(),
                new BookingDetailSectionFragmentExtras(),
                new BookingDetailSectionFragmentAddress(),
                new BookingDetailSectionFragmentPayment(),
                new BookingDetailSectionFragmentBookingActions()
        );
    }

    private void addSectionFragments()
    {
        clearSectionFragments();

        List<BookingDetailSectionFragment> sectionFragments = constructSectionFragments();

        //These are fragments nested inside this fragment, must use getChildFragmentManager instead of getFragmentManager
        for (BookingDetailSectionFragment sectionFragment : sectionFragments)
        {
            //Normally we would bundle all of these adds into one transaction but there is a bug
            //  with the fragment manager which displays them in reverse order if fragments were just cleared
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putParcelable(BundleKeys.BOOKING, mBooking);
            sectionFragment.setArguments(args);
            transaction.add(R.id.section_fragment_container, sectionFragment);
            transaction.commit();
        }

    }

    private void clearSectionFragments()
    {
        //Remove all of the child fragments for this fragment
        List<Fragment> childFragments = getChildFragmentManager().getFragments();
        if (childFragments != null && childFragments.size() > 0)
        {
            FragmentTransaction removalTransaction = getChildFragmentManager().beginTransaction();
            for (Fragment frag : childFragments)
            {
                if (!(frag == null || frag.isDetached() || frag.isRemoving()))
                {
                    removalTransaction.remove(frag);
                }
            }
            removalTransaction.commit();
        }
    }

    //The on screen back button works as the softkey back button
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
        removeUiBlockers();

        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, mBooking);
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onReceivePreRescheduleInfoError(HandyEvent.ReceivePreRescheduleInfoError event)
    {
        removeUiBlockers();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceivePreCancelationInfoSuccess(HandyEvent.ReceivePreCancelationInfoSuccess event)
    {
        removeUiBlockers();

        Pair<String, List<String>> result = event.result;

        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
        intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>(result.second));
        intent.putExtra(BundleKeys.NOTICE, result.first);
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        startActivityForResult(intent, ActivityResult.BOOKING_CANCELED);
    }

    @Subscribe
    public void onReceivePreCancelationInfoError(HandyEvent.ReceivePreCancelationInfoError event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public void onReceiveBookingDetailsSuccess(HandyEvent.ReceiveBookingDetailsSuccess event)
    {
        removeUiBlockers();

        mBooking = event.booking;
        getArguments().putParcelable(BundleKeys.BOOKING, event.booking);
        setUpdatedBookingResult();
        setupForBooking(event.booking);
    }

    @Subscribe
    public void onReceiveBookingDetailsError(HandyEvent.ReceiveBookingDetailsError event)
    {
        removeUiBlockers();

        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void setUpdatedBookingResult()
    {
        mBookingUpdated = true;
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.UPDATED_BOOKING, mBooking);
        getActivity().setResult(ActivityResult.BOOKING_UPDATED, intent);
    }

    private void setCanceledBookingResult()
    {
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.CANCELLED_BOOKING, mBooking);
        getActivity().setResult(ActivityResult.BOOKING_CANCELED, intent);
    }

    private boolean shouldShowPanicButtons(final Booking booking)
    {
        if(booking == null){return false;}
        final Date now = new Date();

        final GregorianCalendar periodStart = new GregorianCalendar();
        periodStart.setTime(booking.getStartDate());
        periodStart.add(Calendar.HOUR, -1); // An hour before

        final GregorianCalendar periodEnd = new GregorianCalendar();
        periodEnd.setTime(booking.getEndDate());
        periodEnd.add(Calendar.MINUTE, 15); //And 15 minutes after
        return periodStart.getTime().before(now) && periodEnd.getTime().after(now);
    }


    @Override
    public boolean onMenuItemClick(final MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_panic_cancel:
                startActivity(HelpActivity.getIntentToOpenNodeId(
                        getActivity(),
                        HelpActivity.HELP_NODE_ID_CANCEL
                ));
                break;
            case R.id.menu_panic_pro_late:
                startActivity(HelpActivity.getIntentToOpenNodeId(
                        getActivity(),
                        HelpActivity.HELP_NODE_ID_PRO_LATE
                ));
                break;
            case R.id.menu_panic_adjust_hours:
                startActivity(HelpActivity.getIntentToOpenNodeId(
                        getActivity(),
                        HelpActivity.HELP_NODE_ID_ADJUST_HOURS
                ));
                break;
            case R.id.menu_panic_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
            default:
                return false;
        }
        return true;
    }
}
