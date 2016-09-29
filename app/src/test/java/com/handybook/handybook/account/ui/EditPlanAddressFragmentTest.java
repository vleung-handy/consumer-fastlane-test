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

public class EditPlanAddressFragmentTest extends RobolectricGradleTestWrapper
{
    private EditPlanAddressFragment mFragment;
    private RecurringBooking mPlan;

    @Before
    public void setUp()
    {
        mPlan = ModelFactory.createRecurringPlan();
        mFragment = EditPlanAddressFragment.newInstance(mPlan);
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
    }

    @Test
    public void shouldDisplayCorrectInfo()
    {
        assertEquals(
                mFragment.getString(R.string.booking_edit_address_title),
                mFragment.mToolbar.getTitle()
        );
        assertEquals(
                mPlan.getAddress().getAddress1(),
                mFragment.mStreetAddressText.getText().toString()
        );
        assertEquals(
                mPlan.getAddress().getAddress2(),
                mFragment.mAptAddressText.getText().toString()
        );
        assertEquals(mPlan.getAddress().getZip(), mFragment.mZipCodeText.getText().toString());
    }

    @Test
    public void shouldUpdateAddress()
    {
        String address1 = "33 W 19th Street";
        String address2 = "FL 6";
        String zipCode = "10011";
        mFragment.mStreetAddressText.setText(address1);
        mFragment.mAptAddressText.setText(address2);
        mFragment.mZipCodeText.setText(zipCode);
        mFragment.getView().findViewById(R.id.plan_address_update_button).performClick();

        String successMessage = mFragment.getString(R.string.account_update_plan_address);
        assertEquals(successMessage, ShadowToast.getTextOfLatestToast());
        assertEquals(address1, mPlan.getAddress().getAddress1());
        assertEquals(address2, mPlan.getAddress().getAddress2());
        assertEquals(zipCode, mPlan.getAddress().getZip());
    }
}
