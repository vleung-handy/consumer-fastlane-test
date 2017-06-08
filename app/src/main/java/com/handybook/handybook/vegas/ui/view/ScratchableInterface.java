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
     * Set the color of overlay
     *
     * @param ResId - resources identifier for color in INT type
     */
    void setOverlayColor(int ResId);

    /**
     * Set the radius size of the circle to be revealed
     *
     * @param size - radius size of circle in pixel unit
     */
    void setRevealSize(int size);

    /**
     * Set turn on/off effect of anti alias of circle revealed
     * By default, anti alias is turn off
     *
     * @param flag - set true to turn on anti alias
     */
    void setAntiAlias(boolean flag);

    /**
     * Reset the scratch view
     *
     */
    void resetView();

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
    float getScratchedRatio();

    /**
     * Get scratched ratio (contribution from daveyfong)
     *
     * @param speed - Scratch speed
     * @return float - return Scratched ratio
     */
    float getScratchedRatio(int speed);

    void setOnScratchCallback(OnScratchCallback callback);

    void setScratchAll(boolean scratchAll);

    void setBackgroundClickable(boolean clickable);

    interface OnScratchCallback {

        void onScratch(float percentage);

        //Call back funtion to monitor the status of finger
        void onDetach(boolean fingerDetach);
    }

}
