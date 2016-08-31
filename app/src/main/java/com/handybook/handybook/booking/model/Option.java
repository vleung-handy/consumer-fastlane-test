package com.handybook.handybook.booking.model;

/**
 * model that defines how an option from an options list should behave
 */
public interface Option
{
    boolean isDefault();
    String getTitleText();
    String getSubtitleText();
}
