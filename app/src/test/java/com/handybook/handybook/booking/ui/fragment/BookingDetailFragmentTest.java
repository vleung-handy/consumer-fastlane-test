package com.handybook.handybook.booking.ui.fragment;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.referral.event.ReferralsEvent;
import com.handybook.handybook.testutil.AppAssertionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingDetailFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private User mUser;
    @Inject
    UserManager mUserManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance()
                                                .getApplicationContext()).inject(this);
        when(mUserManager.getCurrentUser()).thenReturn(mUser);
    }

    @Test
    public void shouldPostPrepareReferralsIfFromBookingFlow() throws Exception
    {
        final BookingDetailFragment fragment = BookingDetailFragment.newInstance(
                mBooking,
                true
        );

        SupportFragmentTestUtil.startVisibleFragment(fragment);

        final ReferralsEvent.RequestPrepareReferrals event =
                AppAssertionUtils.getFirstMatchingBusEvent(
                        fragment.bus,
                        ReferralsEvent.RequestPrepareReferrals.class
                );
        assertNotNull(event);
    }

    @Test
    public void shouldNotPostPrepareReferralsIfNotFromBookingFlow() throws Exception
    {
        final BookingDetailFragment fragment = BookingDetailFragment.newInstance(
                mBooking,
                false
        );

        SupportFragmentTestUtil.startVisibleFragment(fragment);

        final ReferralsEvent.RequestPrepareReferrals event =
                AppAssertionUtils.getFirstMatchingBusEvent(
                        fragment.bus,
                        ReferralsEvent.RequestPrepareReferrals.class
                );
        assertNull(event);
    }
}
