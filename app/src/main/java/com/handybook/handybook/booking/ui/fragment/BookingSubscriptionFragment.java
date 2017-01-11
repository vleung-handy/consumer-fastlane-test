package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.subscription.SubscriptionFrequency;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingSubscriptionFragment extends BookingFlowFragment
{
    private BookingTransaction bookingTransaction;

    @Bind(R.id.booking_frequency_options_spinner_view)
    FrameLayout mFrequencyLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    BookingOptionsSpinnerView mFrequencyOptionsSpinnerView;

    public static BookingSubscriptionFragment newInstance()
    {
        return new BookingSubscriptionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bookingTransaction = bookingManager.getCurrentTransaction();
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
                .inflate(R.layout.fragment_booking_subscription, container, false);
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.booking_subscription_titlebar));

        createFrequencyView();
        return view;
    }

    @Override
    protected final void disableInputs()
    {
        //TODO sammy disable when defaults not selected
        super.disableInputs();
        nextButton.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        nextButton.setClickable(true);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick()
    {
        continueBookingFlow();
    }

    private void createFrequencyView()
    {

        //Get the list of frequencies and put it into the recycler view
        //todo sammy
        List<SubscriptionFrequency> frequencies =new ArrayList<SubscriptionFrequency>();
        frequencies.add(new SubscriptionFrequency("key1", "title1"));
        frequencies.add(new SubscriptionFrequency("key1a", "title1a"));
        frequencies.add(new SubscriptionFrequency("key1b", "title1b"));
        frequencies.add(new SubscriptionFrequency("key1c", "title1c"));
        frequencies.add(new SubscriptionFrequency("key1d", "title1d"));
        frequencies.add(new SubscriptionFrequency("key1e", "title1e"));
        frequencies.add(new SubscriptionFrequency("key1f", "title1f"));
        frequencies.add(new SubscriptionFrequency("key1g", "title1g"));
        frequencies.add(new SubscriptionFrequency("key1d", "title1d"));
        frequencies.add(new SubscriptionFrequency("key1e", "title1e"));
        frequencies.add(new SubscriptionFrequency("key1f", "title1f"));
        frequencies.add(new SubscriptionFrequency("key1g", "title1g"));
        //List<SubscriptionFrequency> frequencies = mBookingQuote.getCommitmentType().getUniqueFrequencies();
        List<String> strings =new ArrayList<String>();

        strings.add("title1 asf");
        strings.add("title1a asfa");
        strings.add("title1 asf");
        strings.add("title1a asfa");
        strings.add("title1 asf");
        strings.add("title1a asfa");
        strings.add("title1 asf");
        strings.add("title1a asfa");
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);
        bookingOption.setOptions(strings.toArray(new String[0]));
        bookingOption.setDefaultValue(Integer.toString(0));
        bookingOption.setTitle(getString(R.string.booking_subscription_frequency_title));



        mFrequencyOptionsSpinnerView = new BookingOptionsSpinnerView(
                getContext(),
                bookingOption,
                new BookingOptionsView.OnUpdatedListener() {
                    @Override
                    public void onUpdate(final BookingOptionsView view)
                    {
                        //view.getCurrentValue()
                    }

                    @Override
                    public void onShowChildren(final BookingOptionsView view, final String[] items)
                    {

                    }

                    @Override
                    public void onHideChildren(final BookingOptionsView view, final String[] items)
                    {

                    }
                }
        );

        mFrequencyLayout.addView(mFrequencyOptionsSpinnerView);
    }
}
