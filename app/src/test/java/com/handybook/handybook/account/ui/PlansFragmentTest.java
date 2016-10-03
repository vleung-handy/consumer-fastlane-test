package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.testutil.ModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class PlansFragmentTest extends RobolectricGradleTestWrapper
{
    private PlansFragment mFragment;
    private ArrayList<RecurringBooking> mPlans;

    @Before
    public void setUp()
    {
        mPlans = ModelFactory.createRecurringPlans(2);

        mFragment = PlansFragment.newInstance(mPlans);
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
    }

    @Test
    public void shouldDisplayCorrectInfo()
    {
        assertEquals(
                mFragment.getString(R.string.account_select_plan),
                mFragment.mToolbar.getTitle()
        );

        assertEquals(2, mFragment.mPlansLayout.getChildCount());
        View view = mFragment.mPlansLayout.getChildAt(0);
        TextView title = (TextView) view.findViewById(R.id.text_plan_title);
        TextView subTitle = (TextView) view.findViewById(R.id.text_plan_subtitle);

        assertEquals(mPlans.get(0).getFrequency(), title.getText().toString());
        assertEquals(DateTimeUtils.DAY_MONTH_DATE_AT_TIME_FORMATTER.format(
                mPlans.get(0).getNextBookingDate()), subTitle.getText().toString());
    }

    @Test
    public void shouldGoToNextFragment()
    {
        mFragment.mPlansLayout.getChildAt(0).performClick();
        Fragment newFragment =
                mFragment.getFragmentManager().findFragmentById(R.id.fragment_container);
        assertTrue(newFragment instanceof EditPlanFragment);
    }
}
