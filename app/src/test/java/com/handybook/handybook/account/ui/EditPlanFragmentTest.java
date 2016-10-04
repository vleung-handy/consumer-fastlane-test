package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.testutil.ModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class EditPlanFragmentTest extends RobolectricGradleTestWrapper
{
    private EditPlanFragment mFragment;
    private RecurringBooking mPlan;

    @Before
    public void setUp()
    {
        mPlan = ModelFactory.createRecurringPlan();
        mFragment = EditPlanFragment.newInstance(mPlan);
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
    }

    @Test
    public void shouldDisplayCorrectInfo()
    {
        assertEquals(
                mFragment.getString(R.string.account_your_plan),
                mFragment.mToolbar.getTitle()
        );
        assertEquals(mPlan.getFrequency(), mFragment.mFrequencyText.getText().toString());
        assertEquals(mPlan.getAddress().toString(), mFragment.mAddressText.getText().toString());
    }

    @Test
    public void shouldNavigateToEditFrequencyFragment()
    {
        mFragment.getView().findViewById(R.id.edit_plan_frequency).performClick();
        Fragment newFragment =
                mFragment.getFragmentManager().findFragmentById(R.id.fragment_container);
        assertTrue(newFragment instanceof EditPlanFrequencyFragment);
    }

    @Test
    public void shouldNavigateToEditAddressFragment()
    {
        mFragment.getView().findViewById(R.id.edit_plan_address).performClick();
        Fragment newFragment =
                mFragment.getFragmentManager().findFragmentById(R.id.fragment_container);
        assertTrue(newFragment instanceof EditPlanAddressFragment);
    }
}
