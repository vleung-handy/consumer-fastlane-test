package com.handybook.handybook.module.proteam.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;
import com.handybook.handybook.ui.view.MiniProProfile;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class ProTeamProHolder extends RecyclerView.ViewHolder
        implements CompoundButton.OnCheckedChangeListener
{
    private ProTeamProViewModel mProTeamProViewModel;
    private boolean mShowProImage;
    private ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    @Bind(R.id.pro_team_pro_card_checkbox)
    CheckBox mCheckbox;
    @Bind(R.id.pro_team_pro_card_profile)
    MiniProProfile mProProfile;

    public ProTeamProHolder(
            View itemView,
            boolean showProImage,
            ProTeamProViewModel.OnInteractionListener onInteractionListener
    )
    {
        super(itemView);
        mShowProImage = showProImage;
        mOnInteractionListener = onInteractionListener;
        ButterKnife.bind(this, itemView);
    }

    public void bindProTeamProViewModel(
            @NonNull final ProTeamProViewModel proTeamProViewModel
    )
    {
        mCheckbox.setOnCheckedChangeListener(null);
        mProTeamProViewModel = proTeamProViewModel;
        mCheckbox.setChecked(mProTeamProViewModel.isChecked());
        mCheckbox.setOnCheckedChangeListener(this);
        mProProfile.setTitle(mProTeamProViewModel.getTitle());
        mProProfile.setRatingAndJobsCount(
                mProTeamProViewModel.getAverageRating(),
                mProTeamProViewModel.getJobsCount()
        );
        mProProfile.setIsProTeam(mProTeamProViewModel.isChecked());
        mProProfile.setProTeamIndicatorEnabled(false);

        if (mShowProImage)
        {
            mProProfile.setImage(mProTeamProViewModel.getImageUrl());
        }
    }

    @OnLongClick(R.id.pro_team_pro_card)
    boolean onLongClick()
    {
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onLongClick(
                    mProTeamProViewModel.getProTeamPro(),
                    mProTeamProViewModel.getProviderMatchPreference()
            );
        }
        return true;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
    {
        mProTeamProViewModel.setChecked(isChecked);
        mProProfile.setIsProTeam(isChecked);
        if (mOnInteractionListener != null)
        {
            mOnInteractionListener.onCheckedChanged(
                    mProTeamProViewModel.getProTeamPro(),
                    isChecked
            );
        }
    }
}
