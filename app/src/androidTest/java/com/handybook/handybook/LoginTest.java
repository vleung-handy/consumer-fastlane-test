package com.handybook.handybook;

import android.support.test.runner.AndroidJUnit4;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class LoginTest
{
    private static final TestUser TEST_USER = TestUsers.LOGIN;

    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    @Test
    public void testLogin()
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();
    }
}
