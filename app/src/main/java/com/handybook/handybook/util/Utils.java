package com.handybook.handybook.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.BookingActionButtonType;

import java.util.Calendar;
import java.util.Date;

public final class Utils
{

    public static int toDP(final float px, final Context context)
    {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, context.getResources().getDisplayMetrics()));
    }

    static int toDP(final int px, final Context context)
    {
        return toDP((float) px, context);
    }

    public static boolean equalDates(final Date date1, final Date date2)
    {
        final Time time = new Time();
        time.set(date1.getTime());

        final int thenYear = time.year;
        final int thenMonth = time.month;
        final int thenMonthDay = time.monthDay;

        time.set(date2.getTime());

        return (thenYear == time.year) && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static void extendHitArea(final View view, final View parent, final int extra)
    {
        parent.post(new Runnable()
        {
            @Override
            public void run()
            {
                final Rect delegateArea = new Rect();
                view.getHitRect(delegateArea);
                delegateArea.right += extra;
                delegateArea.bottom += extra;

                final TouchDelegate touchDelegate = new TouchDelegate(delegateArea, view);
                if (View.class.isInstance(view.getParent()))
                {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static int interpolateColor(final int color1, final int color2, final float proportion)
    {
        final float[] hsva = new float[3];
        final float[] hsvb = new float[3];

        Color.colorToHSV(color1, hsva);
        Color.colorToHSV(color2, hsvb);

        for (int i = 0; i < 3; i++) hsvb[i] = (hsva[i] + ((hsvb[i] - hsva[i]) * proportion));
        return Color.HSVToColor(hsvb);
    }

    public static BookingActionButtonType getBookingActionButtonType(String actionType)
    {
        for (BookingActionButtonType bat : BookingActionButtonType.values())
        {
            if (actionType.equals(bat.getActionName()))
            {
                return bat;
            }
        }
        Crashlytics.log("Invalid booking action button type : " + actionType);
        return null;
    }

    //returns true if the intent was successfully launched
    public static boolean safeLaunchIntent(Intent intent, Context context)
    {
        if (context == null)
        {
            Crashlytics.logException(new Exception("Trying to launch an intent with a null context!"));
        } else if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
            return true;
        } else //no activity found to handle the intent
        {
            //note: this must be called from the UI thread
            Toast toast = Toast.makeText(context, R.string.error_no_intent_handler_found, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Crashlytics.logException(new Exception("No activity found to handle the intent " + intent.toString()));
        }
        return false;
    }

    public static long minutesPastDate(Date oldDate)
    {
        Date nowDate = Calendar.getInstance().getTime(); // Get time now
        long differenceInMillis = nowDate.getTime() - oldDate.getTime();
        long differenceInMinutes = (differenceInMillis) / 1000L / 60L; // Divide by millis/sec, secs/min
        return differenceInMinutes;
    }

    public static long hoursPastDate(Date oldDate)
    {
        return minutesPastDate(oldDate) / 60L;
    }

    public static void showSoftKeyboard(FragmentActivity activity, View view)
    {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static void hideSoftKeyboard(FragmentActivity activity, View view) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
