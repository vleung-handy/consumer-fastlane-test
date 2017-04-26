package com.handybook.handybook.core.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.event.UserLoggedInEvent;
import com.handybook.handybook.core.ui.fragment.LoginFragment;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public final class LoginActivity extends BaseActivity {

    public static final String EXTRA_FROM_BOOKING_FUNNEL
            = "com.handy.handy.EXTRA_FROM_BOOKING_FUNNEL";
    public static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    public static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    public static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    public static final String EXTRA_FROM_ONBOARDING = "com.handy.handy.EXTRA_FROM_ONBOARDING";

    private Object mBusEventListener;

    @Inject
    LayerHelper mLayerHelper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBusEventListener = new Object() {
            @Subscribe
            public void userAuthUpdated(final UserLoggedInEvent event) {
                checkLayerInitiation();
            }
        };

        final boolean findUser = getIntent().getBooleanExtra(EXTRA_FIND_USER, false);
        final String userName = getIntent().getStringExtra(EXTRA_BOOKING_USER_NAME);
        final String userEmail = getIntent().getStringExtra(EXTRA_BOOKING_EMAIL);
        final boolean fromBookingFunnel = getIntent().getBooleanExtra(
                EXTRA_FROM_BOOKING_FUNNEL,
                false
        );
        final boolean fromOnboarding = getIntent().getBooleanExtra(EXTRA_FROM_ONBOARDING, false);

        LoginFragment fragment = LoginFragment.newInstance(
                findUser,
                userName,
                userEmail,
                fromBookingFunnel,
                fromOnboarding
        );
        fragment.setArguments(getIntent().getExtras());
        final FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.login_fragment_container, fragment).commit();
    }

    /**
     * Layer needs to be initialized under these 2 conditions, and we have to check these conditions
     * on user events (login, logout), and if the config parameter changes
     */
    private void checkLayerInitiation() {
        //chat is enabled, so we'll login if the user is available
        User user = mUserManager.getCurrentUser();

        if (user != null) {
            mLayerHelper.initLayer(user.getAuthToken());
        }
        else {
            //the user is in a logged out state
            mLayerHelper.deauthenticate();
        }
        //The menu should always be refreshed
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(mBusEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(mBusEventListener);
    }

    @Override
    protected void onActivityResult(
            final int requestCode, final int resultCode,
            final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.LOGIN_FINISH) { finish(); }
    }
}
