package com.handybook.handybook.event;

import android.app.Activity;
import android.os.Bundle;

/**
 * Collection of events launched for each of all activities lifecycle methods
 */
public abstract class ActivityEvent
{
    public interface ActivityHolder
    {
        Activity getActivity();
    }


    public interface BundleHolder
    {
        Bundle getBundle();
    }


    static class DefaultActivityHolder implements ActivityHolder
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


    static class DefaultActivityAndBundleHolder implements ActivityHolder, BundleHolder
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
    public static class Created extends DefaultActivityAndBundleHolder
    {
        public Created(final Activity activity, final Bundle bundle)
        {
            super(activity, bundle);
        }
    }


    // Started
    public static class Started extends DefaultActivityHolder
    {
        public Started(final Activity activity)
        {
            super(activity);
        }
    }


    // Resumed
    public static class Resumed extends DefaultActivityHolder
    {
        public Resumed(final Activity activity)
        {
            super(activity);
        }
    }


    // Paused
    public static class Paused extends DefaultActivityHolder
    {
        public Paused(final Activity activity)
        {
            super(activity);
        }
    }


    // SavedInstanceState
    public static class SavedInstanceState extends DefaultActivityAndBundleHolder
    {
        public SavedInstanceState(final Activity activity, final Bundle bundle)
        {
            super(activity, bundle);
        }
    }


    // Stopped
    public static class Stopped extends DefaultActivityHolder
    {
        public Stopped(final Activity activity)
        {
            super(activity);
        }
    }


    // Destroyed
    public static class Destroyed extends DefaultActivityHolder
    {
        public Destroyed(final Activity activity)
        {
            super(activity);
        }
    }

}
