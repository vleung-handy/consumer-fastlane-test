package com.handybook.handybook;

import android.content.Intent;
import android.net.Uri;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class DeepLinkTest {

    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    private static final TestUser TEST_USER = TestUsers.LOGIN;

    @Test
    public void testDeepLinks() throws IOException {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        Uri uri;
        Intent deepLinkIntent;

        uri = Uri.parse("handy://bookings");
        deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
        mActivityRule.getActivity().startActivity(deepLinkIntent);
        ViewUtil.matchToolbarTitle(R.string.my_bookings);

        uri = Uri.parse("handy://account");
        deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
        mActivityRule.getActivity().startActivity(deepLinkIntent);
        ViewUtil.matchToolbarTitle(R.string.account);

        uri = Uri.parse("handy://pro_team");
        deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
        mActivityRule.getActivity().startActivity(deepLinkIntent);
        // We cannot use ViewUtil.matchToolbarTitle() here because this toolbar contains additional text view
        ViewUtil.waitForViewVisible(R.id.pro_team_toolbar, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //Note: When snow is activated on this screen, the tests will fail because Espresso's
        // wait for view requires the app to be idle, while snow is constantly updating
        uri = Uri.parse("handy://share");
        deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
        mActivityRule.getActivity().startActivity(deepLinkIntent);
        ViewUtil.waitForViewVisible(R.id.fragment_referral_content, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.matchToolbarTitle(R.string.free_cleanings);
    }

}
