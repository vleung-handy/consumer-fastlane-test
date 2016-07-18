package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.constant.BookingRecurrence;
import com.handybook.handybook.booking.constant.BookingRecurrence.BookingRecurrenceCode;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class BookingRecurrenceFragment extends BookingFlowFragment
{
    private BookingTransaction bookingTransaction;
    private int[] mRecurrenceOptions;
    private static final int[] DEFAULT_RECURRENCE_OPTIONS =
            new int[]{
                    BookingRecurrence.WEEKLY,
                    BookingRecurrence.BIMONTHLY,
                    BookingRecurrence.MONTHLY,
                    BookingRecurrence.ONE_TIME
            };

    @Bind(R.id.options_layout)
    LinearLayout optionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    public static BookingRecurrenceFragment newInstance()
    {
        final BookingRecurrenceFragment fragment = new BookingRecurrenceFragment();
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
        if(bookingManager.getCurrentQuote() == null
                || bookingManager.getCurrentQuote().getRecurrenceOptions() == null)
        {
            mRecurrenceOptions = DEFAULT_RECURRENCE_OPTIONS;

        }
        else
        {
            mRecurrenceOptions = bookingManager.getCurrentQuote().getRecurrenceOptions();
        }
        mixpanel.trackEventAppTrackFrequency();
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_booking_recurrence, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.how_often));
        final BookingHeaderFragment header = new BookingHeaderFragment();
        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.info_header_layout, header).commit();
        final BookingOption option = getBookingOptionModelFromRecurrenceOptions(mRecurrenceOptions);
        final BookingOptionsSelectView optionsView
                = new BookingOptionsSelectView(getActivity(), option, optionUpdated);
        optionsView.hideTitle();

        //set default selected option
        int defaultRecurrenceOption = getDefaultSelectedRecurrenceOption();
        for(int i = 0; i<mRecurrenceOptions.length; i++)
        {
            if(mRecurrenceOptions[i] == defaultRecurrenceOption)
            {
                optionsView.setCurrentIndex(i);
                break;
            }
        }

        optionsLayout.addView(optionsView, 0);
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
            continueBookingFlow();
        }
    };

    private final BookingOptionsView.OnUpdatedListener optionUpdated
            = new BookingOptionsView.OnUpdatedListener()
    {
        @Override
        public void onUpdate(final BookingOptionsView view)
        {
            final int index = ((BookingOptionsSelectView) view).getCurrentIndex();
            bookingTransaction.setRecurringFrequency(mRecurrenceOptions[index]);
        }

        @Override
        public void onShowChildren(
                final BookingOptionsView view,
                final String[] items
        )
        {
        }

        @Override
        public void onHideChildren(
                final BookingOptionsView view,
                final String[] items
        )
        {
        }
    };

    private String[] getSavingsInfoFromRecurrenceOptions(@NonNull int[] recurrenceOptions)
    {
        final String[] info = new String[recurrenceOptions.length];
        final BookingQuote quote = bookingManager.getCurrentQuote();
        final float hours = bookingTransaction.getHours();
        final float prices[] = quote.getPricing(hours, 0);
        final float price = prices[0];
        final float discount = prices[1];

        for (int i = 0; i < recurrenceOptions.length; i++)
        {
            final float recurPrices[] = quote.getPricing(hours, recurrenceOptions[i]);
            final float recurPrice = recurPrices[0];
            final float recurDiscount = recurPrices[1];

            int percent;
            if (recurPrice != recurDiscount)
            {
                percent = (int) ((discount - recurDiscount) / discount * 100);
            }
            else
            {
                percent = (int) ((price - recurPrice) / price * 100);
            }
            if (percent > 0)
            {
                info[i] = getString(R.string.save).toUpperCase() + " " + percent + "%";
            }
        }
        return info;
    }

    /**
     * unfortunately we need to do this mapping here because
     * the server does not return display strings
     *
     * @param recurrenceOptions
     * @return A string array of display strings based on
     * an int array of booking recurrence options
     */
    @NonNull
    private String[] getDisplayStringArrayForRecurrenceOptions(
            @NonNull int[] recurrenceOptions)
    {
        String[] displayStringArray = new String[recurrenceOptions.length];
        for(int i = 0; i<displayStringArray.length; i++)
        {
            displayStringArray[i] = getDisplayStringForRecurrenceCode(recurrenceOptions[i]);
            if(displayStringArray[i] == null)
            {
                Crashlytics.logException(new Exception("Unable to map recurrence option to display string: " + recurrenceOptions[i]));
            }
        }
        return displayStringArray;
    }

    /**
     * @param recurrenceCode
     * @return a display string given a booking recurrence code
     */
    @Nullable
    private String getDisplayStringForRecurrenceCode(
            @BookingRecurrenceCode int recurrenceCode)
    {
        switch(recurrenceCode)
        {
            case BookingRecurrence.WEEKLY:
                return getString(R.string.every_week);
            case BookingRecurrence.BIMONTHLY:
                return getString(R.string.every_two_weeks);
            case BookingRecurrence.MONTHLY:
                return getString(R.string.every_four_weeks);
            case BookingRecurrence.ONE_TIME:
                return getString(R.string.once);
            default:
                return null;
        }
    }

    @Nullable
    private String getOptionSubTextForFrequencyCode(@BookingRecurrenceCode int recurrenceCode)
    {
        switch(recurrenceCode)
        {
            case BookingRecurrence.BIMONTHLY:
                return getString(R.string.most_popular);
            default:
                return null;
        }
    }

    @NonNull
    private String[] getOptionsSubTextArrayFromRecurrenceOptions(
            @NonNull int[] recurrenceOptions)
    {
        String[] optionsSubTextArray = new String[recurrenceOptions.length];
        for(int i = 0; i<optionsSubTextArray.length; i++)
        {
            optionsSubTextArray[i] = getOptionSubTextForFrequencyCode(recurrenceOptions[i]);
            if(optionsSubTextArray[i] == null)
            {
                Crashlytics.logException(new Exception("Unable to map recurrence option to options subtext: " + recurrenceOptions[i]));
            }
        }
        return optionsSubTextArray;
    }

    /**
     * @return an options UI model given a booking recurrence options array
     */
    private BookingOption getBookingOptionModelFromRecurrenceOptions(
            int[] recurrenceOptions)
    {
        final BookingOption option = new BookingOption();
        option.setType(BookingOption.TYPE_OPTION);
        option.setOptions(getDisplayStringArrayForRecurrenceOptions(recurrenceOptions));
        option.setOptionsSubText(getOptionsSubTextArrayFromRecurrenceOptions(recurrenceOptions));
        option.setOptionsRightSubText(getSavingsInfoFromRecurrenceOptions(recurrenceOptions));
        return option;
    }

    /**
     * setting as function rather than constant in case
     * we want to get this from the server response later
     * @return
     */
    @BookingRecurrenceCode
    private int getDefaultSelectedRecurrenceOption()
    {
        return BookingRecurrence.BIMONTHLY;
    }
}
