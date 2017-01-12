package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.subscription.CommitmentType;
import com.handybook.handybook.booking.model.subscription.Price;
import com.handybook.handybook.booking.model.subscription.SubscriptionFrequency;
import com.handybook.handybook.booking.model.subscription.SubscriptionLength;
import com.handybook.handybook.booking.model.subscription.SubscriptionPrices;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingSubscriptionFragment extends BookingFlowFragment
{
    @Bind(R.id.booking_frequency_options_spinner_view)
    FrameLayout mFrequencyLayout;
    @Bind(R.id.booking_subscription_option_layout)
    LinearLayout mSubscriptionOptionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private BookingTransaction mBookingTransaction;
    private BookingOptionsSelectView mSubscriptionOptionsView;
    private BookingOptionsSpinnerView mFrequencyOptionsSpinnerView;
    //This used to do a looking up from the selected subscription value to the subscription key
    private Map<String, String> mSubscriptionLengthToKey;
    //This used to do a looking up from the selected frequency value to the frequency key
    private Map<String, String> mFrequencyLengthToKey;

    public static BookingSubscriptionFragment newInstance()
    {
        return new BookingSubscriptionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBookingTransaction = bookingManager.getCurrentTransaction();
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
                                       .inflate(
                                               R.layout.fragment_booking_subscription,
                                               container,
                                               false
                                       );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.booking_subscription_titlebar));

        createFrequencyView();
        createSubscriptionOptions();
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
        Toast.makeText(
                getContext(),
                mFrequencyLengthToKey.get(mFrequencyOptionsSpinnerView.getCurrentValue()),
                Toast.LENGTH_SHORT
        ).show();
        String freqKey = mFrequencyLengthToKey.get(mFrequencyOptionsSpinnerView.getCurrentValue());
        mBookingTransaction.setRecurringFrequency(Integer.parseInt(freqKey));
        continueBookingFlow();
    }

    //TODO sammy do we have to handle onactivity result when users put in a promo code/remove it on final flow page
    private void createFrequencyView()
    {
        List<SubscriptionFrequency> frequencies = bookingManager.getCurrentQuote()
                                                                .getCommitmentType()
                                                                .getUniqueFrequencies();
        String[] frequencyTitles = new String[frequencies.size()];
        mFrequencyLengthToKey = new HashMap<>();

        for (int i = 0; i < frequencies.size(); i++)
        {
            SubscriptionFrequency frequency = frequencies.get(i);
            mFrequencyLengthToKey.put(frequency.getTitle(), frequency.getKey());
            frequencyTitles[i] = frequency.getTitle();
        }

        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);
        bookingOption.setOptions(frequencyTitles);
        bookingOption.setDefaultValue(Integer.toString(0));
        bookingOption.setTitle(getString(R.string.booking_subscription_frequency_title));

        //Create the frequency spinner
        mFrequencyOptionsSpinnerView = new BookingOptionsSpinnerView(
                getContext(),
                bookingOption,
                new BookingOptionsView.OnUpdatedListener()
                {
                    @Override
                    public void onUpdate(final BookingOptionsView view)
                    {
                        Log.e("BLAH", view.getCurrentValue());
                        updateSubscriptionOptions();
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

    private void createSubscriptionOptions()
    {
        //Booking option used for the BookingOptionsSelectView
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION);
        bookingOption.setDefaultValue(Integer.toString(0));

        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();
        String[] subscriptionTitles = new String[subscriptionLengths.size()];
        String[] pricesText = new String[subscriptionLengths.size()];
        String[] cleaningTexts = new String[subscriptionLengths.size()];
        mSubscriptionLengthToKey = new HashMap<>();

        String cleaningText = getString(R.string.booking_subscription_term_cleaning);

        for (int i = 0; i < subscriptionLengths.size(); i++)
        {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String key = subscriptionLength.getKey();
            mSubscriptionLengthToKey.put(subscriptionLength.getTitle(), key);
            subscriptionTitles[i] = subscriptionLength.getTitle();
            Price price = commitmentType.getPrice(
                    key,
                    getCurrentFrequencyKey(),
                    Float.toString(mBookingTransaction.getHours())
            );
            //TODO sammy use the util class to get the price from cents
            pricesText[i] = Integer.toString(price.getFullPrice());
            cleaningTexts[i] = cleaningText;
        }

        bookingOption.setTitle(getString(R.string.booking_subscription_term_title));
        bookingOption.setOptionsRightTitleText(pricesText);
        bookingOption.setOptionsRightSubText(cleaningTexts);
        bookingOption.setOptions(subscriptionTitles);
        bookingOption.setTitle(getString(R.string.booking_subscription_term_title));

        /*
        build a BookingOption model from the monthly subscription options so we can use it
        to create an options view
         */
        mSubscriptionOptionsView = new BookingOptionsSelectView(getActivity(), bookingOption, null);
        updateSubscriptionOptions();
        mSubscriptionOptionsLayout.addView(mSubscriptionOptionsView, 0);
    }

    private void updateSubscriptionOptions()
    {
        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();

        for (int i = 0; i < subscriptionLengths.size(); i++)
        {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String key = subscriptionLength.getKey();
            SubscriptionPrices subscriptionPrice = commitmentType.getSubscriptionPrice(
                    key,
                    getCurrentFrequencyKey()
            );

            if (subscriptionPrice != null)
            {
                boolean isEnabled = subscriptionPrice.isEnabled();
                Log.e("BLAH", isEnabled +"");
                mSubscriptionOptionsView.setIsOptionEnabled(isEnabled, i);

                //todo sammy udpate to have default only update if previous one doesn't exist
                if (subscriptionLength.isDefault())
                {
                    mSubscriptionOptionsView.setCurrentIndex(i);
                }

                mSubscriptionOptionsView.disableAllOptions();
            }
        }
    }

    private String getCurrentFrequencyKey()
    {
        return mFrequencyLengthToKey.get(mFrequencyOptionsSpinnerView.getCurrentValue());
    }
}
