package com.handybook.handybook.account.ui;

import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.core.TestBaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.model.request.UpdateUserRequest;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ContactFragmentTest extends RobolectricGradleTestWrapper
{
    @Inject
    UserManager mUserManager;
    @Inject
    DataManager mDataManager;

    private ContactFragment mFragment;

    @Before
    public void setUp()
    {
        ((TestBaseApplication) RuntimeEnvironment.application).inject(this);
        mFragment = new ContactFragment();
        SupportFragmentTestUtil.startFragment(mFragment, ProfileActivity.class);
    }

    @Test
    public void shouldDisplayCorrectInfo()
    {
        assertEquals(
                mFragment.getString(R.string.account_contact_info),
                mFragment.mToolbar.getTitle()
        );

        User user = mUserManager.getCurrentUser();
        assertEquals(user.getFullName(), mFragment.mFullNameText.getText().toString());
        assertEquals(user.getEmail(), mFragment.mEmailText.getEmail());
        assertEquals(
                user.getPhonePrefix() + user.getPhone(),
                mFragment.mPhoneText.getPhoneNumber()
        );
    }

    @Test
    public void shouldValidateInput()
    {
        mFragment.mFullNameText.setText("");
        mFragment.getView().findViewById(R.id.contact_update_button).performClick();
        verify(mDataManager, never()).updateUser(
                any(UpdateUserRequest.class), any(String.class), any(DataManager.Callback.class));
        mFragment.mEmailText.setText("");
        mFragment.getView().findViewById(R.id.contact_update_button).performClick();
        verify(mDataManager, never()).updateUser(
                any(UpdateUserRequest.class), any(String.class), any(DataManager.Callback.class));
        mFragment.mPhoneText.setText("");
        mFragment.getView().findViewById(R.id.contact_update_button).performClick();
        verify(mDataManager, never()).updateUser(
                any(UpdateUserRequest.class), any(String.class), any(DataManager.Callback.class));
        mFragment.mFullNameText.setText("Test User2");
        mFragment.mEmailText.setText("user2@test.com");
        mFragment.mPhoneText.setText("9876543210");
        mFragment.getView().findViewById(R.id.contact_update_button).performClick();
        verify(mDataManager, atLeastOnce()).updateUser(
                any(UpdateUserRequest.class), any(String.class), any(DataManager.Callback.class));
    }
}
