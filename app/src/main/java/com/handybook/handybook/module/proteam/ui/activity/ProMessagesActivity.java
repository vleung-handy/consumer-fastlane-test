package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.module.reschedule.RescheduleUpcomingActivity;
import com.handybook.shared.builtin.MessagesListActivity;

/**
 * This is a derivation of the MessagesListActivity that allows for a reschedule flow
 */
public class ProMessagesActivity extends MessagesListActivity
{
    private String mProviderId;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mProviderId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);

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
        getAttachmentButton().setImageDrawable(ContextCompat.getDrawable(
                this,
                R.drawable.ic_calendar_dark
        ));
        getAttachmentButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {

                Intent intent = new Intent(
                        ProMessagesActivity.this,
                        RescheduleUpcomingActivity.class
                );

                intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
                startActivity(intent);
            }
        });


    }
}
