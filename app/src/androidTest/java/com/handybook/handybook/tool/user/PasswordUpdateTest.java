package com.handybook.handybook.tool.user;

import android.support.test.espresso.Espresso;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.TextViewUtil;
import com.handybook.handybook.tool.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class PasswordUpdateTest {

    private static final TestUser TEST_USER = TestUsers.UPDATE_PASSWORD_USER;
    private static final String NEW_PASSWORD = "newpassword";

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    @Test
    public void testUpdatePassword() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //navigate to account page
        onView(withId(R.id.account)).perform(click());

        // Click into password page
        ViewUtil.waitForTextNotVisible(R.string.loading, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewInScrollViewVisible(
                R.id.account_password_layout,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.account_password_layout)).perform(click());

        // Change password
        ViewUtil.waitForViewVisible(R.id.profile_old_password_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.profile_old_password_text, TEST_USER.getPassword());
        TextViewUtil.updateEditTextView(R.id.profile_new_password_text, NEW_PASSWORD);
        TextViewUtil.updateEditTextView(R.id.profile_new_password_confirmation_text, NEW_PASSWORD);

        Espresso.closeSoftKeyboard();
        onView(withId(R.id.profile_password_update_button)).perform(click());

        //this is flaky
        ViewUtil.waitForToastMessageVisibility(
                R.string.info_updated,
                true,
                mActivityRule.getActivity(),
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        ViewUtil.waitForToastMessageVisibility(
                R.string.info_updated,
                false,
                mActivityRule.getActivity(),
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );

        // Confirm that login with the new password works
        pressBack();

        AppInteractionUtil.logOutAndPassOnboarding();
        TEST_USER.setPassword(NEW_PASSWORD);
        AppInteractionUtil.logIn(TEST_USER);

        // If services page is visible, then login worked
        AppInteractionUtil.waitForServiceCategoriesPage();
    }
}
