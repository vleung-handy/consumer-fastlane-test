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
    private Reason reason;

    @DrawableRes
    private int drawableId;

    private boolean selected = false;

    public GridDisplayItem(final Reason reason, final int drawableId, final boolean isSelected)
    {
        this.reason = reason;
        this.drawableId = drawableId;
        this.selected = isSelected;
    }

    public Reason getReason()
    {
        return reason;
    }

    public int getDrawableId()
    {
        return drawableId;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(final boolean selected)
    {
        this.selected = selected;
    }
}
