package com.handybook.handybook.module.proteam.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.holder.ProTeamProHolder;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

public class ProTeamCategoryAdapter extends RecyclerView.Adapter<ProTeamProHolder>
{
    private ProTeamCategoryType mProTeamCategoryType;
    private ProviderMatchPreference mProviderMatchPreference;
    private ProTeam mProTeam;
    private final ProTeamProViewModel.OnInteractionListener mOnXClickedListener;

    public ProTeamCategoryAdapter(
            @NonNull final ProTeam proTeam,
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @NonNull final ProviderMatchPreference providerMatchPreference,
            @NonNull final ProTeamProViewModel.OnInteractionListener onInteractionListener
    )
    {
        mProTeamCategoryType = proTeamCategoryType;
        mProviderMatchPreference = providerMatchPreference;
        mProTeam = proTeam;
        mOnXClickedListener = onInteractionListener;
    }

    @Override
    public ProTeamProHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_pro_team_pro_card, parent, false);
        return new ProTeamProHolder(itemView, mOnXClickedListener);
    }

    @Override
    public void onBindViewHolder(final ProTeamProHolder holder, int position)
    {
        final ProTeamPro proTeamPro = mProTeam
                .getCategory(mProTeamCategoryType)
                .get(mProviderMatchPreference)
                .get(position);
        holder.bindBookingCardViewModel(proTeamPro, mProviderMatchPreference);
    }

    @Override
    public int getItemCount()
    {
        return mProTeam.getCategory(mProTeamCategoryType).get(mProviderMatchPreference).size();
    }

}
