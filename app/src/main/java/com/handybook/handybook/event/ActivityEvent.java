package com.handybook.handybook.event;

import android.app.Activity;
import android.os.Bundle;

/**
 * Collection of events launched for each of all activities lifecycle methods
 */
public interface ActivityEvent
{
    interface ActivityHolder
    {
        Activity getActivity();
    }


    interface BundleHolder
    {
        Bundle getBundle();
    }


    class DefaultActivityHolder implements ActivityEvent, ActivityHolder
    {
        private Activity mActivity;

        public DefaultActivityHolder(final Activity activity)
        {
            this.mActivity = activity;
        }

        @Override
        public Activity getActivity()
        {
            return mActivity;
        }
    }


    class DefaultActivityAndBundleHolder implements
            ActivityEvent,
            ActivityHolder,
            BundleHolder
    {
        private Activity mActivity;
        private Bundle mBundle;

        public DefaultActivityAndBundleHolder(final Activity activity, final Bundle bundle)
        {
            mActivity = activity;
            mBundle = bundle;
        }

        @Override
        public Bundle getBundle()
        {
            return mBundle;
        }

        @Override
        public Activity getActivity()
        {
            return mActivity;
        }
    }


    // Created
    class Created extends DefaultActivityAndBundleHolder
    {
        public Created(final Activity activity, final Bundle bundle)
        {
            super(activity, bundle);
        }
    }


    // Started
    class Started extends DefaultActivityHolder
    {
        public Started(final Activity activity)
        {
            super(activity);
        }
    }


    // Resumed
    class Resumed extends DefaultActivityHolder
    {
        public Resumed(final Activity activity)
        {
            super(activity);
        }
    }


    // Paused
    class Paused extends DefaultActivityHolder
    {
        public Paused(final Activity activity)
        {
            super(activity);
        }
    }


    // SavedInstanceState
    class SavedInstanceState extends DefaultActivityAndBundleHolder
    {
        public SavedInstanceState(final Activity activity, final Bundle bundle)
        {
            super(activity, bundle);
        }
    }


    // Stopped
    class Stopped extends DefaultActivityHolder
    {
        public Stopped(final Activity activity)
        {
            super(activity);
        }
    }


    // Destroyed
    class Destroyed extends DefaultActivityHolder
    {
        public Destroyed(final Activity activity)
        {
            super(activity);
        }
    }

}