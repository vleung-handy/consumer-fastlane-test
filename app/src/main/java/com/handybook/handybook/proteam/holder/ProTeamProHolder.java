package com.handybook.handybook.proteam.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.HorizontalMiniProProfile;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
        implements CompoundButton.OnCheckedChangeListener {

    private ProTeamProViewModel mProTeamProViewModel;
    private boolean mShowProImage;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    @BindView(R.id.pro_team_pro_card_checkbox)
    CheckBox mCheckbox;
    @BindView(R.id.pro_team_pro_card_profile)
    HorizontalMiniProProfile mProProfile;

    public ProTeamProHolder(
            View itemView,
            boolean showProImage,
            ProTeamProViewModel.OnInteractionListener onInteractionListener
    ) {
        super(itemView);
        mShowProImage = showProImage;
        mOnInteractionListener = onInteractionListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindProTeamProViewModel(
            @NonNull final ProTeamProViewModel proTeamProViewModel
    ) {
        mCheckbox.setOnCheckedChangeListener(null);
        mProTeamProViewModel = proTeamProViewModel;
        if(mProTeamProViewModel.shouldShowCheckbox()) {
            mCheckbox.setVisibility(View.VISIBLE);
            mCheckbox.setChecked(mProTeamProViewModel.isChecked());
            mCheckbox.setOnCheckedChangeListener(this);
        } else {
            mCheckbox.setVisibility(View.GONE);
        }
        mProProfile.setTitle(mProTeamProViewModel.getTitle());
        mProProfile.setRatingAndJobsCount(
                mProTeamProViewModel.getAverageRating(),
                mProTeamProViewModel.getJobsCount()
        );

        mProProfile.setIsProTeam(mProTeamProViewModel.isChecked());
        mProProfile.setIsProTeamFavorite(mProTeamProViewModel.isFavorite());
        mProProfile.setProTeamIndicatorEnabled(false);
        mProProfile.setHandymanIndicatorEnabled(mProTeamProViewModel.isHandymanIndicatorEnabled());

        if (mShowProImage) {
            mProProfile.setImage(mProTeamProViewModel.getImageUrl());
        }
    }

    @OnLongClick(R.id.pro_team_pro_card)
    boolean onLongClick() {
        if (mOnInteractionListener != null) {
            mOnInteractionListener.onLongClick(
                    mProTeamProViewModel.getProTeamPro(),
                    mProTeamProViewModel.getProviderMatchPreference()
            );
        }
        return true;
    }

    @OnClick(R.id.pro_team_pro_card)
    void onClick() {
        mCheckbox.setChecked(!mCheckbox.isChecked());
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        mProTeamProViewModel.setChecked(isChecked);
        mProProfile.setIsProTeam(isChecked);
        mProProfile.setIsProTeamFavorite(mProTeamProViewModel.isFavorite());
        if (mOnInteractionListener != null) {
            mOnInteractionListener.onCheckedChanged(
                    mProTeamProViewModel.getProTeamPro(),
                    isChecked
            );
        }
    }
}
