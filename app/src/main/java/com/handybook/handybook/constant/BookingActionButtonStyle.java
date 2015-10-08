package com.handybook.handybook.constant;

import com.handybook.handybook.R;

public enum BookingActionButtonStyle
{
    GREEN(
            R.drawable.button_booking_action_green,
            R.style.Button_BookingAction_Green,
            R.layout.template_booking_action_button
    ),
    RED_EMPTY(
            R.drawable.button_booking_action_red_empty,
            R.style.Button_BookingAction_Red_Empty,
            R.layout.template_booking_action_button
    ),
    BLUE(
            R.drawable.button_booking_action_blue,
            R.style.Button_BookingAction_Blue,
            R.layout.template_booking_action_button
    ),
    BLUE_EMPTY(
            R.drawable.button_booking_action_blue_empty,
            R.style.Button_BookingAction_Blue_Empty,
            R.layout.template_booking_action_button
    ),
    GREY(
            R.drawable.button_booking_action_grey,
            R.style.Button_BookingAction_Grey,
            R.layout.template_booking_action_button
    ),
    TEAL(
            R.drawable.button_booking_action_teal,
            R.style.Button_BookingAction_Teal,
            R.layout.template_booking_action_button
    ),
    TEAL_EMPTY(
            R.drawable.button_booking_action_teal_empty,
            R.style.Button_BookingAction_Teal_Empty,
            R.layout.template_booking_action_button
    ),
    CONTACT(
            R.drawable.button_booking_action_white,
            R.style.Button_BookingAction_White,
            R.layout.template_booking_action_button
    ),;

    private int backgroundDrawableId;
    private int textStyleId;
    private int layoutTemplateId;

    BookingActionButtonStyle(int backgroundDrawableId, int textStyleId, int layoutTemplateId)
    {
        this.backgroundDrawableId = backgroundDrawableId;
        this.textStyleId = textStyleId;
        this.layoutTemplateId = layoutTemplateId;
    }

    public int getBackgroundDrawableId()
    {
        return backgroundDrawableId;
    }

    public int getLayoutTemplateId()
    {
        return layoutTemplateId;
    }

    public int getTextStyleId()
    {
        return textStyleId;
    }
}
