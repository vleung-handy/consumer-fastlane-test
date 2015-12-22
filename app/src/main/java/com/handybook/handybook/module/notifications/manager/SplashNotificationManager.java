package com.handybook.handybook.module.notifications.manager;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.module.notifications.model.response.SplashPromo;
import com.handybook.handybook.module.notifications.view.fragment.SplashPromoDialogFragment;
import com.handybook.handybook.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class SplashNotificationManager
{
    /*
        manages the display and requests for splash promos and notifications
        since we don't have push messages for these, we must essentially poll
     */

    private final UserManager mUserManager;
    private final DataManager mDataManager;
    private final Bus mBus;

    //every 30 minutes
    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 30;
    private long mAvailablePromoLastCheckMs = 0;

    @Inject
    public SplashNotificationManager(
            final UserManager userManager, final DataManager dataManager,
            final Bus bus
    )
    {
        mUserManager = userManager;
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onEachActivityResume(final ActivityEvent.Resumed e)
    {
        if (e.getActivity() != null && e.getActivity() instanceof FragmentActivity)
        {
            if(mUserManager.getCurrentUser() != null)
            {
                requestAvailableSplashPromo(mUserManager.getCurrentUser().getId(), (FragmentActivity) e.getActivity());
            }
        }
    }

    private boolean shouldRequestAvailablePromo()
    {
        return System.currentTimeMillis() - mAvailablePromoLastCheckMs > REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    private void requestAvailableSplashPromo(@NonNull String userId, @NonNull final FragmentActivity fragmentActivity)
    {
        if(shouldRequestAvailablePromo())
        {
            mAvailablePromoLastCheckMs = System.currentTimeMillis();
            mDataManager.getAvailableSplashPromo(userId, new DataManager.Callback<SplashPromo>()
            {
                @Override
                public void onSuccess(final SplashPromo response)
                {
                    showSplashPromo(response, fragmentActivity);
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    //do nothing
                }
            });
        }
        //otherwise do nothing

    }

    private void showSplashPromo(@NonNull SplashPromo splashPromo, @NonNull FragmentActivity fragmentActivity)
    {
        //TODO: move this dialog-launching logic out of the manager!
        /*
        BaseActivity cannot subscribe to events, so will have to make a BusEventListener
        to handle the success event + trigger this dialog launch
         */
        if(!fragmentActivity.isFinishing())
        {
            SplashPromoDialogFragment splashPromoDialogFragment =
                    SplashPromoDialogFragment.newInstance(splashPromo);
            FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
            transaction.add(splashPromoDialogFragment, SplashPromoDialogFragment.class.getSimpleName());
            transaction.commitAllowingStateLoss();
        }

    }

}
