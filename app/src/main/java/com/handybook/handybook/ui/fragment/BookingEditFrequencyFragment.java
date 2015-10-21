package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingPricesForFrequenciesResponse;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.BookingUpdateFrequencyTransaction;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.squareup.otto.Subscribe;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditFrequencyFragment extends BookingFlowFragment
{
    //TODO: need to consolidate all booking edit fragments with booking flow fragments that are used in booking creation
    private BookingUpdateFrequencyTransaction bookingUpdateFrequencyTransaction;
    private Booking booking;
    private int[] recurValues;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;

//    private BookingOptionsSelectView mOptionsView;

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
        booking = getArguments().getParcelable(BundleKeys.BOOKING);
        initTransaction();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HandyEvent.RequestGetBookingPricesForFrequencies(Integer.parseInt(booking.getId()))); //TODO: investigate why ID is a string?
    }

    private void initTransaction()
    {
        bookingUpdateFrequencyTransaction = new BookingUpdateFrequencyTransaction();
        bookingUpdateFrequencyTransaction.setRecurringFrequency(0);
    }


    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);

        ButterKnife.bind(this, view);
        nextButton.setText(R.string.update);
        nextButton.setOnClickListener(nextClicked);
        return view;
    }

    @Override
    protected final void disableInputs()
    {
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    private final View.OnClickListener nextClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View view)
        {
            showUiBlockers();
            bus.post(new HandyEvent.RequestUpdateBookingFrequency(Integer.parseInt(booking.getId()), bookingUpdateFrequencyTransaction));
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            bookingUpdateFrequencyTransaction.setRecurringFrequency(recurValues[index]);
            if (bookingManager.getCurrentTransaction() != null)
            {
                bookingManager.getCurrentTransaction().setRecurringFrequency(recurValues[index]);
            }
        }

        @Override
        public void onShowChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }

        @Override
        public void onHideChildren(final BookingOptionsView view,
                                   final String[] items)
        {
        }
    };

    //nearly completely copy-pasted from BookingRecurrenceFragment
    //TODO: the discounts displayed here may seem misleading because the price for the one-time booking will not be displayed for user to compare. what to do?
    private String[] getSavingsInfo(Map<Integer, float[]> priceMap)
    {
        final String[] info = new String[4];
        final float prices[] = priceMap.get(0);//get price for one-time booking
        final float price = prices[0];
        final float discount = prices[1];

        for (int i = 0; i < 3; i++) {
            final float recurPrices[] = priceMap.get(freqForIndex(i));
            final float recurPrice = recurPrices[0];
            final float recurDiscount = recurPrices[1];

            int percent;
            if (recurPrice != recurDiscount)
                percent = (int)((discount - recurDiscount) / discount * 100);
            else percent = (int)((price - recurPrice) / price * 100);

            if (percent > 0) info[i] = getString(R.string.save) + " " + percent + "%";
        }
        return info;
    }

    private int indexForFreq(final int freq)
    {
        switch (freq)
        {
            case 1:
                return 0;

            case 2:
                return 1;

            case 4:
                return 2;

            default:
                return 3;
        }
    }

    private int freqForIndex(final int index)
    {
        switch (index)
        {
            case 0:
                return 1;

            case 1:
                return 2;

            case 2:
                return 4;

            default:
                return 0;
        }
    }

    private void showUiBlockers()
    {
        disableInputs();
        progressDialog.show();
    }

    private void removeUiBlockers()
    {
        enableInputs();
        progressDialog.dismiss();
    }

    private void updateUiWithBookingPricesForFrequencies(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
    {
        //update the discount % display to the right of the options text
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setDefaultValue("0");

        option.setOptions(new String[]{getString(R.string.every_week),
                getString(R.string.every_two_weeks), getString(R.string.every_four_weeks)});

        recurValues = new int[]{1, 2, 4}; //allowing edit frequency only for recurring bookings

        option.setOptionsSubText(new String[]
                {null, getString(R.string.most_popular), null});

        option.setOptionsRightText(getSavingsInfo(bookingPricesForFrequenciesResponse.getPriceMap())); //TODO: need BookingQuote's price table to get savings info

        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        optionsView.hideTitle();

        optionsLayout.removeAllViews();
        optionsLayout.addView(optionsView, 0);


        //this is gross, need to call bookingManager.setCurrentTransaction because BookingHeaderFragment uses bookingManager.getCurrentTransaction to do a lot of things
        BookingTransaction bookingTransaction = new BookingTransaction();
        bookingTransaction.setHours(booking.getHours());
        bookingTransaction.setRecurringFrequency(bookingPricesForFrequenciesResponse.getCurrentFrequency());
        optionsView.setCurrentIndex(indexForFreq(bookingPricesForFrequenciesResponse.getCurrentFrequency()));
        bookingTransaction.setStartDate(booking.getStartDate());
        bookingManager.setCurrentTransaction(bookingTransaction);

        //set the header fragment. need to do it here because we need the price for each booking frequency
        final BookingHeaderFragment header = BookingHeaderFragment.newInstance(bookingPricesForFrequenciesResponse);
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();
    }

    private void onReceiveErrorEvent(HandyEvent.ReceiveErrorEvent event)
    {
        removeUiBlockers();
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencySuccess(HandyEvent.ReceiveUpdateBookingFrequencySuccess event)
    {
        removeUiBlockers();
        showToast(R.string.updated_booking_frequency);

        getActivity().setResult(ActivityResult.RESULT_BOOKING_UPDATED, new Intent());
        getActivity().finish();
    }

    @Subscribe
    public final void onReceiveUpdateBookingFrequencyError(HandyEvent.ReceiveUpdateBookingFrequencyError event)
    {
        onReceiveErrorEvent(event);
    }

    @Subscribe
    public final void onReceiveBookingPricesForFrequenciesSuccess(HandyEvent.ReceiveGetBookingPricesForFrequenciesSuccess event)
    {
        updateUiWithBookingPricesForFrequencies(event.bookingPricesForFrequenciesResponse);
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveBookingPricesForFrequenciesError(HandyEvent.ReceiveGetBookingPricesForFrequenciesError event)
    {
        onReceiveErrorEvent(event);
    }
}
