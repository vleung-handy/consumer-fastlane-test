package com.handybook.handybook.test.booking;

import android.support.test.espresso.PerformException;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.TextViewUtil;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class OnboardingTest {

    /**
     * Declaring this with launchActivity = false. Need to manually launch during the actual test
     */
    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class, false, false);

    /**
     * Tests a new user onboarding. Clears any existing app data, launches onboarding screen,
     * fills out zip and email, then advances to the home page. The email used is random, to
     * simulate a new user.
     */
    @Ignore
    @Test
    public void testOnboardingV2() {
        //This needs to be done for the test, as to simulate a user that has never used the app
        //before, thus the need to onboard
        AppInteractionUtil.clearSharedPrefs();

        //actually launches the activity
        mActivityRule.launchActivity(null);

        AppInteractionUtil.waitForOnboardZipPage();
        AppInteractionUtil.pauseTestFor(1000);

        //enter a zip, click next.
        TextViewUtil.updateEditTextView(R.id.onboard_edit_zip, "10011");
        onView(withId(R.id.onboard_button_next)).perform(click());

        //enter in giberrish email and continue
        AppInteractionUtil.waitForOnboardEmailPage();
        AppInteractionUtil.pauseTestFor(1000);
        TextViewUtil.updateEditTextView(R.id.onboard_edit_email, getRandomEmail());
        onView(withId(R.id.onboard_button_submit)).perform(click());

        try {
            //checks for the home page
            AppInteractionUtil.waitForServiceCategoriesPage();
        }
        catch (PerformException e) {
            //if it's not there, check for the new bottom nav
            AppInteractionUtil.waitForBottomNavPage();
        }
    }

    private String getRandomEmail() {
        return UUID.randomUUID().toString() + "@handy.com";
    }
}
