package com.handybook.handybook.booking.rating;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Mainly used to display rating reasons, handles the ability to select/deselect
 * <p>
 * Created by jtse on 3/31/16.
 */
public class GridDisplayItem implements Serializable {

    private Reason mReason;

    @DrawableRes
    private int mDrawableId;

    private boolean mSelected = false;

    public GridDisplayItem(final Reason reason, final int drawableId, final boolean isSelected) {
        mReason = reason;
        mDrawableId = drawableId;
        mSelected = isSelected;
    }

    public Reason getReason() {
        return mReason;
    }

    public int getDrawableId() {
        return mDrawableId;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(final boolean selected) {
        mSelected = selected;
    }
}
