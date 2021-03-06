package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
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
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.DataSynchronizer;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Used to display a selection of recurring bookings that the user may want to cancel.
 */
public class CancelRecurringBookingSelectionFragment extends ProgressSpinnerFragment {

    public static final int INITIAL_REQUEST_COUNT = 2;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?

    BookingOptionsSelectView mOptionsView;

    private BookingCancelRecurringViewModel mBookingCancelRecurringViewModel;
    private List<RecurringBooking> mRecurringBookings;
    private Configuration mConfiguration;
    private DataSynchronizer mDataSynchronizer;

    public static CancelRecurringBookingSelectionFragment newInstance() {
        return new CancelRecurringBookingSelectionFragment();
    }

    private void setContentViewVisible(boolean visible) {
        if (getView() != null) {
            getView().setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressSpinner(true);
        mDataSynchronizer = new DataSynchronizer(
                INITIAL_REQUEST_COUNT,
                new DataSynchronizer.Callback() {
                    @Override
                    public void onSuccess() {
                        displayData();
                    }

                    @Override
                    public void onError(final List<DataManager.DataManagerError> errors) {
                        //TODO: change other option-based screens to exit if the options data is missing?
                        //exit and show toast if we cannot render the options
                        showToastAndExit(R.string.default_error_string);
                    }
                }
        );
        bus.post(new ConfigurationEvent.RequestConfiguration());
        bus.post(new BookingEvent.RequestRecurringBookings());
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(
                R.layout.fragment_cancel_recurring_booking_selection,
                container,
                false
        ));

        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.cancel_recurring_booking_title));

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentViewVisible(false); //want the fragment to be invisible until options can be rendered
    }

    private void handleCancelRecurringBooking(
            final RecurringBooking recurringBooking,
            final boolean isFromSelection
    ) {
        if (mConfiguration.shouldUseCancelRecurringWebview()) {
            hideProgressSpinner();
            final String cancelUrl = recurringBooking.getCancelUrl();
            final Fragment fragment = WebViewFragment.newInstance(
                    cancelUrl,
                    getString(R.string.cancel_recurring_booking_title)
            );
            final FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_up, 0, 0, R.anim.slide_down)
                    .add(R.id.fragment_container, fragment);
            if (isFromSelection) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
        else {
            //send the cancel recurring booking email for the series that the user selected
            showProgressSpinner(true);
            int recurringId = recurringBooking.getId();
            bus.post(new BookingEvent.RequestSendCancelRecurringBookingEmail(recurringId));
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClicked() {
        RecurringBooking selectedBooking = mBookingCancelRecurringViewModel.getBookingForIndex
                (mOptionsView.getCurrentIndex());
        handleCancelRecurringBooking(selectedBooking, true);
    }

    private void createOptionsView() {
        //create the options view
        BookingOption bookingOption
                = mBookingCancelRecurringViewModel.getBookingOption(getActivity());
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), R.layout
                .view_booking_select_option_recurring_series, bookingOption, null);
        mOptionsView.hideTitle();
        optionsLayout.removeAllViews(); //TODO: use a stub or replaceview instead
        optionsLayout.addView(mOptionsView);
    }

    private void showEmailSentConfirmationDialog() {
        String userEmailAddress = userManager.getCurrentUser().getEmail();
        EmailCancellationDialogFragment emailCancellationDialogFragment =
                EmailCancellationDialogFragment.newInstance(userEmailAddress);
        emailCancellationDialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    private void handleErrorEvent(final DataManager.DataManagerError error) {
        removeUiBlockersAndShowContent();
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void showToastAndExit(int toastStringResourceId) {
        String toastString = getString(toastStringResourceId);
        showToast(toastString);
        getActivity().finish();
    }

    protected void removeUiBlockersAndShowContent() {
        super.hideProgressSpinner();
        setContentViewVisible(true);
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailSuccess(
            final BookingEvent.ReceiveSendCancelRecurringBookingEmailSuccess event
    ) {
        hideProgressSpinner();
        showEmailSentConfirmationDialog();
    }

    @Subscribe
    public void onReceiveRecurringBookingsSuccess(
            final BookingEvent.ReceiveRecurringBookingsSuccess event
    ) {
        mRecurringBookings = event.recurringBookings;
        mDataSynchronizer.countDownSuccess();
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    ) {
        mConfiguration = event.getConfiguration();
        mDataSynchronizer.countDownSuccess();
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailError(
            final BookingEvent.ReceiveSendCancelRecurringBookingEmailError event
    ) {
        handleErrorEvent(event.error);
    }

    @Subscribe
    public void onReceiveRecurringBookingsError(
            final BookingEvent.ReceiveRecurringBookingsError event
    ) {
        mDataSynchronizer.countDownError(event.error);
    }

    @Subscribe
    public void onReceiveConfigurationError(
            final ConfigurationEvent.ReceiveConfigurationError event
    ) {
        mDataSynchronizer.countDownError(event.error);
    }

    private void displayData() {
        mBookingCancelRecurringViewModel = BookingCancelRecurringViewModel.from(mRecurringBookings);
        int numBookings = mRecurringBookings.size();

        if (numBookings > 0) {
            if (mRecurringBookings.size() > 1) {
                //allow user to select which recurrence they want to cancel
                createOptionsView();
                removeUiBlockersAndShowContent();
            }
            else {
                //send cancellation email for the only recurring booking that user has
                handleCancelRecurringBooking(mRecurringBookings.get(0), false);
            }
        }
        else {
            //this can happen if the user's analytics.recurring_bookings_count > 0
            //but user doesn't actually have any recurring bookings
            //if this logic is reached, then we are using recurring_bookings_count wrong
            showToastAndExit(R.string.cancel_recurring_booking_none_to_cancel_error_msg);
        }
    }
}
