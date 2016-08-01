package com.handybook.handybook;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class LoginTest
{
    private static final TestUser TEST_USER = TestUsers.LOGIN;

    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);

    @Test
    public void testLogin()
    {
        AppInteractionUtil.logOutAndPassOnboarding();

        AppInteractionUtil.logIn(TEST_USER);

        //wait for network call to return with service list
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
