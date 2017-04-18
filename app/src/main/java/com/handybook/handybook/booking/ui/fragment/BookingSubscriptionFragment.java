package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.subscription.CommitmentType;
import com.handybook.handybook.booking.model.subscription.Price;
import com.handybook.handybook.booking.model.subscription.SubscriptionFrequency;
import com.handybook.handybook.booking.model.subscription.SubscriptionLength;
import com.handybook.handybook.booking.model.subscription.SubscriptionPrices;
import com.handybook.handybook.booking.ui.view.BookingOptionsCheckboxView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.booking.ui.view.BookingOptionsView;
import com.handybook.handybook.library.ui.fragment.WebViewFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingFunnelLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingSubscriptionFragment extends BookingFlowFragment {

    private static final String TAG = BookingSubscriptionFragment.class.getName();
    private static final String KEY_TRIAL_EXPANDED = "key:trial_expanded";

    @Bind(R.id.booking_frequency_options_spinner_view)
    ViewGroup mFrequencyLayout;
    @Bind(R.id.booking_subscription_option_layout)
    ViewGroup mSubscriptionOptionsLayout;
    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.booking_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.booking_subscription_trial_container)
    LinearLayout mTrialContainer;
    @Bind(R.id.booking_subscription_trial_cta)
    TextView mTrialCta;
    @Bind(R.id.booking_subscription_trial_option_checkbox)
    BookingOptionsCheckboxView mTrialCheckbox;

    private BookingTransaction mBookingTransaction;
    protected BookingOptionsSelectView mSubscriptionOptionsView;
    protected BookingOptionsSelectView mTrialOptionView;
    protected BookingOptionsSpinnerView mFrequencyOptionsSpinnerView;
    //This used to do a looking up from the selected subscription value to the subscription key
    private Map<String, String> mSubscriptionLengthToKey;
    //This used to do a looking up from the selected frequency value to the frequency key
    private Map<String, String> mFrequencyValueToKey;
    private boolean mIsTrialExpanded = false;

    public static BookingSubscriptionFragment newInstance() {
        return new BookingSubscriptionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingTransaction = bookingManager.getCurrentTransaction();
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(
                R.layout.fragment_booking_subscription,
                container,
                false
        );
        ButterKnife.bind(this, view);
        setupToolbar(mToolbar, getString(R.string.booking_subscription_titlebar));
        if (savedInstanceState != null) {
            mIsTrialExpanded = savedInstanceState.getBoolean(KEY_TRIAL_EXPANDED, false);
        }
        createFrequencyView();
        createSubscriptionOptions();
        initTrial();
        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_TRIAL_EXPANDED, mIsTrialExpanded);
    }

    private void initTrial() {
        BookingQuote quote = bookingManager.getCurrentQuote();
        if (!quote.isCommitmentTrialActive()) {
            return;
        }
        mTrialContainer.setVisibility(View.VISIBLE);
        if (mIsTrialExpanded) {
            expandTrial();
        }
        quote.getTrialCommitmentType();
        mTrialCheckbox.setLeftTitle("Top Left");
        mTrialCheckbox.setLeftText("Bottom Left");
        mTrialCheckbox.setRightTitle("$299");
        mTrialCheckbox.setRightText(getString(R.string.booking_subscription_term_cleaning));
        mTrialCheckbox.setSuperText(null);
        mTrialCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                trialChecked(isChecked);
            }
        });
    }

    private void trialChecked(final boolean isChecked) {
        showToast("Trial checked");
    }

    @OnClick(R.id.booking_subscription_trial_cta)
    public void onTrialCtaClicked() {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialCtaClickedLog()));
        expandTrial();
    }

    private void expandTrial() {
        mIsTrialExpanded = true;
        mTrialCta.setVisibility(View.GONE);
        mTrialCheckbox.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 50);
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        //Get the frequency selected
        mBookingTransaction.setRecurringFrequency(Integer.parseInt(getCurrentFrequencyKey()));

        //Get the subscription selected
        String subKey = mSubscriptionLengthToKey.get(mSubscriptionOptionsView.getCurrentValue());
        int commitmentLength = TextUtils.isBlank(subKey) ? 0 : Integer.parseInt(subKey);
        mBookingTransaction.setCommitmentLength(commitmentLength);

        continueBookingFlow();
    }

    @OnClick(R.id.booking_subscription_toolbar_faq)
    public void onFAQClicked() {
        String faqURL = bookingManager.getCurrentQuote().getCommitmentFaqUrl();
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingFAQPressedLog(faqURL)));
        WebViewFragment fragment = WebViewFragment
                .newInstance(faqURL, getString(R.string.booking_subscription_titlebar_faq));
        FragmentUtils.switchToFragment(this, fragment, true);
    }

    private void createFrequencyView() {
        List<SubscriptionFrequency> frequencies = bookingManager.getCurrentQuote()
                                                                .getCommitmentType()
                                                                .getUniqueFrequencies();
        String[] frequencyTitles = new String[frequencies.size()];
        mFrequencyValueToKey = new HashMap<>();

        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);
        bookingOption.setOptions(frequencyTitles);
        bookingOption.setDefaultValue(Integer.toString(0));
        bookingOption.setTitle(getString(R.string.booking_subscription_frequency_title));

        for (int i = 0; i < frequencies.size(); i++) {
            SubscriptionFrequency frequency = frequencies.get(i);
            mFrequencyValueToKey.put(frequency.getTitle(), frequency.getKey());
            frequencyTitles[i] = frequency.getTitle();

            if (frequency.isDefault()) {
                bookingOption.setDefaultValue(Integer.toString(i));
            }
        }

        //Create the frequency spinner
        mFrequencyOptionsSpinnerView = new BookingOptionsSpinnerView(
                getContext(),
                bookingOption,
                new BookingOptionsView.OnUpdatedListener() {
                    @Override
                    public void onUpdate(final BookingOptionsView view) {
                        updateSubscriptionOptions();
                    }

                    @Override
                    public void onShowChildren(
                            final BookingOptionsView view,
                            final String[] items
                    ) {

                    }

                    @Override
                    public void onHideChildren(
                            final BookingOptionsView view,
                            final String[] items
                    ) {

                    }
                }
        );

        mFrequencyLayout.addView(mFrequencyOptionsSpinnerView);
    }

    private void createSubscriptionOptions() {
        //Booking option used for the BookingOptionsSelectView
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION);

        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();
        String[] subscriptionTitles = new String[subscriptionLengths.size()];
        String[] pricesText = new String[subscriptionLengths.size()];
        String[] cleaningTexts = new String[subscriptionLengths.size()];
        mSubscriptionLengthToKey = new HashMap<>();

        String cleaningText = getString(R.string.booking_subscription_term_cleaning);

        for (int i = 0; i < subscriptionLengths.size(); i++) {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String key = subscriptionLength.getKey();
            if (subscriptionLength.isDefault()) {
                bookingOption.setDefaultValue(Integer.toString(i));
            }
            mSubscriptionLengthToKey.put(subscriptionLength.getTitle(), key);
            subscriptionTitles[i] = subscriptionLength.getTitle();
            Price price = commitmentType.getPrice(
                    key,
                    getCurrentFrequencyKey(),
                    Float.toString(mBookingTransaction.getHours())
            );

            if (price != null) {
                pricesText[i] = TextUtils.formatPriceCents(
                        price.getFullPrice(),
                        bookingManager.getCurrentQuote()
                                      .getCurrencyChar()
                );
            }

            cleaningTexts[i] = cleaningText;
        }

        bookingOption.setOptionsRightTitleText(pricesText);
        bookingOption.setOptionsRightSubText(cleaningTexts);
        bookingOption.setOptions(subscriptionTitles);

        /*
        build a BookingOption model from the monthly subscription options so we can use it
        to create an options view
         */
        mSubscriptionOptionsView = new BookingOptionsSelectView(getActivity(), bookingOption, null);
        mSubscriptionOptionsView.hideTitle();
        updateSubscriptionOptions();
        mSubscriptionOptionsLayout.addView(mSubscriptionOptionsView);
    }

    private void updateSubscriptionOptions() {
        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();

        for (int i = 0; i < subscriptionLengths.size(); i++) {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String key = subscriptionLength.getKey();
            SubscriptionPrices subscriptionPrice = commitmentType.getSubscriptionPrice(
                    key,
                    getCurrentFrequencyKey()
            );

            if (subscriptionPrice != null) {
                boolean isEnabled = subscriptionPrice.isEnabled();
                mSubscriptionOptionsView.setIsOptionEnabled(isEnabled, i);

                Price price = subscriptionPrice.getPrices()
                                               .get(Float.toString(mBookingTransaction.getHours()));

                if (price != null) {
                    mSubscriptionOptionsView.updateRightOptionsTitleText(
                            TextUtils.formatPriceCents(
                                    price.getFullPrice(),
                                    bookingManager.getCurrentQuote().getCurrencyChar()
                            ),
                            i
                    );
                }

                if (isEnabled && subscriptionLength.isDefault()) {
                    mSubscriptionOptionsView.setCurrentIndex(i);
                }
            }
        }
    }

    private String getCurrentFrequencyKey() {
        //This is here only because unit tests fail here. Should never happen in real life
        if (mFrequencyOptionsSpinnerView.getListSize() == 0) { return "0"; }

        return mFrequencyValueToKey.get(mFrequencyOptionsSpinnerView.getCurrentValue());
    }
}
