package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.viewmodel.BookingCancelRecurringViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelRecurringBookingFragment extends InjectedFragment
{
    @Bind(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?

    BookingOptionsSelectView mOptionsView;

    BookingCancelRecurringViewModel mBookingCancelRecurringViewModel;

    public static CancelRecurringBookingFragment newInstance()
    {
        final CancelRecurringBookingFragment fragment = new CancelRecurringBookingFragment();
        return fragment;
    }

    private void setContentViewVisible(boolean visible)
    {
        if(getView() != null)
        {
            getView().setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new BookingEvent.RequestRecurringBookingsForUser(userManager.getCurrentUser()));
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_cancel_recurring_booking, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setContentViewVisible(false); //want the fragment to be invisible until options can be rendered
    }

    private void sendCancelRecurringBookingEmail(RecurringBooking recurringBooking)
    {
        //send the cancel recurring booking email for the series that the user selected
        showUiBlockers();
        int recurringId = recurringBooking.getRecurringId();
        bus.post(new BookingEvent.RequestSendCancelRecurringBookingEmail(recurringId));
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        RecurringBooking selectedBooking = mBookingCancelRecurringViewModel.getBookingForIndex
                (mOptionsView.getCurrentIndex());
        sendCancelRecurringBookingEmail(selectedBooking);
    }

    private void createOptionsView()
    {
        //create the options view
        BookingOption bookingOption = mBookingCancelRecurringViewModel.getBookingOption(getActivity());
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), R.layout
                .view_booking_select_option_recurring_series, bookingOption, null);
        mOptionsView.hideTitle();
        optionsLayout.removeAllViews(); //TODO: use a stub or replaceview instead
        optionsLayout.addView(mOptionsView);
    }

    private void showEmailSentConfirmationDialog()
    {
        String userEmailAddress = userManager.getCurrentUser().getEmail();
        EmailCancellationDialogFragment emailCancellationDialogFragment =
                EmailCancellationDialogFragment.newInstance(userEmailAddress);
        emailCancellationDialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    private void handleErrorEvent(HandyEvent.ReceiveErrorEvent event)
    {
        removeUiBlockersAndShowContent();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void showToastAndExit(int toastStringResourceId)
    {
        String toastString = getString(toastStringResourceId);
        showToast(toastString);
        getActivity().finish();
    }

    protected void removeUiBlockersAndShowContent()
    {
        super.removeUiBlockers();
        setContentViewVisible(true);
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailSuccess(
            BookingEvent.ReceiveSendCancelRecurringBookingEmailSuccess event)
    {
        removeUiBlockers();
        showEmailSentConfirmationDialog();
    }

    @Subscribe
    public void onReceiveRecurringBookingsSuccess(BookingEvent.ReceiveRecurringBookingsSuccess event)
    {
        mBookingCancelRecurringViewModel = BookingCancelRecurringViewModel.from(event.recurringBookings);
        int numBookings = event.recurringBookings.size();

        if(numBookings > 0)
        {
            if(event.recurringBookings.size() > 1)
            {
                //allow user to select which recurrence they want to cancel
                createOptionsView();
                removeUiBlockersAndShowContent();
            }
            else
            {
                //send cancellation email for the only recurring booking that user has
                sendCancelRecurringBookingEmail(event.recurringBookings.get(0));
            }
        }
        else
        {
            //this can happen if the user's analytics.recurring_bookings_count > 0
            //but user doesn't actually have any recurring bookings
            //if this logic is reached, then we are using recurring_bookings_count wrong
            showToastAndExit(R.string.cancel_recurring_booking_none_to_cancel_error_msg);
        }
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailError(
            BookingEvent.ReceiveSendCancelRecurringBookingEmailError event)
    {
        handleErrorEvent(event);
    }

    @Subscribe
    public void onReceiveRecurringBookingsError(BookingEvent.ReceiveRecurringBookingsError event)
    {
        //TODO: change other option-based screens to exit if the options data is missing?
        //exit and show toast if we cannot render the options
        showToastAndExit(R.string.default_error_string);
    }
}
