package com.handybook.handybook.test.user;


import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.TextViewUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class PasswordUpdateTest
{
    private static final TestUser TEST_USER = TestUsers.UPDATE_PASSWORD_USER;
    private static final String NEW_PASSWORD = "newpassword";

    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);


    @Test
    public void testUpdatePassword()
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);

        //wait for network call to return with service list
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //Go to My Account - assuming that is at position 5
        //(don't know how to cleanly query nested item)
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.account)).perform(click());

        // Change password
        ViewUtil.waitForViewVisible(R.id.profile_old_password_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.profile_old_password_text, TEST_USER.getPassword());
        TextViewUtil.updateEditTextView(R.id.profile_new_password_text, NEW_PASSWORD);
        onView(withId(R.id.profile_update_button)).perform(click());
        ViewUtil.waitForViewVisible(R.id.profile_old_password_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForToastMessageVisibility(R.string.info_updated, mActivityRule.getActivity(), ViewUtil.LONG_MAX_WAIT_TIME_MS);

        // Confirm that login with the new password works
        AppInteractionUtil.logOutAndPassOnboarding();
        TEST_USER.setPassword(NEW_PASSWORD);
        AppInteractionUtil.logIn(TEST_USER);

        // If services page is visible, then login worked
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
