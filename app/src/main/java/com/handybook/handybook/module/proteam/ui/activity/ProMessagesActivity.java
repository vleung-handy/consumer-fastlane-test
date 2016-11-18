package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.module.reschedule.RescheduleUpcomingActivity;
import com.handybook.shared.AttachmentItemView;
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

        setupCustomAttachmentMenus();
    }

    private void setupCustomAttachmentMenus()
    {
        addAttachmentMenuItem(getRescheduleView());
        addAttachmentMenuItem(getNewBookingView());
    }


    private AttachmentItemView getNewBookingView()
    {
        AttachmentItemView rescheduleView = new AttachmentItemView(this);
//        TODO: JIA: we need the proper icon from Jaclyn
        rescheduleView.getAttachmentImage().setImageResource(R.drawable.ic_help_center);
        rescheduleView.getAttachmentText().setText(getResources().getString(R.string.new_booking));
        rescheduleView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                startActivity(new Intent(
                        ProMessagesActivity.this,
                        ServiceCategoriesActivity.class
                ));
            }
        });

        return rescheduleView;
    }

    private AttachmentItemView getRescheduleView()
    {
        AttachmentItemView rescheduleView = new AttachmentItemView(this);
        rescheduleView.getAttachmentImage().setImageResource(R.drawable.ic_help_past_booking);
        rescheduleView.getAttachmentText().setText(getResources().getString(R.string.reschedule));
        rescheduleView.setOnClickListener(new View.OnClickListener()
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

        return rescheduleView;
    }
}

