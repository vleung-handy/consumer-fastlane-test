package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.booking.constant.BookingActionButtonType;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.library.util.Utils;

/**
 * these are dynamically generated for booking details
 */
public class BookingActionButton extends Button {

    private BookingActionButtonType mBookingActionButtonType;

    public BookingActionButton(Context context) {
        super(context);
    }

    public BookingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookingActionButton(Context context, View parent) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    public void init(String bookingAction, OnClickListener clickListener) {
        mBookingActionButtonType = Utils.getBookingActionButtonType(
                bookingAction);
        if (mBookingActionButtonType == null) {
            Crashlytics.log(
                    "BookingActionButton : No associated action type for : " + bookingAction);
            return;
        }

        setBackgroundResource(mBookingActionButtonType.getBackgroundDrawableId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(mBookingActionButtonType.getTextStyleId());
        }
        else {
            setTextAppearance(getContext(), mBookingActionButtonType.getTextStyleId());
        }

        setLeftDrawable();
        setText(mBookingActionButtonType.getDisplayNameId());
        setId(mBookingActionButtonType.getAccessibilityId());
        setTypeface(TextUtils.get(getContext(), TextUtils.Fonts.CIRCULAR_BOOK));
        if (clickListener != null) {
            setOnClickListener(clickListener);
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        setLeftDrawable();
    }

    private void setLeftDrawable() {
        @DrawableRes
        int drawableResourceId = isEnabled()
                                 ? mBookingActionButtonType.getLeftDrawableResourceId()
                                 : mBookingActionButtonType.getLeftDisabledDrawableResourceId();
        if (drawableResourceId > 0) {
            setCompoundDrawablesWithIntrinsicBounds(
                    drawableResourceId,
                    0,
                    0,
                    0
            );
        }
    }
}
