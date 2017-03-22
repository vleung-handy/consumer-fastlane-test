package com.handybook.handybook.test;

import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

//note that animations should be disabled on the device running these tests
public class LoginTest {

    private static final TestUser TEST_USER = TestUsers.LOGIN;

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    @Test
    public void testLogin() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();
    }
}
