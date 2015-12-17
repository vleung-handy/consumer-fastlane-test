package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.booking.constant.BookingActionButtonType;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

public class BookingActionButton extends Button
{
    public BookingActionButton(Context context)
    {
        super(context);
    }

    public BookingActionButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingActionButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public BookingActionButton(Context context, View parent)
    {
        super(context);
    }

    public void init(String bookingAction, OnClickListener clickListener)
    {
        final BookingActionButtonType bookingActionButtonType = Utils.getBookingActionButtonType(bookingAction);
        if (bookingActionButtonType == null)
        {
            Crashlytics.log("BookingActionButton : No associated action type for : " + bookingAction);
            return;
        }

        setBackgroundResource(bookingActionButtonType.getBackgroundDrawableId());
        setTextAppearance(getContext(), bookingActionButtonType.getTextStyleId());
        setText(bookingActionButtonType.getDisplayNameId());
        setId(bookingActionButtonType.getAccessibilityId());
        setTypeface(TextUtils.get(getContext(), TextUtils.Fonts.CIRCULAR_BOOK));
        if (clickListener != null)
        {
            setOnClickListener(clickListener);
        }
    }
}
