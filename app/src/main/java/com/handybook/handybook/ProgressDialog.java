package com.handybook.handybook;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;

final class ProgressDialog extends android.app.ProgressDialog {
    private int delay;
    private boolean wasDismissedCanceled;

    ProgressDialog(final Context context) {
        super(context);
    }

    @Override
    public final void show() {
        wasDismissedCanceled = false;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (wasDismissedCanceled) return;
                ProgressDialog.super.show();
                ProgressDialog.this.getWindow().setGravity(Gravity.CENTER);
            }
        }, delay);
    }

    @Override
    public void dismiss() {
        wasDismissedCanceled = true;
        super.dismiss();
    }

    @Override
    public void cancel() {
        wasDismissedCanceled = true;
        super.cancel();
    }

    final void setDelay(final int delay) {
        this.delay = delay;
    }
}
