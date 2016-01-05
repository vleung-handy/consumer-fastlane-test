package com.handybook.handybook.core;

import android.view.View;

/**
 * a click listener that only allows one click to be processed at a time
 * which will help prevent certain bugs caused by clicking a button twice in quick succession
 *
 * obviously this will not work if there is an async callback in onOneClick
 */
public abstract class OnOneClickListener implements View.OnClickListener
    //TODO: give this a better name + package
{
    private boolean mClickable = true;

    public abstract void onOneClick(View v);

    @Override
    public void onClick(final View v)
    {
        if(mClickable)
        {
            setClickable(false);
            try
            {
                onOneClick(v);
            }
            finally
            {
                //set back to clickable no matter what, even if exception occurs
                setClickable(true);
            }
        }
        //do nothing. don't add click events to the event queue while a click is being processed
    }

    private void setClickable(boolean clickable)
    {
        mClickable = clickable;
    }

}
