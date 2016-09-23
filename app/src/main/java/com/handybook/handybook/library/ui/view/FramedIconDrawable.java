package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

public class FramedIconDrawable extends LayerDrawable
{
    //TODO: can split this class into a more generic one
    //creates a drawable that is framed by another drawable
    public FramedIconDrawable(Context context, int frameResourceId, int iconResourceId, int inset)
    {
        //have to make drawables mutable so that it won't share color filters with any other drawables
        super(new Drawable[]
                {
                        ContextCompat.getDrawable(context, frameResourceId).mutate(),
                        ContextCompat.getDrawable(context, iconResourceId).mutate()
                });
        setLayerInset(1, inset, inset, inset, inset);
    }

    //sets color of non-transparent pixels. safe because this drawable was created using mutate()
    public void setColor(int resolvedColor)
    {
        setColorFilter(resolvedColor, PorterDuff.Mode.SRC_IN);
    }

    public void clearColor()
    {
        clearColorFilter();
    }
}
