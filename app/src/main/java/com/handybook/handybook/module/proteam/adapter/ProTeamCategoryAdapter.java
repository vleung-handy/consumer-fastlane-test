package com.handybook.handybook.module.proteam.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.module.proteam.holder.ProTeamProHolder;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProTeamCategoryAdapter extends RecyclerView.Adapter<ProTeamProHolder>
{
    @Nullable
    private User mUser;
    private ProTeamCategoryType mProTeamCategoryType;
    private ProTeam mProTeam;
    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeamProViewModel.OnInteractionListener mOnXClickedListener;

    public ProTeamCategoryAdapter(
            @Nullable final User user,
            @NonNull final ProTeam proTeam,
            @NonNull final ProTeamCategoryType proTeamCategoryType,
            @NonNull final ProTeamProViewModel.OnInteractionListener onInteractionListener
    )
    {
        mUser = user;
        mProTeamCategoryType = proTeamCategoryType;
        mProTeam = proTeam;
        mOnXClickedListener = onInteractionListener;
        initProTeamProViewModels();
    }

    @Override
    public ProTeamProHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final int layoutResId = mUser != null && mUser.isProTeamProfilePicturesEnabled() ?
                R.layout.layout_pro_team_pro_card_v3 : R.layout.layout_pro_team_pro_card_v2;
        final View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(layoutResId, parent, false);
        return new ProTeamProHolder(itemView, mOnXClickedListener);
    }


    @Override
    public void onBindViewHolder(final ProTeamProHolder holder, int position)
    {
        holder.bindProTeamProViewModel(mProTeamProViewModels.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mProTeamProViewModels.size();
    }

    private void initProTeamProViewModels()
    {
        mProTeamProViewModels = new ArrayList<>();
        final ProTeam.ProTeamCategory proTeamCategory = mProTeam.getCategory(mProTeamCategoryType);
        if (proTeamCategory != null)
        {
            final List<ProTeamPro> preferredPros = proTeamCategory.getPreferred();
            if (preferredPros != null)
            {
                for (ProTeamPro eachPro : preferredPros)
                {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            mProTeamCategoryType,
                            ProviderMatchPreference.PREFERRED
                    ));
                }
            }
            final List<ProTeamPro> indifferentPros = proTeamCategory.getIndifferent();
            if (indifferentPros != null)
            {
                for (ProTeamPro eachPro : indifferentPros)
                {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            mProTeamCategoryType,
                            ProviderMatchPreference.INDIFFERENT
                    ));
                }
            }
        }
    }
}
