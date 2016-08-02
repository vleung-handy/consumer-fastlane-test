package com.handybook.handybook.booking.bookingedit.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.primitives.Ints;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.ui.fragment.BookingFlowFragment;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingEditFrequencyFragment extends BookingFlowFragment
{
    //TODO: need to consolidate all booking edit fragments with booking flow fragments that are used in booking creation
    private Booking mBooking;
    private BookingQuote mBookingQuote;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout; //TODO: can we use a stub or replaceview for this instead?
    @Bind(R.id.next_button)
    TextView mSaveButton;

    private BookingEditFrequencyViewModel mBookingEditFrequencyViewModel;
    private BookingOptionsSelectView mOptionsView;

    public static BookingEditFrequencyFragment newInstance(Booking booking)
    {
        final BookingEditFrequencyFragment fragment = new BookingEditFrequencyFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mixpanel.trackEventAppTrackFrequency();
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mBookingQuote = bookingManager.getCurrentQuote();

        if (bookingManager.getCurrentQuote() != null && bookingManager.getCurrentQuote().getRecurrenceOptions() != null)
        {
            List<Integer> recurrenceOptionsList = Ints.asList(bookingManager.getCurrentQuote().getRecurrenceOptions());
            if (recurrenceOptionsList != null)
            {
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingFrequencyShownLog(recurrenceOptionsList)));
            }
        }
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingQuotePageShown()));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new BookingEditEvent.RequestGetEditFrequencyViewModel(Integer.parseInt(mBooking.getId()))); //TODO: investigate why ID is a string?
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater
                .inflate(R.layout.fragment_booking_edit_frequency, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick()
    {
        sendEditFrequencyRequest();
    }

    public void sendEditFrequencyRequest()
    {
        //get the current selected index of the options view
        final int selectedIndex = mOptionsView.getCurrentIndex();
        if (selectedIndex < 0) { return; }

        showUiBlockers();

        //create and set the booking request object
        BookingEditFrequencyRequest bookingEditFrequencyRequest = new BookingEditFrequencyRequest();
        final int frequency = mBookingEditFrequencyViewModel.getFrequencyOptionValue(selectedIndex);
        bookingEditFrequencyRequest.setRecurringFrequency(frequency);

        if (mBookingQuote != null)
        {
            float[] prices = mBookingQuote.getPricing(mBooking.getHours(), frequency);
            if (prices != null && prices.length != 0)
            {
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingFrequencySubmittedLog(
                        frequency, Math.round(prices[0] * 100)))); //price is in $
            }
        }

        //post the booking request object
        bus.post(new BookingEditEvent.RequestEditBookingFrequency(
                Integer.parseInt(mBooking.getId()),
                bookingEditFrequencyRequest));
    }

    private void createOptionsView()
    {
        //create the options view
        BookingOption bookingOption = mBookingEditFrequencyViewModel.getBookingOptionFromBooking(
                this.getActivity(), mBooking);
        mOptionsView
                = new BookingOptionsSelectView(getActivity(), bookingOption, null);
        mOptionsView.setCurrentIndex(mBookingEditFrequencyViewModel.getOptionIndexForFrequencyValue());
        mOptionsView.hideTitle();
        optionsLayout.removeAllViews(); //TODO: use a stub or replaceview instead
        optionsLayout.addView(mOptionsView);
    }

    private void setSaveButtonEnabled(boolean enabled)
    {
        mSaveButton.setEnabled(enabled);
    }

    @Override
    protected void showUiBlockers()
    {
        super.showUiBlockers();
        setSaveButtonEnabled(false);
    }

    @Override
    protected void removeUiBlockers()
    {
        super.removeUiBlockers();
        setSaveButtonEnabled(true);
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencySuccess(BookingEditEvent.ReceiveEditBookingFrequencySuccess event)
    {
        removeUiBlockers();
        showToast(R.string.updated_booking_frequency);

        getActivity().setResult(ActivityResult.BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencyError(BookingEditEvent.ReceiveEditBookingFrequencyError event)
    {
        onReceiveErrorEvent(event);
        removeUiBlockers(); //allow user to try again
    }

    @Subscribe
    public final void onReceiveEditFrequencyViewModelSuccess(
            BookingEditEvent.ReceiveGetEditFrequencyViewModelSuccess event
    )
    {
        mBookingEditFrequencyViewModel = event.bookingEditFrequencyViewModel;
        createOptionsView();
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveEditFrequencyViewModelError(
            BookingEditEvent.ReceiveGetEditFrequencyViewModelError event
    )
    {
        onReceiveErrorEvent(event);
        setSaveButtonEnabled(false); //don't allow user to save if options data is invalid
    }
}
