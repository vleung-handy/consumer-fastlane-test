package com.handybook.handybook.ui.fragment;

import android.content.Intent;

import com.google.common.collect.Lists;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.ui.activity.BookingDateActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class BookingOptionsFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingOptionsFragment mFragment;
    @Inject
    BookingManager mBookingManager;
    @Mock
    private BookingRequest mMockRequest;
    @Mock
    private BookingOption mMockBookingOption;

    // TODO: Write tests where some options are selected before clicking next

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);

        when(mBookingManager.getCurrentRequest()).thenReturn(mMockRequest);

        mFragment = BookingOptionsFragment.newInstance(
                Lists.newArrayList(mMockBookingOption),
                99,
                new HashMap<String, Boolean>(),
                new ArrayList<BookingOption>(),
                false
        );

        SupportFragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void shouldLaunchBookingDateActivity() throws Exception
    {
        mFragment.nextButton.performClick();

        Intent nextStartedActivity = shadowOf(mFragment.getActivity()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(BookingDateActivity.class.getName()));
    }
}