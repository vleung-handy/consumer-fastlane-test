package com.handybook.handybook;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;


public class DeepLinkTest
{
    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);

    private static final TestUser TEST_USER = TestUsers.LOGIN;

    @Test
    public void testDeepLinks() throws IOException
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);

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
        ViewUtil.matchToolbarTitle(R.string.my_pro_team);

        uri = Uri.parse("handy://share");
        deepLinkIntent = new Intent(Intent.ACTION_VIEW, uri);
        mActivityRule.getActivity().startActivity(deepLinkIntent);
        ViewUtil.matchToolbarTitle(R.string.free_cleanings);
    }
}
