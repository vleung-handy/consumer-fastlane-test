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


public class UserInformationUpdateTest
{
    private static final TestUser TEST_USER = TestUsers.UPDATE_PROFILE_USER;

    private static final String NEW_NAME = "test updateuser";
    private static final String NEW_EMAIL = "testupdateprofile@user.com";
    private static final String NEW_PHONE = "9013345567";
    private static final String NEW_PHONE_FORMATTED = "(901) 334-5567";

    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);

    @Test
    public void testUpdateInformation()
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);

        //wait for network call to return with service list
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //Go to My Account - assuming that is at position 5
        //(don't know how to cleanly query nested item)
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.account)).perform(click());

        // Change name, email, phone number and click update
        ViewUtil.waitForViewVisible(R.id.profile_fullname_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.profile_fullname_text, NEW_NAME);
        TextViewUtil.updateEditTextView(R.id.profile_email_text, NEW_EMAIL);
        TextViewUtil.updateEditTextView(R.id.profile_phone_text, NEW_PHONE);
        onView(withId(R.id.profile_update_button)).perform(click());

        // Go somewhere else(Make a Booking, in this case) and come back to profile screen
        ViewUtil.waitForViewVisible(R.id.profile_fullname_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.make_a_booking)).perform(click());
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.account)).perform(click());

        // Confirm that the changes were persisted
        ViewUtil.waitForViewVisible(R.id.profile_fullname_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.assertViewHasText(R.id.profile_fullname_text, NEW_NAME);
        TextViewUtil.assertViewHasText(R.id.profile_email_text, NEW_EMAIL);
        TextViewUtil.assertViewHasText(R.id.profile_phone_text, NEW_PHONE_FORMATTED);
    }
}
