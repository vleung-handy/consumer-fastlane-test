package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private static final String KEY_MONTHS_DISABLED = "key:months_disabled";

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
    protected BookingOptionsSelectView mCommitmentView;
    protected BookingOptionsSpinnerView mFrequencyView;
    //This used to do a looking up from the selected subscription value to the subscription key
    private Map<String, String> mSubscriptionLengthToKey;
    //This used to do a looking up from the selected frequency value to the frequency key
    private Map<String, String> mFrequencyValueToKey;

    private boolean mIsTrialExpanded = false;
    private boolean mIsMonthsDisabled = false;

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
            mIsMonthsDisabled = savedInstanceState.getBoolean(KEY_MONTHS_DISABLED, false);
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
        outState.putBoolean(KEY_MONTHS_DISABLED, mIsMonthsDisabled);
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
        CommitmentType trialCommitmentType = bookingManager.getCurrentQuote()
                                                           .getTrialCommitmentType();
        SubscriptionPrices trialPrices = trialCommitmentType.getSubscriptionPrice("0", "0");
        Price price = trialPrices.getPrices().get(Float.toString(mBookingTransaction.getHours()));
        mTrialCheckbox.setRightTitle(
                TextUtils.formatPriceCents(
                        price.getFullPrice(),
                        bookingManager.getCurrentQuote().getCurrencyChar()
                )
        );
        mTrialCheckbox.setLeftTitle(trialCommitmentType.getUniqueLengths().get(0).getTitle());
        mTrialCheckbox.setRightText(getString(R.string.booking_subscription_term_cleaning));
        mTrialCheckbox.setSuperText(null);
        mTrialCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                onTrialCheckedChanged(isChecked);
            }
        });
        if (CommitmentType.STRING_TRIAL.equals(mBookingTransaction.getCommitmentType())) {
            mTrialCheckbox.setChecked(true);
        }
    }

    private void onTrialCheckedChanged(final boolean isChecked) {
        if (isChecked) {
            updateBookingTransaction(CommitmentType.STRING_TRIAL, 0, 0);
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialCheckedLog()));
            disableMonths();
        }
        else {
            enableMonths();
        }
    }

    private void enableMonths() {
        mIsMonthsDisabled = false;
        mTrialCheckbox.setChecked(false);
        mFrequencyView.setAlpha(1);
        mSubscriptionOptionsLayout.setAlpha(1);
    }

    private void disableMonths() {
        mIsMonthsDisabled = true;
        mFrequencyView.setAlpha(0.5f);
        mSubscriptionOptionsLayout.setAlpha(0.5f);
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

    @OnClick(R.id.booking_subscription_trial_cta)
    public void onTrialCtaClicked() {
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialCtaClickedLog()));
        expandTrial();
    }

    private void updateBookingTransaction(
            @NonNull String commitmentType,
            final int recurringFrequency,
            final int commitmentLength
    ) {
        mBookingTransaction.setCommitmentType(commitmentType);
        mBookingTransaction.setRecurringFrequency(recurringFrequency);
        mBookingTransaction.setCommitmentLength(commitmentLength);
    }

    @NonNull
    private String getCurrentFrequencyKey() {
        //This is here only because unit tests fail here. Should never happen in real life
        if (mFrequencyView.getListSize() == 0) { return "0"; }
        return mFrequencyValueToKey.get(mFrequencyView.getCurrentValue());
    }

    @NonNull
    private String getCurrentCommitmentKey() {
        return mSubscriptionLengthToKey.get(mCommitmentView.getCurrentValue());
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
        mFrequencyView = new BookingOptionsSpinnerView(
                getContext(),
                bookingOption,
                new BookingOptionsView.OnUpdatedListener() {
                    @Override
                    public void onUpdate(final BookingOptionsView view) {
                        enableMonths();
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
        mFrequencyView.setOnTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return false;
            }
        });
        mFrequencyLayout.addView(mFrequencyView);
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
        mCommitmentView = new BookingOptionsSelectView(getActivity(), bookingOption, null);
        mCommitmentView.hideTitle();
        updateSubscriptionOptions();
        mCommitmentView.setOnTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                enableMonths();
                return false;
            }
        });
        mSubscriptionOptionsLayout.addView(mCommitmentView);
    }

    private void updateSubscriptionOptions() {
        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();

        for (int i = 0; i < subscriptionLengths.size(); i++) {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String commitmentKey = subscriptionLength.getKey();
            SubscriptionPrices subscriptionPrice = commitmentType.getSubscriptionPrice(
                    commitmentKey,
                    getCurrentFrequencyKey()
            );

            if (subscriptionPrice != null) {
                boolean isEnabled = subscriptionPrice.isEnabled();
                if (isEnabled && !mTrialCheckbox.isChecked()) {
                    updateBookingTransaction(
                            CommitmentType.STRING_MONTHS,
                            Integer.parseInt(getCurrentFrequencyKey()),
                            Integer.parseInt(commitmentKey)
                    );
                }
                mCommitmentView.setIsOptionEnabled(isEnabled, i);

                Price price = subscriptionPrice.getPrices()
                                               .get(Float.toString(mBookingTransaction.getHours()));

                if (price != null) {
                    mCommitmentView.updateRightOptionsTitleText(
                            TextUtils.formatPriceCents(
                                    price.getFullPrice(),
                                    bookingManager.getCurrentQuote().getCurrencyChar()
                            ),
                            i
                    );
                }

                if (isEnabled && subscriptionLength.isDefault()) {
                    mCommitmentView.setCurrentIndex(i);
                }
            }
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if (mTrialCheckbox.isChecked()) {
            updateBookingTransaction(CommitmentType.STRING_TRIAL, 0, 0);
        }
        else {
            updateBookingTransaction(
                    CommitmentType.STRING_MONTHS,
                    Integer.parseInt(getCurrentFrequencyKey()),
                    Integer.parseInt(getCurrentCommitmentKey())
            );
        }
/*
        String out =
                "CT: " + mBookingTransaction.getCommitmentType()
                + " F: " + String.valueOf(mBookingTransaction.getRecurringFrequency())
                + " C: " + String.valueOf(mBookingTransaction.getCommitmentLength());
        showToast(out);
*/
        continueBookingFlow();
    }
}
