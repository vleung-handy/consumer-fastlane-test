package com.handybook.handybook.tool.user;

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

public class UserInformationUpdateTest {

    private static final TestUser TEST_USER = TestUsers.UPDATE_PROFILE_USER;

    private static final String NEW_NAME = "test updateuser";
    private static final String NEW_EMAIL = "testupdateprofile@user.com";
    private static final String NEW_PHONE = "9013345567";
    private static final String NEW_PHONE_FORMATTED = "(901) 334-5567";

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    @Test
    public void testUpdateInformation() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //navigate to account page
        onView(withId(R.id.account)).perform(click());

        // Click into contact info page
        ViewUtil.waitForTextNotVisible(R.string.loading, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewInScrollViewVisible(
                R.id.account_contact_info_layout,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.account_contact_info_layout)).perform(click());

        // Change name, email, phone number and click update
        ViewUtil.waitForViewVisible(R.id.contact_name_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.contact_name_text, NEW_NAME);
        TextViewUtil.updateEditTextView(R.id.contact_email_text, NEW_EMAIL);
        TextViewUtil.updateEditTextView(R.id.contact_phone_text, NEW_PHONE);
        onView(withId(R.id.contact_update_button)).perform(click());

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

        // Go somewhere else(Make a Booking, in this case) and come back to profile screen
        pressBack();

        onView(withId(R.id.bookings)).perform(click());

        onView(withId(R.id.account)).perform(click());

        // Click into contact info page
        ViewUtil.waitForTextNotVisible(R.string.loading, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewInScrollViewVisible(
                R.id.account_contact_info_layout,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.account_contact_info_layout)).perform(click());

        // Confirm that the changes were persisted
        ViewUtil.waitForViewVisible(R.id.contact_name_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.assertViewHasText(R.id.contact_name_text, NEW_NAME);
        TextViewUtil.assertViewHasText(R.id.contact_email_text, NEW_EMAIL);
        TextViewUtil.assertViewHasText(R.id.contact_phone_text, NEW_PHONE_FORMATTED);
    }
}
