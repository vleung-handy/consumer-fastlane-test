package com.handybook.handybook.library.ui.view;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.handybook.handybook.R;

public class HandySnackbar {

    public static final String TYPE_SUCCESS = "success";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_WARNING = "warning";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_DEFAULT = "default";

    private static final String FAB_VIEW_TAG = "fab";

    public static void show(Activity activity, String message) {
        show(activity, message, TYPE_DEFAULT);
    }

    public static void show(Activity activity, String message, String type) {
        final View view = getView(activity);
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        int color = ContextCompat.getColor(activity, getColorForType(type));
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }

    private static int getColorForType(final String type) {
        if (type == null) {
            return R.color.nav_bg;
        }
        switch (type) {
            case TYPE_SUCCESS:
                return R.color.handy_green;
            case TYPE_ERROR:
                return R.color.error_red;
            case TYPE_WARNING:
                return R.color.handy_yellow;
            case TYPE_INFO:
                return R.color.handy_blue;
            case TYPE_DEFAULT:
            default:
                return R.color.nav_bg;

        }
    }

    private static View getView(Activity activity) {
        final View contentView = activity.findViewById(android.R.id.content);
        final View fabView = contentView.findViewWithTag(FAB_VIEW_TAG);
        if (fabView != null) {
            return fabView;
        }
        else {
            return contentView;
        }
    }
}
