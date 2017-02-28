package com.handybook.handybook.library.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;

/**
 * utility class for fragments
 */
public class FragmentUtils {

    /**
     * wrapper method for launching dialog fragments to prevent crash due to IllegalStateException
     * due to this fragment transaction being performed in an asynchronous callback that might be
     * called after onSaveInstanceState()
     *
     * @param dialogFragment
     * @param activity
     * @param tag
     * @return true if the DialogFragment was successfully launched
     */
    public static boolean safeLaunchDialogFragment(
            DialogFragment dialogFragment, FragmentActivity activity, String tag
    ) {
        try {
            dialogFragment.show(activity.getSupportFragmentManager(), tag);
            return true;
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
        return false;
    }

    public static void switchToFragment(
            Fragment currentFragment, Fragment newFragment, boolean addToBackStack
    ) {
        FragmentTransaction transaction = currentFragment.getFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
        }
        else {
            transaction.replace(R.id.fragment_container, newFragment).commit();
        }
    }

    public static void switchToFragment(
            @NonNull AppCompatActivity activity,
            @NonNull Fragment newFragment,
            boolean addToBackStack
    ) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
        }
        else {
            //clears out the whole stack
            activity.getSupportFragmentManager().popBackStackImmediate(null, 0);

            transaction.replace(R.id.fragment_container, newFragment).commit();
        }
    }
}
