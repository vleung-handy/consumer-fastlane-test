package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.ui.view.HorizontalMiniProProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingDetailSectionProInfoView extends BookingDetailSectionView {

    @BindView(R.id.action_buttons_layout_slot_1)
    public LinearLayout actionButtonsLayoutSlot1;

    @BindView(R.id.action_buttons_layout_slot_2)
    public LinearLayout actionButtonsLayoutSlot2;

    /**
     * mini pro profile
     */
    @BindView(R.id.element_booking_detail_section_pro_info_pro_profile)
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

    public void setProProfileClickListener(OnClickListener onClickListener)
    {
        mProProfile.setOnClickListener(onClickListener);
    }
}
