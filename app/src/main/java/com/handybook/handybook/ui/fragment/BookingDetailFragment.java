package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.common.collect.Lists;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BookingAction;
import com.handybook.handybook.constant.BookingActionButtonType;
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
import com.handybook.handybook.ui.widget.BookingActionButton;
import com.handybook.handybook.util.Utils;
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
          //bookingDetailView.rescheduleButton.setOnClickListener(rescheduleClicked);
          //bookingDetailView.cancelButton.setOnClickListener(cancelClicked);
        }
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        bookingDetailView.backButton.setClickable(false);
        setActionButtonsEnabled(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        bookingDetailView.backButton.setClickable(true);
        setActionButtonsEnabled(true);
    }

    private void setActionButtonsEnabled(boolean enabled)
    {
        for(int i = 0; i < bookingDetailView.actionButtonsLayout.getChildCount(); i++)
        {
            BookingActionButton actionButton = (BookingActionButton) bookingDetailView.actionButtonsLayout.getChildAt(i);
            if(actionButton != null)
            {
                actionButton.setEnabled(enabled);
            }
        }
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
        setupBookingActionButtons(booking);
    }

    private void setupBookingActionButtons(Booking booking)
    {
        clearBookingActionButtons();

        List<String> actionButtonTypes = getActionButtonTypeList(booking);

        for(String actionButtonType : actionButtonTypes)
        {
            BookingActionButtonType bookingActionButtonType = Utils.getBookingActionButtonType(actionButtonType);
            if(bookingActionButtonType != null)
            {
                ViewGroup buttonParentLayout = getParentLayoutForButtonActionType(bookingActionButtonType);
                if(buttonParentLayout != null)
                {
                    int newChildIndex = buttonParentLayout.getChildCount(); //new index is equal to the old count since the new count is +1
                    BookingActionButton bookingActionButton = (BookingActionButton) ((ViewGroup) getActivity().getLayoutInflater().inflate(bookingActionButtonType.getLayoutTemplateId(), buttonParentLayout)).getChildAt(newChildIndex);
                    View.OnClickListener onClickListener = getOnClickListenerForAction(actionButtonType);
                    bookingActionButton.init(actionButtonType, onClickListener);
                }
            }
        }
    }

    private void clearBookingActionButtons()
    {
        LinearLayout buttonsContainer = bookingDetailView.actionButtonsLayout;
        buttonsContainer.removeAllViews();
    }

    //List of action button types in display order
    private List<String> getActionButtonTypeList(Booking booking)
    {
        List<String> actionButtonTypes = new ArrayList<>();

        //TODO: Get this from server like we do for portal

        //TODO: Is there an additional time restriction on when these actions can be taken?

        if(!booking.isPast())
        {
            actionButtonTypes.add(BookingAction.ACTION_RESCHEDULE);
            actionButtonTypes.add(BookingAction.ACTION_CANCEL);
        }

        //TODO: Is there a time restriction on when these actions can be taken?

        //these buttons need to go in a different container

//        if(booking.hasAssignedProvider())
//        {
//            actionButtonTypes.add(BookingAction.ACTION_CONTACT_PHONE);
//            actionButtonTypes.add(BookingAction.ACTION_CONTACT_TEXT);
//        }

        return actionButtonTypes;
    }

    private View.OnClickListener getOnClickListenerForAction(String actionButtonType)
    {
        switch(actionButtonType)
        {
            case BookingAction.ACTION_CANCEL: return cancelClicked;
            case BookingAction.ACTION_RESCHEDULE: return rescheduleClicked;
            case BookingAction.ACTION_CONTACT_TEXT: return contactTextClicked;
            case BookingAction.ACTION_CONTACT_PHONE: return contactPhoneClicked;
        }
        return null;
    }

    private View.OnClickListener cancelClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            postBlockingEvent(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
        }
    };

    private View.OnClickListener rescheduleClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            postBlockingEvent(new HandyEvent.RequestPreRescheduleInfo(booking.getId()));
        }
    };

    private View.OnClickListener contactTextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            //TODO: Text message to provider if possible
           // postBlockingEvent(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
        }
    };

    private View.OnClickListener contactPhoneClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            //TODO: Call provider phone if possible
            //postBlockingEvent(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
        }
    };


    //Mapping for ButtonActionType to Parent Layout, used when adding Action Buttons dynamically
    private ViewGroup getParentLayoutForButtonActionType(BookingActionButtonType buttonActionType)
    {
        if(buttonActionType == null)
        {
            return null;
        }

        switch (buttonActionType)
        {
            case RESCHEDULE:
            case CANCEL:
            {
                return bookingDetailView.actionButtonsLayout;
            }

            case CONTACT_PHONE: return null; //(ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout_slot_1);
            case CONTACT_TEXT: return null; //(ViewGroup) contactLayout.findViewById(R.id.booking_details_contact_action_button_layout_slot_2);

            default:
            {
                Crashlytics.log("Could not find parent layout for button action type : " + buttonActionType.toString());
                return null;
            }
        }
    }



}
