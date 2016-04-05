package com.handybook.handybook.booking.rating;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Mainly used to display rating reasons, handles the ability to select/deselect
 * <p>
 * Created by jtse on 3/31/16.
 */
public class GridDisplayItem implements Serializable
{
    public Reason reason;

    @DrawableRes
    public int drawableId;

    public boolean selected = false;

    public GridDisplayItem(final Reason reason, final int drawableId, final boolean isSelected)
    {
        this.reason = reason;
        this.drawableId = drawableId;
        this.selected = isSelected;
    }
}
