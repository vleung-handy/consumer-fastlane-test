package com.handybook.handybook.booking.ui.fragment;

import android.support.v7.app.AppCompatActivity;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.util.BookingUtil;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.library.util.StringUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.Date;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingHeaderFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingHeaderFragment mFragment;

    @Mock
    private BookingTransaction mMockTransaction;
    @Mock
    private BookingRequest mMockBookingRequest;
    @Mock
    private BookingQuote mMockQuote;
    @Inject
    BookingManager mBookingManager;
    @Inject
    ConfigurationManager mConfigurationManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        //mock the transaction
        when(mMockTransaction.getStartDate()).thenReturn(new Date());
        when(mMockTransaction.getHours()).thenReturn(3f);
        when(mMockTransaction.getExtraHours()).thenReturn(1f);

        when(mMockBookingRequest.getTimeZone()).thenReturn("America/Los_Angeles");
        when(mBookingManager.getCurrentTransaction()).thenReturn(mMockTransaction);
        when(mBookingManager.getCurrentRequest()).thenReturn(mMockBookingRequest);
        when(mMockQuote.getPricing(
                anyFloat(),
                anyInt(),
                anyInt()
        )).thenReturn(new float[]{0.0f, 0.0f});
        when(mBookingManager.getCurrentQuote()).thenReturn(mMockQuote);

        mFragment = BookingHeaderFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);
    }

    @Test
    public void whenBookingHoursClarificationExperimentOn_thenShowBookingStartDateWithoutHours()
    {
        Configuration mockConfiguration = mConfigurationManager.getPersistentConfiguration();
        when(mockConfiguration.isBookingHoursClarificationExperimentEnabled()).thenReturn(true);

        //need to restart fragment for config change to take effect
        mFragment = BookingHeaderFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        String startTimeDisplayString = StringUtils.toLowerCase(DateTimeUtils.formatDate(
                mMockTransaction.getStartDate(),
                DateTimeUtils.CLOCK_FORMATTER_12HR,
                mMockBookingRequest.getTimeZone()
        ));

        assertEquals(mFragment.timeText.getText().toString(), startTimeDisplayString);
    }

    @Test
    public void whenBookingHoursClarificationExperimentOff_thenShowBookingStartDateWithHours()
    {
        Configuration mockConfiguration = mConfigurationManager.getPersistentConfiguration();
        when(mockConfiguration.isBookingHoursClarificationExperimentEnabled()).thenReturn(false);

        //need to restart fragment for config change to take effect
        mFragment = BookingHeaderFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        String startTimeDisplayString = StringUtils.toLowerCase(DateTimeUtils.formatDate(
                mMockTransaction.getStartDate(),
                DateTimeUtils.CLOCK_FORMATTER_12HR,
                mMockBookingRequest.getTimeZone()
        ));
        float totalBookingHours = mMockTransaction.getHours() + mMockTransaction.getExtraHours();
        String expectedTimeTextDisplayString = startTimeDisplayString +
                " - " + BookingUtil.getNumHoursDisplayString(
                totalBookingHours,
                mFragment.getContext()
        );

        assertEquals(mFragment.timeText.getText().toString(), expectedTimeTextDisplayString);
    }
}
