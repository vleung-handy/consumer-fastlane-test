package com.handybook.handybook.booking.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.library.util.TransitionListenerAdapter;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class ServicesActivity extends MenuDrawerActivity
{
    public static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";

    ServicesFragment mFragment;

    @Override
    protected final Fragment createFragment()
    {
        final Service service = getIntent().getParcelableExtra(EXTRA_SERVICE);
        mFragment = ServicesFragment.newInstance(service);
        return mFragment;
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Transition transition = TransitionInflater
                    .from(this)
                    .inflateTransition(R.transition.changebounds_with_arcmotion);

            getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new TransitionListenerAdapter()
            {
                @Override
                public void onTransitionEnd(final Transition transition)
                {
                    if (mFragment != null)
                    {
                        mFragment.revealHeader();
                    }
                }
            });
        }
    }
}
