package com.handybook.handybook;

import android.content.Context;
import android.util.TypedValue;

public final class Utils {

    static int toDP(final float px, final Context context) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, context.getResources().getDisplayMetrics()));
    }

    static int toDP(final int px, final Context context) {
        return toDP((float)px, context);
    }
}
