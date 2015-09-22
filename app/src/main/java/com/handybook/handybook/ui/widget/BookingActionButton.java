package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.BookingActionButtonType;
import com.handybook.handybook.util.TextUtils;
import com.handybook.handybook.util.Utils;

public class BookingActionButton extends Button
{
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

    public void init(String bookingAction, OnClickListener clickListener)
    {
        final BookingActionButtonType bookingActionButtonType = Utils.getBookingActionButtonType(bookingAction);
        if(bookingActionButtonType == null)
        {
            Crashlytics.log("BookingActionButton : No associated action type for : " + bookingAction);
            return;
        }

        //final BookingActionButton self = this;
        //associatedFragment = fragment;
        setBackgroundResource(bookingActionButtonType.getBackgroundDrawableId());
        setTextAppearance(getContext(), bookingActionButtonType.getTextStyleId());
        setText(bookingActionButtonType.getDisplayNameId());
        setTypeface(TextUtils.get(getContext(), TextUtils.Fonts.CIRCULAR_BOOK));

//        setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(final View v)
//            {
//                //self.associatedFragment.onActionButtonClick(bookingActionButtonType);
//
//                //TODO: When an action button is clicked do the appropriate thing
//            }
//        });

        if(clickListener != null)
        {
            setOnClickListener(clickListener);
        }

        //setEnabled(data.isEnabled());
    }

//    private BookingActionButtonType getBookingActionButtonType(String actionType)
//    {
//        switch(actionType)
//        {
//            case BookingAction.ACTION_CANCEL: return BookingActionButtonType.CANCEL;
//            case BookingAction.ACTION_CONTACT_PHONE: return BookingActionButtonType.CONTACT_PHONE;
//            case BookingAction.ACTION_CONTACT_TEXT: return BookingActionButtonType.CONTACT_TEXT;
//            case BookingAction.ACTION_RESCHEDULE: return BookingActionButtonType.RESCHEDULE;
//        }
//        return null;
//    }

}
