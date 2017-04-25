package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.ui.activity.BookingExtrasActivity;
import com.handybook.handybook.booking.ui.view.BookingOptionsSelectView;
import com.handybook.handybook.booking.ui.view.BookingOptionsSpinnerView;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

@Ignore
public class BookingSubscriptionFragmentTest extends RobolectricGradleTestWrapper {

    private static final String JSON_SUBSCRIPTION_NEW_QUOTE = "subscription_new_quote.json";
    private static final String JSON_SUBSCRIPTION_ONETIME_QUOTE
            = "subscription_with_onetime_quote.json";

    private static final String FREQ_ONETIME = "One-time $81.00";
    private static final String FREQ_WEEKLY = "Weekly";
    private static final String FREQ_BIWEEKLY = "Bi-weekly";
    private static final String FREQ_MONTHLY = "Monthly";

    private static final String SUB_3MONTHS = "3 Months";
    private static final String SUB_6MONTHS = "6 Months";
    private static final String SUB_12MONTHS = "12 Months";

    private static final String[] FREQUENCY_LABELS = {FREQ_WEEKLY, FREQ_BIWEEKLY, FREQ_MONTHLY};
    private static final String[] FREQUENCY_LABELS_WITH_ONETIME = {
            FREQ_ONETIME,
            FREQ_WEEKLY,
            FREQ_BIWEEKLY,
            FREQ_MONTHLY
    };
    private static final String[] SUBSCRIPTION_LABELS = {SUB_3MONTHS, SUB_6MONTHS, SUB_12MONTHS};

    private BookingSubscriptionFragment mFragment;

    @Mock
    private BookingTransaction mMockTransaction;
    @Inject
    BookingManager mBookingManager;
    @Mock
    private BookingRequest mMockRequest;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mMockTransaction.getHours()).thenReturn(3f);
        when(mMockRequest.getUniq()).thenReturn("home_cleaning");
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);
        mFragment = BookingSubscriptionFragment.newInstance();
    }

    @Test
    public void shouldHandleNewSubscriptionQuote() throws Exception {
        when(mBookingManager.getCurrentQuote()).thenReturn(getNewSubscriptionBookingQuote());
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        BookingOptionsSpinnerView frequencySpinner = mFragment.mFrequencyView;
        BookingOptionsSelectView subscriptionViews = mFragment.mCommitmentView;
        assertThat(frequencySpinner.getListSize(), equalTo(FREQUENCY_LABELS.length));
        assertThat(subscriptionViews.getOptionViewsCount(), equalTo(SUBSCRIPTION_LABELS.length));
        //Check default value
        assertThat(frequencySpinner.getCurrentValue(), equalTo(FREQ_BIWEEKLY));
        assertThat(subscriptionViews.getCurrentValue(), equalTo(SUB_6MONTHS));

        //Do this after checking defaults
        verifyFrequencyLabels(frequencySpinner, FREQUENCY_LABELS);
        verifySubscriptionLabels(subscriptionViews, SUBSCRIPTION_LABELS);

        //Check last frequency setup
        frequencySpinner.setCurrentIndex(FREQUENCY_LABELS.length - 1);
        assertThat(frequencySpinner.getCurrentValue(), equalTo(FREQ_MONTHLY));
        //Check 3 months is disabled. which is the first item
        assertThat(subscriptionViews.isOptionEnabled(0), equalTo(false));
    }

    @Test
    public void shouldHandleOneTimeQuote() throws Exception {
        when(mBookingManager.getCurrentQuote()).thenReturn(getOneTimeWithSubscriptionBookingQuote());
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        BookingOptionsSpinnerView frequencySpinner = mFragment.mFrequencyView;
        BookingOptionsSelectView subscriptionViews = mFragment.mCommitmentView;
        assertThat(frequencySpinner.getListSize(), equalTo(FREQUENCY_LABELS_WITH_ONETIME.length));
        assertThat(subscriptionViews.getOptionViewsCount(), equalTo(SUBSCRIPTION_LABELS.length));

        //Check default value
        assertThat(frequencySpinner.getCurrentValue(), equalTo(FREQ_BIWEEKLY));
        assertThat(subscriptionViews.getCurrentValue(), equalTo(SUB_6MONTHS));

        //Do this after checking defaults
        verifyFrequencyLabels(frequencySpinner, FREQUENCY_LABELS_WITH_ONETIME);
        verifySubscriptionLabels(subscriptionViews, SUBSCRIPTION_LABELS);

        //Check last frequency setup
        frequencySpinner.setCurrentIndex(FREQUENCY_LABELS_WITH_ONETIME.length - 1);
        assertThat(frequencySpinner.getCurrentValue(), equalTo(FREQ_MONTHLY));
        //Check 3 months is disabled. which is the first item
        assertThat(subscriptionViews.isOptionEnabled(0), equalTo(false));

        //Check one time frequency setup
        frequencySpinner.setCurrentIndex(0);
        assertThat(frequencySpinner.getCurrentValue(), equalTo(FREQ_ONETIME));
        //Check all months is disabled. which is the first item
        assertThat(subscriptionViews.isOptionEnabled(0), equalTo(false));
        assertThat(subscriptionViews.isOptionEnabled(1), equalTo(false));
        assertThat(subscriptionViews.isOptionEnabled(2), equalTo(false));
    }

    @Test
    public void shouldLaunchBookingExtrasActivity() throws Exception {
        when(mBookingManager.getCurrentQuote()).thenReturn(getNewSubscriptionBookingQuote());
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
        mFragment.nextButton.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(
                nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingExtrasActivity.class.getName())
        );
    }

    private void verifyFrequencyLabels(
            BookingOptionsSpinnerView frequencySpinner,
            String[] labels
    ) {
        for (int i = 0; i < labels.length; i++) {
            frequencySpinner.setCurrentIndex(i);
            assertThat(frequencySpinner.getCurrentValue(), equalTo(labels[i]));
        }
    }

    private void verifySubscriptionLabels(
            BookingOptionsSelectView subscriptionViews,
            String[] labels
    ) {
        for (int i = 0; i < labels.length; i++) {
            subscriptionViews.setCurrentIndex(i);
            assertThat(subscriptionViews.getCurrentValue(), equalTo(labels[i]));
        }
    }

    private BookingQuote getNewSubscriptionBookingQuote() throws Exception {
        return BookingQuote.fromJson(IOUtils.getJsonStringForTest(JSON_SUBSCRIPTION_NEW_QUOTE));
    }

    private BookingQuote getOneTimeWithSubscriptionBookingQuote() throws Exception {
        return BookingQuote.fromJson(IOUtils.getJsonStringForTest(JSON_SUBSCRIPTION_ONETIME_QUOTE));
    }
}
