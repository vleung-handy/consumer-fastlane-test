package com.handybook.handybook.ui.widget;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.handybook.handybook.R;

public class HandySnackbar
{
    public static final String TYPE_SUCCESS = "success";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_WARNING = "warning";
    public static final String TYPE_INFO = "info";
    public static final String TYPE_DEFAULT = "default";

    public static void show(Activity activity, String message, String type)
    {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG);
        int color = activity.getResources().getColor(getColorForType(type));
        snackbar.getView().setBackgroundColor(color);
        snackbar.show();
    }

    private static int getColorForType(final String type)
    {
        if (type == null)
        {
            return R.color.nav_bg;
        }
        switch (type)
        {
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
}
