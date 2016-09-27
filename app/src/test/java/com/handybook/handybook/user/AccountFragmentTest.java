package com.handybook.handybook.user;


import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.account.ui.ContactFragment;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.account.ui.ProfilePasswordFragment;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.ui.activity.UpdatePaymentActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
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
        View view = mFragment.getView().findViewById(R.id.contact_info_layout);
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
        View view = mFragment.getView().findViewById(R.id.password_layout);
        assertNotNull(view);
        view.performClick();
        assertTrue(mFragment.getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container) instanceof
                           ProfilePasswordFragment);
    }

    @Test
    public void shouldLaunchCorrectActivityWhenPaymentClicked() throws Exception
    {
        ProfileActivity initialActivity = (ProfileActivity) mFragment.getActivity();
        ShadowActivity shadowActivity = (ShadowActivity) ShadowExtractor.extract(
                mFragment.getActivity()
        );
        assertNotNull(initialActivity);

        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.payment_method_layout);
        assertNotNull(view);
        view.performClick();

        assertEquals(
                new Intent(initialActivity, UpdatePaymentActivity.class).getComponent(),
                shadowActivity.peekNextStartedActivity().getComponent()
        );
    }

    @Test
    public void shouldLaunchCorrectFragmentWhenActivePlansClicked() throws Exception
    {
        //TODO: Write this when active plans is done
    }

    @Test
    public void shouldLaunchCorrectActivityWhenPromoClicked() throws Exception
    {
        ProfileActivity initialActivity = (ProfileActivity) mFragment.getActivity();
        ShadowActivity shadowActivity = (ShadowActivity) ShadowExtractor.extract(
                mFragment.getActivity()
        );
        assertNotNull(initialActivity);

        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.promo_code_layout);
        assertNotNull(view);
        view.performClick();

        assertEquals(
                new Intent(initialActivity, PromosActivity.class).getComponent(),
                shadowActivity.peekNextStartedActivity().getComponent()
        );
    }

    @Test
    public void shouldShowLogOutDialog() throws Exception
    {
        assertNotNull(mFragment.getView());
        View view = mFragment.getView().findViewById(R.id.sign_out_button);
        assertNotNull(view);
        view.performClick();

        Dialog dialog = ShadowAlertDialog.getLatestDialog();
        assertNotNull(dialog);
    }
}
