package com.handybook.handybook.constant;

import com.handybook.handybook.R;

public enum BookingActionButtonType
{
    RESCHEDULE(BookingAction.ACTION_RESCHEDULE, R.string.reschedule, BookingActionButtonStyle.BLUE, R.id.action_button_reschedule_booking),
    CANCEL(BookingAction.ACTION_CANCEL, R.string.cancel_booking, BookingActionButtonStyle.GREY, R.id.action_button_cancel_booking),
    CONTACT_PHONE(BookingAction.ACTION_CONTACT_PHONE, R.string.call, BookingActionButtonStyle.CONTACT, R.id.action_button_contact_phone),
    CONTACT_TEXT(BookingAction.ACTION_CONTACT_TEXT, R.string.text, BookingActionButtonStyle.CONTACT, R.id.action_button_contact_text),
    ;

    private String actionName; //TODO: Enum this?
    private int displayNameId;
    private BookingActionButtonStyle style;
    private int accessibilityId;

    BookingActionButtonType(String actionName, int displayNameId, BookingActionButtonStyle style, int accessibilityId)
    {
        this.actionName = actionName;
        this.displayNameId = displayNameId;
        this.style = style;
        this.accessibilityId = accessibilityId;
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

    public int getAccessibilityId() {
        return accessibilityId;
    }
}
