package com.handybook.handybook.constant;

import com.handybook.handybook.R;

public enum BookingActionButtonType
{
    RESCHEDULE(BookingAction.ACTION_RESCHEDULE, R.string.reschedule, BookingActionButtonStyle.TEAL),
    CANCEL(BookingAction.ACTION_CANCEL, R.string.cancel_booking, BookingActionButtonStyle.RED_EMPTY),
    CONTACT_PHONE(BookingAction.ACTION_CONTACT_PHONE, R.string.call, BookingActionButtonStyle.CONTACT),
    CONTACT_TEXT(BookingAction.ACTION_CONTACT_TEXT, R.string.text, BookingActionButtonStyle.CONTACT),
    ;

    private String actionName; //must correspond to server's actionName to match up correctly
    private int displayNameId;
    private BookingActionButtonStyle style;

    BookingActionButtonType(String actionName, int displayNameId, BookingActionButtonStyle style)
    {
        this.actionName = actionName;
        this.displayNameId = displayNameId;
        this.style = style;
    }

    public int getBackgroundDrawableId()
    {
        return style.getBackgroundDrawableId();
    }

    public String getActionName()
    {
        return actionName;
    }

    public int getDisplayNameId()
    {
        return displayNameId;
    }

    public int getLayoutTemplateId()
    {
        return style.getLayoutTemplateId();
    }

    public int getTextStyleId()
    {
        return style.getTextStyleId();
    }
}
