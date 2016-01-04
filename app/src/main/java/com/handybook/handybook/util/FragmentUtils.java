package com.handybook.handybook.util;

import android.support.v4.app.DialogFragment;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.ui.activity.BaseActivity;

/**
 * utility class for fragments
 */
public class FragmentUtils
{
    /**
     * wrapper method for launching dialog fragments
     * to prevent crash due to IllegalStateException
     * due to this fragment transaction being performed in an asynchronous
     * callback that might be called after onSaveInstanceState()
     * @param dialogFragment
     * @param activity
     * @param tag
     * @return true if the DialogFragment was successfully launched
     */
    public static boolean safeLaunchDialogFragment(
            DialogFragment dialogFragment, BaseActivity activity, String tag)
    {
        try
        {
            dialogFragment.show(activity.getSupportFragmentManager(), tag);
            return true;
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
        return false;
    }
}
