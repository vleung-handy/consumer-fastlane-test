package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;


/**
 * Created by cdavis on 9/1/15.
 */
    public class BookingDetailSectionProInfoView extends BookingDetailSectionView
{
    /**
     * fallback that is shown when there is no pro assigned AND no provider assignment state object
     * is present
     */
    @Bind(R.id.element_booking_detail_section_pro_info_no_pro_view)
    View mLegacyNoProView;

    /**
     * part of the fallback no pro view
     */
    @Bind(R.id.element_booking_detail_section_pro_info_no_pro_view_pro_team_button)
    Button mLegacyNoProViewProTeamButton;

    @Bind(R.id.action_buttons_layout_slot_1)
    public LinearLayout actionButtonsLayoutSlot1;

    @Bind(R.id.action_buttons_layout_slot_2)
    public LinearLayout actionButtonsLayoutSlot2;

    /**
     * part of assigned pro info layout. shown when pro is part of user's pro team
     */
    @Bind(R.id.element_booking_detail_section_pro_info_assigned_pro_team_match_indicator)
    View mAssignedProInfoProTeamMatchIndicatorView;


    /**
     * part of assigned pro info layout. the pro's display name
     */
    @Bind(R.id.element_booking_detail_section_pro_info_assigned_pro_name_text)
    TextView mAssignedProInfoNameText;

    public BookingDetailSectionProInfoView(final Context context)
    {
        super(context);
    }

    public BookingDetailSectionProInfoView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailSectionProInfoView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setAssignedProTeamMatchIndicatorVisible(boolean visible)
    {
        mAssignedProInfoProTeamMatchIndicatorView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setAssignedProNameTextVisible(boolean visible)
    {
        mAssignedProInfoNameText.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setAssignedProNameText(String assignedProNameText)
    {
        mAssignedProInfoNameText.setText(assignedProNameText);
    }

    public void setLegacyNoProViewProTeamButtonClickListener(@NonNull OnClickListener onClickListener)
    {
        mLegacyNoProViewProTeamButton.setOnClickListener(onClickListener);
    }

    public void setLegacyNoProViewVisible(boolean visible)
    {
        mLegacyNoProView.setVisibility(visible ? VISIBLE : GONE);
    }
}
