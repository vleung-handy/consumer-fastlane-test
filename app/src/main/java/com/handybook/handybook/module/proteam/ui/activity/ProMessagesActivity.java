package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
    private int mAttachmentViewItemHeight;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mProviderId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);
        mAttachmentViewItemHeight = getResources().getDimensionPixelSize(R.dimen.attachment_item_height);
        setupCustomAttachmentMenus();
    }

    private void setupCustomAttachmentMenus()
    {
        addAttachmentMenuItem(getRescheduleView());
        addAttachmentMenuItem(getNewBookingView());
    }


    private AttachmentItemView getNewBookingView()
    {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_make_booking);
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.new_booking));
        attachmentItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getAttachmentMenu().dismiss();
                startActivity(new Intent(
                        ProMessagesActivity.this,
                        ServiceCategoriesActivity.class
                ));
            }
        });

        return attachmentItemView;
    }

    private AttachmentItemView getRescheduleView()
    {
        AttachmentItemView attachmentItemView = new AttachmentItemView(this);
        attachmentItemView.getAttachmentImage().setImageResource(R.drawable.ic_reschedule_booking);
        attachmentItemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mAttachmentViewItemHeight
        ));
        attachmentItemView.getAttachmentText()
                          .setText(getResources().getString(R.string.reschedule));
        attachmentItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getAttachmentMenu().dismiss();
                Intent intent = new Intent(
                        ProMessagesActivity.this,
                        RescheduleUpcomingActivity.class
                );

                intent.putExtra(BundleKeys.PROVIDER_ID, mProviderId);
                startActivity(intent);
            }
        });

        return attachmentItemView;
    }
}

