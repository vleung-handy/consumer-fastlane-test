package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.viewmodel.BookingCancelRecurringViewModel;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CancelRecurringBookingFragment extends InjectedFragment
{
    //TODO: clean up
    @Bind(R.id.subtitle_text)
    TextView mSubtitleText;
    @Bind(R.id.next_button)
    Button mSaveButton;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?

    BookingOptionsSelectView mOptionsView;

    BookingCancelRecurringViewModel mBookingCancelRecurringViewModel;

    public static CancelRecurringBookingFragment newInstance()
    {
        final CancelRecurringBookingFragment fragment = new CancelRecurringBookingFragment();
        return fragment;
    }

    private void setFragentVisible(boolean visible)
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
        mSubtitleText.setText(R.string.cancel_regular_cleanings_subtitle);
        //TODO: use onclick annotation
        view.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setFragentVisible(false);
    }

    private void sendCancelRecurringBookingEmail(Booking booking)
    {
        //get selected booking
        //TODO: wtf is booking's recurringId type Long?
        showUiBlockers();
        int recurringId = (int)(booking.getRecurringId().longValue());
        bus.post(new HandyEvent.RequestSendCancelRecurringBookingEmail(recurringId));
    }

    private void createOptionsView()
    {
        //create the options view
        BookingOption bookingOption = mBookingCancelRecurringViewModel.getBookingOption();
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), R.layout
                .view_booking_select_option_booking, bookingOption, null);
        mOptionsView.hideTitle();
        optionsLayout.removeAllViews(); //TODO: use a stub or replaceview instead
        optionsLayout.addView(mOptionsView);

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                int selectedIndex = mOptionsView.getCurrentIndex();
                sendCancelRecurringBookingEmail(mBookingCancelRecurringViewModel.getBookingForIndex
                        (selectedIndex));

            }
        });
    }

    private void showEmailSentConfirmationDialog()
    {
        String userEmailAddress = userManager.getCurrentUser().getEmail();
        EmailCancellationDialogFragment emailCancellationDialogFragment = EmailCancellationDialogFragment.newInstance(userEmailAddress);
        emailCancellationDialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    private void handleErrorEvent(HandyEvent.ReceiveErrorEvent event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    private void setSaveButtonEnabled(boolean enabled)
    {
        mSaveButton.setEnabled(enabled);
    }

    @Subscribe
    public void onReceiveSendCancelRecurringBookingEmailSuccess(
            HandyEvent.ReceiveSendCancelRecurringBookingEmailSuccess event)
    {
        removeUiBlockers();
        showEmailSentConfirmationDialog();
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
                setFragentVisible(true);
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
            //TODO: put in strings.xml
            showToast("No recurring bookings to cancel");
            getActivity().finish();
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
//        handleErrorEvent(event);
//        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid

        //exit and show toast
        showToast("Sorry, there was an error fetching your recurring bookings. Please try again " +
                "later.");
        getActivity().finish();
    }
}
