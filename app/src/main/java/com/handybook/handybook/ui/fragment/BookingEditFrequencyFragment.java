package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BookingFrequency;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingPricesForFrequenciesResponse;
import com.handybook.handybook.core.BookingUpdateFrequencyTransaction;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.widget.BookingOptionsSelectView;
import com.handybook.handybook.ui.widget.BookingOptionsView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingEditFrequencyFragment extends BookingFlowFragment
{
    //TODO: need to consolidate all booking edit fragments with booking flow fragments that are used in booking creation
    private BookingUpdateFrequencyTransaction mBookingUpdateFrequencyTransaction;
    private Booking mBooking;
    private int[] mRecurValues;

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.nav_text)
    TextView navText;

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
        initTransaction();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        showUiBlockers();
        bus.post(new HandyEvent.RequestGetBookingPricesForFrequencies(Integer.parseInt(mBooking.getId()))); //TODO: investigate why ID is a string?
    }

    private void initTransaction()
    {
        mBookingUpdateFrequencyTransaction = new BookingUpdateFrequencyTransaction();
        mBookingUpdateFrequencyTransaction.setRecurringFrequency(0);
    }


    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_booking_recurrence, container, false);

        ButterKnife.bind(this, view);
        ViewGroup infoHeaderHolder = (ViewGroup) view.findViewById(R.id.info_header_layout);
        infoHeaderHolder.removeAllViews();
        TextView newView = (TextView) inflater.inflate(R.layout.view_centered_header_text, null);
        infoHeaderHolder.addView(newView);
        newView.setText(getResources().getString(R.string.how_often_should_come));

        navText.setText(R.string.edit_frequency);
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
            bus.post(new HandyEvent.RequestUpdateBookingFrequency(Integer.parseInt(mBooking.getId()), mBookingUpdateFrequencyTransaction));
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            mBookingUpdateFrequencyTransaction.setRecurringFrequency(mRecurValues[index]);
            if (bookingManager.getCurrentTransaction() != null)
            {
                bookingManager.getCurrentTransaction().setRecurringFrequency(mRecurValues[index]);
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

    //TODO: duplicated from BookingRecurrenceFragment. we shouldn't have to use this kind of logic
    private int indexForFreq(final int freq)
    {
        switch (freq)
        {
            case BookingFrequency.WEEKLY:
                return 0;

            case BookingFrequency.BIMONTHLY:
                return 1;

            case BookingFrequency.MONTHLY:
                return 2;

            default:
                return 3;
        }
    }

    private String[] getOriginalPriceArrayForRecurValues(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
    {
        String[] priceArray = new String[mRecurValues.length];
        //this is string because server returns formatted prices (let's not do that in new api)

        for (int i = 0; i < priceArray.length; i++)
        {
            priceArray[i] = bookingPricesForFrequenciesResponse.getFormattedPriceForFrequency(mRecurValues[i]);
        }
        return priceArray;
    }

    //TODO: move to a place where this can be shared (but NOT to the Booking model)
    private final String getServiceShortName(String serviceMachineName)
    {
        switch (serviceMachineName)
        {
            case Booking.SERVICE_CLEANING:
            case Booking.SERVICE_HOME_CLEANING:
            case Booking.SERVICE_OFFICE_CLEANING:
                return getString(R.string.clean);
            default:
                return getString(R.string.job);
        }
    }

    private BookingOption getBookingOption(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
    {
        //TODO: mostly duplicated from checkout flow fragment, should reconsider redesigning the options logic
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(new String[]{getString(R.string.every_week),
                getString(R.string.every_two_weeks), getString(R.string.every_four_weeks)});
        mRecurValues = new int[]{BookingFrequency.WEEKLY, BookingFrequency.BIMONTHLY, BookingFrequency.MONTHLY}; //allowing edit frequency only for recurring bookings

        //update the options right-hand text views
        int indexForFreq = indexForFreq(bookingPricesForFrequenciesResponse.getCurrentFrequency());
        String optionsSubText[] = new String[mRecurValues.length];
        optionsSubText[indexForFreq] = getString(R.string.current);
        option.setOptionsSubText(optionsSubText);
        option.setOptionsRightTitleText(getOriginalPriceArrayForRecurValues(bookingPricesForFrequenciesResponse));

        String[] optionsRightSubText = new String[mRecurValues.length];
        String rightSubText = getString(
                R.string.booking_options_right_sub_text,
                getServiceShortName(mBooking.getServiceMachineName()));
        for (int i = 0; i < optionsRightSubText.length; i++)
        {
            optionsRightSubText[i] = rightSubText;
        }
        option.setOptionsRightSubText(optionsRightSubText);
        return option;
    }

    private void createOptionsView(BookingPricesForFrequenciesResponse bookingPricesForFrequenciesResponse)
    {
        //create the options view
        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), getBookingOption(bookingPricesForFrequenciesResponse), optionUpdated);
        optionsView.setCurrentIndex(indexForFreq(bookingPricesForFrequenciesResponse.getCurrentFrequency()));
        optionsView.hideTitle();
        optionsLayout.removeAllViews();
        optionsLayout.addView(optionsView);
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
        createOptionsView(event.bookingPricesForFrequenciesResponse);
        removeUiBlockers();
    }

    @Subscribe
    public final void onReceiveBookingPricesForFrequenciesError(HandyEvent.ReceiveGetBookingPricesForFrequenciesError event)
    {
        onReceiveErrorEvent(event);
    }
}
