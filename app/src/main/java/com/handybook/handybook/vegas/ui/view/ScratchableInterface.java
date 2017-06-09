package com.handybook.handybook.vegas.ui.view;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public interface ScratchableInterface {

    /**
     * Whether the view receive user on touch motion
     *
     * @return true if scratchable
     */
    boolean isScratchable();

    /**
     * If true, set the view allow receive on touch to reveal the view
     * By default, scratchable is true
     *
     * @param flag - flag for enable/disable scratch
     */
    void setScratchable(boolean flag);

    /**
     * Set the radius of the brush (in pixels)
     *
     * @param radiusPx - radius radiusPx of circle in pixel unit
     */
    void setBrushRadius(int radiusPx);

    /**
     * Togle anti-aliasing of brush
     * By default, anti alias is turn off
     *
     * @param flag - set true to turn on anti-alias
     */
    void setAntiAlias(boolean flag);

    /**
     * Reset the scratch view
     *
     */
    void resetView();

    /**
     * Set the color of overlay
     *
     * @param ResId - resources identifier for color in INT type
     */
    void setOverlayColor(int ResId);

    /**
     * Set drawable for scratch view
     *
     * @param drawable - Set drawable for scratch view
     */
    void setScratchDrawable(Drawable drawable);

    /**
     * Set bitmap for scratch view
     *
     * @param bitmap - Set bitmap for scratch view
     */
    void setScratchBitmap(Bitmap bitmap);

    /**
     * Get scratched ratio (contribution from daveyfong)
     *
     * @return float - return Scratched ratio
     */
    float getRevealedRatio();

    /**
     * Get scratched ratio (contribution from daveyfong)
     *
     * @param speed - Scratch speed
     * @return float - return Scratched ratio
     */
    float getRevealedRatio(int speed);

    void setOnScratchCallback(OnScratchCallback callback);

    void revealAll();

    void setBackgroundClickable(boolean clickable);

    interface OnScratchCallback {

        void onScratch(float percentage);

        //Call back funtion to monitor the status of finger
        void onDetach(boolean fingerDetach);
    }

}
