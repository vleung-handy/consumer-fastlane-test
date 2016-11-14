package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.RescheduleUpcomingActivity;
import com.handybook.shared.builtin.MessagesListActivity;
import com.layer.atlas.AtlasMessageComposer;

/**
 * This is a derivation of the MessagesListActivity that allows for a reschedule flow
 */
public class ProMessagesActivity extends MessagesListActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //we want to show the reschedule icon in the spot where the attachment icon is, so we
        //need to hijact the hell out of it.
        hijackAttachmentButton();
    }

    /**
     * TODO: JIA: maybe put this under some kind of a flag, because not everything can be
     * "rescheduled"
     */
    private void hijackAttachmentButton()
    {
        //Step 1, get the message composer
        AtlasMessageComposer messageComposer = (AtlasMessageComposer) findViewById(R.id.messages_composer);

        //Step 2, get the attachment button
        if (messageComposer != null)
        {
            ImageView mAttachButton = (ImageView) messageComposer.findViewById(com.layer.atlas.R.id.attachment);

            if (mAttachButton != null)
            {
                mAttachButton.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_calendar_dark
                ));
                mAttachButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        startActivity(new Intent(
                                ProMessagesActivity.this,
                                RescheduleUpcomingActivity.class
                        ));
                    }
                });
            }
        }


    }
}
