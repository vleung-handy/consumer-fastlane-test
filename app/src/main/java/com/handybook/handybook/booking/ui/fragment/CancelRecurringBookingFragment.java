package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.viewmodel.BookingCancelRecurringViewModel;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataSynchronizer;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelRecurringBookingFragment extends InjectedFragment
{
    @Bind(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?

    BookingOptionsSelectView mOptionsView;

    private BookingCancelRecurringViewModel mBookingCancelRecurringViewModel;
    private List<RecurringBooking> mRecurringBookings;
    private Configuration mConfiguration;
    private DataSynchronizer mDataSynchronizer;

    public static CancelRecurringBookingFragment newInstance()
    {
        final CancelRecurringBookingFragment fragment = new CancelRecurringBookingFragment();
        return fragment;
    }

    private void setContentViewVisible(boolean visible)
    {
        if (getView() != null)
        {
            getView().setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        mDataSynchronizer = new DataSynchronizer(2, new DataSynchronizer.Callback()
        {
            @Override
            public void onSuccess()
            {
                displayData();
            }

            @Override
            public void onError(final List<DataManager.DataManagerError> errors)
            {
                //TODO: change other option-based screens to exit if the options data is missing?
                //exit and show toast if we cannot render the options
                showToastAndExit(R.string.default_error_string);
            }
        });
        bus.post(new ConfigurationEvent.RequestConfiguration());
        bus.post(new BookingEvent.RequestRecurringBookings());
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
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

    private void handleCancelRecurringBooking(final RecurringBooking recurringBooking)
    {
        //send the cancel recurring booking email for the series that the user selected
        if (mConfiguration.shouldUseCancelRecurringWebview())
        {
            // load webview using recurringBooking.getCancelUrl();
        }
        else
        {
            showUiBlockers();
            int recurringId = recurringBooking.getId();
            bus.post(new BookingEvent.RequestSendCancelRecurringBookingEmail(recurringId));
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked()
    {
        RecurringBooking selectedBooking = mBookingCancelRecurringViewModel.getBookingForIndex
                (mOptionsView.getCurrentIndex());
        handleCancelRecurringBooking(selectedBooking);
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

    private void handleErrorEvent(final DataManager.DataManagerError error)
    {
        removeUiBlockersAndShowContent();
        dataManagerErrorHandler.handleError(getActivity(), error);
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
            final BookingEvent.ReceiveSendCancelRecurringBookingEmailSuccess event
    )
    {
        removeUiBlockers();
        showEmailSentConfirmationDialog();
    }

    @Subscribe
    public void onReceiveRecurringBookingsSuccess(
            final BookingEvent.ReceiveRecurringBookingsSuccess event
    )
    {
        mRecurringBookings = event.recurringBookings;
        mDataSynchronizer.countDownSuccess();
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        mConfiguration = event.getConfiguration();
        mDataSynchronizer.countDownSuccess();
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailError(
            final BookingEvent.ReceiveSendCancelRecurringBookingEmailError event
    )
    {
        handleErrorEvent(event.error);
    }

    @Subscribe
    public void onReceiveRecurringBookingsError(
            final BookingEvent.ReceiveRecurringBookingsError event
    )
    {
        mDataSynchronizer.countDownError(event.error);
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    )
    {
        mDataSynchronizer.countDownError(event.error);
    }

    private void displayData()
    {
        mBookingCancelRecurringViewModel = BookingCancelRecurringViewModel.from(mRecurringBookings);
        int numBookings = mRecurringBookings.size();

        if (numBookings > 0)
        {
            if (mRecurringBookings.size() > 1)
            {
                //allow user to select which recurrence they want to cancel
                createOptionsView();
                removeUiBlockersAndShowContent();
            }
            else
            {
                //send cancellation email for the only recurring booking that user has
                handleCancelRecurringBooking(mRecurringBookings.get(0));
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
}
