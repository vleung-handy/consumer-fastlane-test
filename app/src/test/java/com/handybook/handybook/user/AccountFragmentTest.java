package com.handybook.handybook.user;


import android.app.Dialog;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.account.ui.ContactFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.account.ui.UpdatePaymentFragment;
import com.handybook.handybook.booking.ui.fragment.PromosFragment;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AccountFragmentTest extends RobolectricGradleTestWrapper
{
    @Inject
    UserManager mUserManager;
    @Mock
    private User mMockUser;

    private AccountFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext())
                .inject(this);
        when(mUserManager.isUserLoggedIn()).thenReturn(true);
        when(mUserManager.getCurrentUser()).thenReturn(mMockUser);
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
        assertTrue(mFragment.getActivity().getSupportFragmentManager()
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
        assertTrue(mFragment.getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container) instanceof
                           ProfilePasswordFragment);
    }

    @Test
    public void shouldLaunchCorrectActivityWhenPaymentClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_payment_method_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container) instanceof
                           UpdatePaymentFragment);
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenActivePlansClicked() throws Exception
    {
        //TODO: Write this when active plans is done
    }

    @Test
    public void shouldLaunchCorrectActivityWhenPromoClicked() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.account_promo_code_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container) instanceof
                           PromosFragment);
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
