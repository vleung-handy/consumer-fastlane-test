package com.handybook.handybook.library.ui.view;

import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import java.lang.ref.WeakReference;

public final class ProgressDialog extends android.app.ProgressDialog
{
    private int delay;
    private boolean wasDismissedCanceled;
    private WeakReference<Activity> mContextWeakReference;

    public ProgressDialog(final Activity activity)
    {
        super(activity);
        mContextWeakReference = new WeakReference<>(activity);
    }

    @Override
    public final void show()
    {
        wasDismissedCanceled = false;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Activity activity = mContextWeakReference.get(); // No activity => no dialog
                if (activity == null || activity.isFinishing() || wasDismissedCanceled)
                {
                    return;
                }
                try
                {
                    ProgressDialog.super.show();
                    ProgressDialog.this.getWindow().setGravity(Gravity.CENTER);
                }
                catch (WindowManager.BadTokenException e)
                {
                    Crashlytics.logException(e);
                }
            }
        }, delay);
    }

    @Override
    public void dismiss()
    {
        wasDismissedCanceled = true;
        super.dismiss();
    }

    @Override
    public void cancel()
    {
        wasDismissedCanceled = true;
        super.cancel();
    }

    public final void setDelay(final int delay)
    {
        this.delay = delay;
    }
}
