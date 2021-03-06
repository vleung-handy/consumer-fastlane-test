package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.TermsOfUse;
import com.handybook.handybook.booking.model.subscription.CommitmentType;
import com.handybook.handybook.booking.model.subscription.Price;
import com.handybook.handybook.booking.model.subscription.SubscriptionFrequency;
import com.handybook.handybook.booking.model.subscription.SubscriptionLength;
import com.handybook.handybook.booking.model.subscription.SubscriptionPrices;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionPaymentView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class BookingSubscriptionFragment extends BookingFlowFragment {

    private static final String TAG = BookingSubscriptionFragment.class.getName();
    private static final String KEY_TRIAL_EXPANDED = "key:trial_expanded";
    private static final String KEY_MONTHS_DISABLED = "key:months_disabled";
    public static final String TAG_COMMITMENT_FAQ = "dialog:commitment:faq";

    @BindView(R.id.booking_frequency_options_spinner_view)
    ViewGroup mFrequencyLayout;
    @BindView(R.id.booking_subscription_option_layout)
    ViewGroup mSubscriptionOptionsLayout;
    @BindView(R.id.next_button)
    Button nextButton;
    @BindView(R.id.booking_scrollview)
    ScrollView mScrollView;
    @BindView(R.id.booking_subscription_trial_container)
    LinearLayout mTrialContainer;
    @BindView(R.id.booking_subscription_trial_cta)
    TextView mTrialCta;
    @BindView(R.id.booking_subscription_trial_option_checkbox)
    BookingOptionsCheckboxView mTrialCheckbox;
    @BindView(R.id.booking_subscription_commitment_tooltip)
    ImageView mCommitmentTooltip;
    @BindView(R.id.booking_subscription_title)
    TextView mSubscriptionTitle;
    @BindView(R.id.coupon_banner_container)
    ViewGroup mCouponBannerContainer;
    @BindView(R.id.coupon_banner_title)
    TextView mCouponBannerTitle;
    @BindView(R.id.coupon_banner_subtitle)
    TextView mCouponBannerSubtitle;
    @BindView(R.id.booking_subscription_coupon_disclaimer)
    TextView mCouponDisclaimerText;

    private BookingTransaction mBookingTransaction;
    protected BookingOptionsSelectView mCommitmentView;
    protected BookingOptionsSpinnerView mFrequencyView;
    //This used to do a looking up from the selected subscription value to the subscription key
    private Map<String, String> mSubscriptionLengthToKey;

    //This used to do a looking up from the selected frequency value to the SubscriptionFrequency
    private Map<String, SubscriptionFrequency> mFrequencyValueToSubscriptionFrequency;

    private boolean mIsTrialExpanded = false;
    private boolean mIsMonthsDisabled = false;

    public static BookingSubscriptionFragment newInstance(@Nullable final Bundle extras) {
        BookingSubscriptionFragment fragment = new BookingSubscriptionFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingTransaction = bookingManager.getCurrentTransaction();
        bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.BookingDetailsShownLog()));
        setHasOptionsMenu(true);
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
        initTooltip();
        initCoupon();
        return view;
    }

    private void initCoupon() {
        final BookingQuote.QuoteCoupon coupon = bookingManager.getCurrentQuote().getCoupon();

        if (coupon == null) {
            mSubscriptionTitle.setVisibility(View.VISIBLE);
            mCouponBannerContainer.setVisibility(View.GONE);
            mCouponDisclaimerText.setVisibility(View.GONE);
            return;
        }

        final String title = coupon.getTitle();
        final String subtitle = coupon.getSubtitle();
        final String disclaimer = coupon.getDisclaimer();

        final boolean shouldShowBanner = !TextUtils.isBlank(title) || !TextUtils.isBlank(subtitle);
        mCouponBannerContainer.setVisibility(shouldShowBanner ? View.VISIBLE : View.GONE);
        mSubscriptionTitle.setVisibility(shouldShowBanner ? View.GONE : View.VISIBLE);

        mCouponBannerTitle.setText(title);
        mCouponBannerTitle.setVisibility(TextUtils.isBlank(title) ? View.GONE : View.VISIBLE);

        mCouponBannerSubtitle.setText(subtitle);
        mCouponBannerSubtitle.setVisibility(TextUtils.isBlank(subtitle) ? View.GONE : View.VISIBLE);

        mCouponDisclaimerText.setText(disclaimer);
        mCouponDisclaimerText.setVisibility(
                TextUtils.isBlank(disclaimer) ? View.GONE : View.VISIBLE
        );

    }

    private void initTooltip() {
        if (bookingManager.getCurrentQuote().hasCommitmentTooltip()) {
            mCommitmentTooltip.setVisibility(View.VISIBLE);
        }
        else {
            mCommitmentTooltip.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_TRIAL_EXPANDED, mIsTrialExpanded);
        outState.putBoolean(KEY_MONTHS_DISABLED, mIsMonthsDisabled);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.booking_subscription, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.booking_subscription_faq:
                String faqURL = bookingManager.getCurrentQuote().getCommitmentFaqUrl();
                bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingFAQPressedLog(faqURL)));
                WebViewFragment fragment = WebViewFragment
                        .newInstance(faqURL, getString(R.string.booking_subscription_titlebar_faq));
                FragmentUtils.switchToFragment(this, fragment, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            handleTrialCommitment();
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialSelectedLog()));
            disableMonths();
        }
        else {
            bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialDeselectedLog()));
            enableMonths();
        }
    }

    private void enableMonths() {
        mIsMonthsDisabled = false;
        mTrialCheckbox.setChecked(false);
        mFrequencyView.setAlpha(1);
        //        mSubscriptionOptionsLayout.setAlpha(1);
        updateSubscriptionOptions();
    }

    private void disableMonths() {
        mIsMonthsDisabled = true;
        mFrequencyView.setAlpha(0.5f);
        //        mSubscriptionOptionsLayout.setAlpha(0.5f);
        for (int i = 0; i < mCommitmentView.getOptionViewsCount(); i++) {
            mCommitmentView.setIsOptionEnabled(false, i);
        }

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
        bus.post(new LogEvent.AddLogEvent(new BookingFunnelLog.BookingOneTimeTrialCtaTappedLog()));
        expandTrial();
    }

    private void updateBookingTransaction(
            @NonNull String commitmentType,
            final SubscriptionFrequency recurringFrequency,
            final int commitmentLength
    ) {
        mBookingTransaction.setCommitmentType(commitmentType);
        mBookingTransaction.setRecurringFrequency(Integer.parseInt(recurringFrequency.getKey()));
        mBookingTransaction.setCommitmentLength(commitmentLength);

        //Set the type in the terms of use and update the booking Transaction object with it
        TermsOfUse termsOfUse = bookingManager.getCurrentQuote().getTermsOfUse();
        termsOfUse.setType(recurringFrequency.getTermsOfUseType());
    }

    @NonNull
    private String getCurrentFrequencyKey() {
        //This is here only because unit tests fail here. Should never happen in real life
        if (mFrequencyView.getListSize() == 0) { return "0"; }

        return mFrequencyValueToSubscriptionFrequency.get(mFrequencyView
                                                                  .getCurrentValue())
                                                     .getKey();
    }

    private SubscriptionFrequency getCurrentSubscriptionFrequency() {
        return mFrequencyValueToSubscriptionFrequency.get(mFrequencyView.getCurrentValue());
    }

    @NonNull
    private String getCurrentCommitmentKey() {
        if (mCommitmentView.getCurrentIndex() == -1) {
            return "0";
        } // One time deselects commitment
        return mSubscriptionLengthToKey.get(mCommitmentView.getCurrentValue());
    }

    private void createFrequencyView() {
        List<SubscriptionFrequency> frequencies = bookingManager.getCurrentQuote()
                                                                .getCommitmentType()
                                                                .getUniqueFrequencies();
        String[] frequencyTitles = new String[frequencies.size()];
        mFrequencyValueToSubscriptionFrequency = new HashMap<>();

        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION_PICKER);
        bookingOption.setOptions(frequencyTitles);
        bookingOption.setDefaultValue(Integer.toString(0));
        bookingOption.setTitle(getString(R.string.booking_subscription_frequency_title));

        for (int i = 0; i < frequencies.size(); i++) {
            SubscriptionFrequency frequency = frequencies.get(i);
            mFrequencyValueToSubscriptionFrequency.put(frequency.getTitle(), frequency);
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
        mFrequencyLayout.addView(mFrequencyView);
    }

    private void createSubscriptionOptions() {
        //Booking option used for the BookingOptionsSelectView
        BookingOption bookingOption = new BookingOption();
        bookingOption.setType(BookingOption.TYPE_OPTION);

        CommitmentType commitmentType = bookingManager.getCurrentQuote().getCommitmentType();
        List<SubscriptionLength> subscriptionLengths = commitmentType.getUniqueLengths();
        String[] subscriptionTitles = new String[subscriptionLengths.size()];
        String[] subscriptionSubtitles = new String[subscriptionLengths.size()];
        String[] subscriptionPrices = new String[subscriptionLengths.size()];
        String[] subscriptionSubprices = new String[subscriptionLengths.size()];
        mSubscriptionLengthToKey = new HashMap<>();

        final String cleaningText = getString(R.string.booking_subscription_term_cleaning);

        for (int i = 0; i < subscriptionLengths.size(); i++) {
            SubscriptionLength subscriptionLength = subscriptionLengths.get(i);
            String commitmentKey = subscriptionLength.getKey();
            if (subscriptionLength.isDefault()) {
                bookingOption.setDefaultValue(Integer.toString(i));
            }
            mSubscriptionLengthToKey.put(subscriptionLength.getTitle(), commitmentKey);
            subscriptionTitles[i] = subscriptionLength.getTitle();
            subscriptionSubtitles[i] = bookingManager
                    .getCurrentQuote()
                    .getCommitmentType()
                    .getCommitmentSubtitle(
                            commitmentKey,
                            SubscriptionFrequency.convertFromFrequencyKey(
                                    getCurrentFrequencyKey()
                            )
                    );
            subscriptionSubprices[i] = cleaningText;
            Price price = commitmentType.getPrice(
                    commitmentKey,
                    getCurrentFrequencyKey(),
                    Float.toString(mBookingTransaction.getHours())
            );

            if (price != null) {
                subscriptionPrices[i] = TextUtils.formatPriceCents(
                        price.getFullPrice(),
                        bookingManager.getCurrentQuote()
                                      .getCurrencyChar()
                );
            }

        }

        bookingOption.setOptions(subscriptionTitles);
        bookingOption.setOptionsRightTitleText(subscriptionPrices);
        bookingOption.setOptionsSubText(subscriptionSubtitles);
        bookingOption.setOptionsRightSubText(subscriptionSubprices);

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
            SubscriptionPrices pricingStructure = commitmentType.getSubscriptionPrice(
                    commitmentKey,
                    getCurrentFrequencyKey()
            );

            if (pricingStructure != null) {
                boolean isEnabled = pricingStructure.isEnabled();
                mCommitmentView.setIsOptionEnabled(isEnabled, i);

                Price price = pricingStructure.getPrices()
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
                final String commitmentSubtitle = bookingManager
                        .getCurrentQuote()
                        .getCommitmentType()
                        .getCommitmentSubtitle(
                                commitmentKey,
                                SubscriptionFrequency.convertFromFrequencyKey(
                                        getCurrentFrequencyKey()
                                )
                        );
                mCommitmentView.updateSubtitleText(commitmentSubtitle, i);
            }
        }
    }

    @OnClick(R.id.booking_subscription_commitment_tooltip)
    public void onCommitmentTooltipClick() {
        final FragmentManager fm = ((AppCompatActivity) getContext())
                .getSupportFragmentManager();
        if (fm.findFragmentByTag(TAG_COMMITMENT_FAQ) == null) {
            BookingDetailSectionPaymentView.PriceLineHelpTextDialog
                    .newInstance(bookingManager.getCurrentQuote().getCommitmentTooltip())
                    .show(fm, TAG_COMMITMENT_FAQ);
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if (mTrialCheckbox.isChecked()) {
            handleTrialCommitment();
        }
        else {
            updateBookingTransaction(
                    CommitmentType.STRING_MONTHS,
                    getCurrentSubscriptionFrequency(),
                    Integer.parseInt(getCurrentCommitmentKey())
            );
        }
        continueBookingFlow();
    }

    private void handleTrialCommitment() {
        CommitmentType trialCommitmentType = bookingManager
                .getCurrentQuote()
                .getTrialCommitmentType();
        updateBookingTransaction(
                CommitmentType.STRING_TRIAL,
                trialCommitmentType.getUniqueFrequencies().get(0),
                0
        );
    }
}
