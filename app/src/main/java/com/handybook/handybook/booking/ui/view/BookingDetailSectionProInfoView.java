package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.ui.view.HorizontalMiniProProfile;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailSectionProInfoView extends BookingDetailSectionView {

    /**
     * fallback that is shown when there is no pro assigned AND no provider assignment state object
     * AND pro teams enabled
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

    @Bind(R.id.element_booking_detail_section_pro_info_prefer_diff_pro_layout)
    ViewGroup mPreferDifferentProLayout;
    @Bind(R.id.element_booking_detail_section_pro_info_prefer_diff_pro_text)
    TextView mPreferDifferentProText;

    /**
     * mini pro profile
     */
    @Bind(R.id.element_booking_detail_section_pro_info_pro_profile)
    HorizontalMiniProProfile mProProfile;

    public BookingDetailSectionProInfoView(final Context context) {
        super(context);
    }

    public BookingDetailSectionProInfoView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingDetailSectionProInfoView(
            final Context context,
            final AttributeSet attrs,
            final int defStyle
    ) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        inflate(getContext(), R.layout.view_booking_detail_section_pro_info, this);
        ButterKnife.bind(this);
    }

    public void setAssignedProTeamMatchIndicatorVisible(boolean visible) {
        mProProfile.setProTeamIndicatorEnabled(visible);
    }

    public void setProProfileVisible(boolean visible) {
        mProProfile.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setAssignedProInfo(
            final Provider provider,
            final Booking.ProviderAssignmentInfo providerAssignmentInfo
    ) {
        mProProfile.setTitle(provider.getFirstNameAndLastInitial());
        mProProfile.setIsProTeam(providerAssignmentInfo.isProTeamMatch());
        mProProfile.setIsProTeamFavorite(provider.isFavorite());
        mProProfile.setRatingAndJobsCount(provider.getAverageRating(), provider.getBookingCount());
        if (providerAssignmentInfo.shouldShowProfileImage()) {
            mProProfile.setImage(provider.getImageUrl());
        }
    }

    public void setLegacyNoProViewProTeamButtonClickListener(@NonNull OnClickListener onClickListener) {
        mLegacyNoProViewProTeamButton.setOnClickListener(onClickListener);
    }

    public void setLegacyNoProViewVisible(boolean visible) {
        mLegacyNoProView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void showPreferDifferentProLayout(boolean show) {
        mPreferDifferentProLayout.setVisibility(show ? VISIBLE : GONE);
    }

    public void setPreferDifferentProOnClickListener(OnClickListener onClickListener) {
        mPreferDifferentProText.setOnClickListener(onClickListener);
    }

    public void setProProfileClickListener(OnClickListener onClickListener)
    {
        mProProfile.setOnClickListener(onClickListener);
    }
}
