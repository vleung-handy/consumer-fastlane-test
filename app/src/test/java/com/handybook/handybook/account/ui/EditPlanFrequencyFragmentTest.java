package com.handybook.handybook.account.ui;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.testutil.ModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertEquals;

public class EditPlanFrequencyFragmentTest extends RobolectricGradleTestWrapper {

    private EditPlanFrequencyFragment mFragment;
    private RecurringBooking mPlan;

    @Before
    public void setUp() {
        mPlan = ModelFactory.createRecurringPlan();
        mFragment = EditPlanFrequencyFragment.newInstance(mPlan);
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
    }

    @Test
    public void shouldDisplayCorrectInfo() {
        assertEquals(mFragment.getString(R.string.edit_frequency), mFragment.mToolbar.getTitle());

        FrequencySelectionsView frequencyView = mFragment.mFrequencySelectionsView;

        assertEquals(3, frequencyView.getChildCount());
        assertEquals(mPlan.getFrequencyValue(), frequencyView.getCurrentlySelectedFrequency());
    }

    @Test
    public void shouldUpdateFrequency() {
        FrequencySelectionsView frequencyView = mFragment.mFrequencySelectionsView;
        frequencyView.getChildAt(1).findViewById(R.id.frequency_option_radio).performClick();
        assertEquals(2, frequencyView.getCurrentlySelectedFrequency());

        mFragment.getView().findViewById(R.id.plan_frequency_update_button).performClick();
        String successMessage = mFragment.getString(R.string.updated_booking_frequency);
        assertEquals(successMessage, ShadowToast.getTextOfLatestToast());
        assertEquals(2, mPlan.getFrequencyValue());
    }
}
