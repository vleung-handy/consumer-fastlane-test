package com.handybook.handybook.booking.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * A test for ActiveBookingFragment
 */
public class ActiveBookingFragmentTest extends RobolectricGradleTestWrapper
{
    private ActiveBookingFragment mActiveFragment;
    private ActiveBookingFragment mNoProviderFragment;
    Booking mActiveBooking;

    @Before
    public void setUp() throws Exception
    {
        setupActiveBooking();
        setupBookingNoProvider();
    }

    private void setupActiveBooking() throws Exception
    {
        String json = IOUtils.getJsonStringForTest("active_booking.json");

        mActiveBooking = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                                          .create()
                                          .fromJson(json, Booking.class);

        mActiveFragment = ActiveBookingFragment.newInstance(mActiveBooking);
    }

    private void setupBookingNoProvider() throws Exception
    {
        String json = IOUtils.getJsonStringForTest("active_booking_no_provider.json");

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
        assertEquals(((TextView) mActiveFragment.mProProfile.findViewById(R.id.mini_pro_profile_title)).getText(), "Marky S.");
        assertEquals(mActiveFragment.mTextCall.getVisibility(), View.VISIBLE);
        assertEquals(mActiveFragment.mTextText.getVisibility(), View.VISIBLE);
        assertEquals(mActiveFragment.mMapDivider.getVisibility(), View.VISIBLE);
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
        assertEquals(((TextView) mNoProviderFragment.mProProfile.findViewById(R.id.mini_pro_profile_title)).getText(), "");
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
        ShadowActivity shadowActivity = shadowOf(mActiveFragment.getActivity());
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(BookingDetailActivity.class.getName(), shadowIntent.getIntentClass().getName());

        //when the provider cell is clicked it should also launch the bookings activity
        mActiveFragment.mProfileContainer.performClick();
        shadowActivity = shadowOf(mActiveFragment.getActivity());
        startedIntent = shadowActivity.getNextStartedActivity();
        shadowIntent = shadowOf(startedIntent);
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
        assertEquals(
                "9:16 am – 11:16 am",
                mActiveFragment.mTextBookingSubtitle.getText().toString()
        );
    }

    /**
     * Test the status display, in this case, pro never arrived.
     */
    @Test
    public void testMilestoneStatusText()
    {
        SupportFragmentTestUtil.startFragment(mActiveFragment);

        mActiveFragment.updateLocationStatus(mActiveBooking.getActiveBookingLocationStatus());
        assertEquals(
                "Your pro never arrived",
                mActiveFragment.mTextMilestoneStatus.getText().toString()
        );
    }

    /**
     * Test the status icon, in this case it should be the red drawable.
     */
    @Test
    public void testMilestoneStatusIcon()
    {
        SupportFragmentTestUtil.startFragment(mActiveFragment);

        mActiveFragment.updateLocationStatus(mActiveBooking.getActiveBookingLocationStatus());
        ShadowDrawable shadowDrawable = shadowOf(mActiveFragment.mTextMilestoneStatus.getCompoundDrawables()[0]);
        assertEquals(R.drawable.circle_red, shadowDrawable.getCreatedFromResId());
    }


}
