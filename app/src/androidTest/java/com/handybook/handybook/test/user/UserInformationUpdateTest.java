package com.handybook.handybook.test.user;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
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

public class UserInformationUpdateTest {

    private static final TestUser TEST_USER = TestUsers.UPDATE_PROFILE_USER;

    private static final String NEW_NAME = "test updateuser";
    private static final String NEW_EMAIL = "testupdateprofile@user.com";
    private static final String NEW_PHONE = "9013345567";
    private static final String NEW_PHONE_FORMATTED = "(901) 334-5567";

    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    @Test
    public void testUpdateInformation() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //Go to My Account - assuming that is at position 5
        //(don't know how to cleanly query nested item)
        AppInteractionUtil.openDrawer();
        ViewUtil.waitForTextVisible(R.string.account, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withText(R.string.account)).perform(click());

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
        ViewUtil.waitForViewVisible(R.id.contact_name_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        AppInteractionUtil.openDrawer();
        ViewUtil.waitForTextVisible(R.string.make_a_booking, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withText(R.string.make_a_booking)).perform(click());
        AppInteractionUtil.openDrawer();
        ViewUtil.waitForTextVisible(R.string.account, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withText(R.string.account)).perform(click());

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
