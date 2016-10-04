package com.handybook.handybook.account.ui;


import android.app.Dialog;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class AccountFragmentTest extends RobolectricGradleTestWrapper
{
    private AccountFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        mFragment = AccountFragment.newInstance();

        SupportFragmentTestUtil.startVisibleFragment(mFragment, ProfileActivity.class,
                                                     R.id.fragment_container
        );
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenContactClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_contact_info_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getFragmentManager()
                            .findFragmentById(R.id.fragment_container) instanceof
                           ContactFragment);
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenPasswordClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_password_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getFragmentManager().findFragmentById(R.id.fragment_container)
                           instanceof ProfilePasswordFragment);
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenPaymentClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_payment_method_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getFragmentManager().findFragmentById(R.id.fragment_container)
                           instanceof UpdatePaymentFragment);
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenActivePlansClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_active_plans_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getFragmentManager().findFragmentById(R.id.fragment_container)
                           instanceof PlansFragment);
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenPromoClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_promo_code_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getFragmentManager().findFragmentById(R.id.fragment_container)
                           instanceof PromosFragment);
    }

    @Test
    public void shouldShowLogOutDialog() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_sign_out_button);
        assertNotNull(view);
        view.performClick();

        Dialog dialog = ShadowAlertDialog.getLatestDialog();
        assertNotNull(dialog);
    }
}
