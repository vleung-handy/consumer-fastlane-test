package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.view.View;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.util.IoUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;

/**
 * A test for ActiveBookingFragment
 */
public class ActiveBookingFragmentTest extends RobolectricGradleTestWrapper
{
    private ActiveBookingFragment mActiveFragment;
    private ActiveBookingFragment mNoProviderFragment;

    @Before
    public void setUp() throws Exception
    {
        setupActiveBooking();
        setupBookingNoProvider();
    }

    private void setupActiveBooking() throws Exception
    {
        String json = IoUtils.getJsonString("active_booking.json");

        Booking booking = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, Booking.class);

        mActiveFragment = ActiveBookingFragment.newInstance(booking);
    }

    private void setupBookingNoProvider() throws Exception
    {
        String json = IoUtils.getJsonString("active_booking_no_provider.json");

        Booking booking = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, Booking.class);

        mNoProviderFragment = ActiveBookingFragment.newInstance(booking);
    }

    /**
     * The active booking used here should have provider information. Hence, that information needs
     * to be visible, and accurate
     *
     * @throws Exception
     */
    @Test
    public void providerInfoShouldBeVisible() throws Exception
    {

        SupportFragmentTestUtil.startFragment(mActiveFragment);

        assertEquals(mActiveFragment.mProfileContainer.getVisibility(), View.VISIBLE);
        assertEquals(mActiveFragment.mTextProviderName.getText().toString(), "Marky S.");
        assertEquals(mActiveFragment.mTextCall.getVisibility(), View.VISIBLE);
        assertEquals(mActiveFragment.mTextText.getVisibility(), View.VISIBLE);
        assertEquals(mActiveFragment.mStartingSoonIndicatorDivider.getVisibility(), View.VISIBLE);
    }


    /**
     * If there are no provider information with the active booking, then the provider views
     * need to be hidden.
     *
     * @throws Exception
     */
    @Test
    public void providerInfoShouldBeGone() throws Exception
    {

        SupportFragmentTestUtil.startFragment(mNoProviderFragment);

        assertEquals(mNoProviderFragment.mProfileContainer.getVisibility(), View.GONE);
        assertEquals(mNoProviderFragment.mTextProviderName.getText().toString(), "");
        assertEquals(mNoProviderFragment.mTextCall.getVisibility(), View.GONE);
        assertEquals(mNoProviderFragment.mTextText.getVisibility(), View.GONE);
    }

    /**
     * Tests that when the booking cell is clicked, then it will launch the bookings activity
     *
     * @throws Exception
     */
    @Test
    public void shouldLaunchBookingActivity() throws Exception
    {
        SupportFragmentTestUtil.startFragment(mActiveFragment);

        mActiveFragment.mBookingItemContainer.performClick();
        ShadowActivity shadowActivity = Shadows.shadowOf(mActiveFragment.getActivity());
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
        assertEquals(BookingDetailActivity.class.getName(), shadowIntent.getIntentClass().getName());

        //when the provider cell is clicked it should also launch the bookings activity
        mActiveFragment.mProfileContainer.performClick();
        shadowActivity = Shadows.shadowOf(mActiveFragment.getActivity());
        startedIntent = shadowActivity.getNextStartedActivity();
        shadowIntent = Shadows.shadowOf(startedIntent);
        assertEquals(BookingDetailActivity.class.getName(), shadowIntent.getIntentClass().getName());
    }


    /**
     * Since this is a valid booking, we want to see that all the booking fields are filled out.
     */
    @Test
    public void testBookingInfoShown()
    {
        SupportFragmentTestUtil.startFragment(mActiveFragment);
        assertEquals("Thursday, August 25", mActiveFragment.mTextBookingTitle.getText().toString());
        assertEquals("9:16am – 11:16am", mActiveFragment.mTextBookingSubtitle.getText().toString());
    }
}
