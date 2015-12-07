package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.viewmodel.BookingCancelRecurringViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelRecurringBookingFragment extends InjectedFragment
{
    //TODO: clean up
    @Bind(R.id.subtitle_text)
    TextView mSubtitleText;

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
        bus.post(new HandyEvent.RequestRecurringBookingsForUser(userManager.getCurrentUser()));
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
        mSubtitleText.setText(R.string.cancel_regular_cleanings_subtitle);
        setContentViewVisible(false); //want the fragment to be invisible until options can be rendered
    }

    private void sendCancelRecurringBookingEmail(Booking booking)
    {
        //get selected recurring series
        //TODO: why is booking's recurringId type Long?
        if(booking.getRecurringId() == null)
        {
            Crashlytics.logException(new Exception("Recurring id for booking #" + booking.getId() +
                    " is null!"));
            showToastAndExit(R.string.default_error_string);
            return;
        }
        showUiBlockers();
        int recurringId = (int)(booking.getRecurringId().longValue());
        bus.post(new HandyEvent.RequestSendCancelRecurringBookingEmail(recurringId));
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        Booking selectedBooking = mBookingCancelRecurringViewModel.getBookingForIndex
                (mOptionsView.getCurrentIndex());
        sendCancelRecurringBookingEmail(selectedBooking);
    }

    private void createOptionsView()
    {
        //create the options view
        BookingOption bookingOption = mBookingCancelRecurringViewModel.getBookingOption(getActivity());
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), R.layout
                .view_booking_select_option_booking, bookingOption, null);
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
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void showToastAndExit(int toastStringResourceId)
    {
        String toastString = getString(toastStringResourceId);
        showToast(toastString);
        getActivity().finish();
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailSuccess(
            HandyEvent.ReceiveSendCancelRecurringBookingEmailSuccess event)
    {
        removeUiBlockers();
        showEmailSentConfirmationDialog();
    }

    @Override
    protected void removeUiBlockers()
    {
        super.removeUiBlockers();
        setContentViewVisible(true);
    }

    @Subscribe
    public void onReceiveRecurringBookingsSuccess(HandyEvent.ReceiveRecurringBookingsSuccess event)
    {
        mBookingCancelRecurringViewModel = BookingCancelRecurringViewModel.from(event.bookings);
        int numBookings = event.bookings.size();

        if(numBookings > 0)
        {
            if(event.bookings.size() > 1)
            {
                //allow user to select which recurrence they want to cancel
                createOptionsView();
                removeUiBlockers();
            }
            else
            {
                //send cancellation email for the only recurring booking that user has
                sendCancelRecurringBookingEmail(event.bookings.get(0));
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
            HandyEvent.ReceiveSendCancelRecurringBookingEmailError event)
    {
        handleErrorEvent(event);
        removeUiBlockers();
    }

    @Subscribe
    public void onReceiveRecurringBookingsError(HandyEvent.ReceiveRecurringBookingsError event)
    {
        //TODO: change other option-based screens to exit if the options data is missing?
        //exit and show toast if we cannot render the options
        showToastAndExit(R.string.default_error_string);
    }
}
