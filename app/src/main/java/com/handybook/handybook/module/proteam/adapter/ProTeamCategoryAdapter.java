package com.handybook.handybook.module.proteam.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.User;
import com.handybook.handybook.module.proteam.holder.ProTeamFacebookHolder;
import com.handybook.handybook.module.proteam.holder.ProTeamProHolder;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.module.proteam.model.ProTeamPro;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProTeamCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int FACEBOOK_HEADER = 1;

    @Nullable
    private User mUser;
    private ProTeamCategoryType mProTeamCategoryType;
    private ProTeam mProTeam;
    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    private ProTeamFacebookHolder mFacebookHeaderHolder;
    private int mHeaderCount = 0;

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
        mOnInteractionListener = onInteractionListener;
        initProTeamProViewModels();
    }

    public void setFacebookHeaderHolder(ProTeamFacebookHolder proTeamFacebookHolder)
    {
        mFacebookHeaderHolder = proTeamFacebookHolder;
        enableFacebookHeader(true);
    }

    public void enableFacebookHeader(boolean enable)
    {
        mHeaderCount = enable ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == FACEBOOK_HEADER)
        {
            return mFacebookHeaderHolder;
        }
        else
        {
            final View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.layout_pro_team_pro_card, parent, false);
            final boolean showProImage = mUser != null && mUser.isProTeamProfilePicturesEnabled();
            return new ProTeamProHolder(itemView, mOnInteractionListener, showProImage);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        if (position >= mHeaderCount)
        {
            ((ProTeamProHolder) holder).bindProTeamProViewModel(
                    mProTeamProViewModels.get(position - mHeaderCount));
        }
    }

    @Override
    public int getItemCount()
    {
        return mProTeamProViewModels.size() + mHeaderCount;
    }

    @Override
    public int getItemViewType(final int position)
    {
        return position < mHeaderCount ? FACEBOOK_HEADER : super.getItemViewType(position);
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
                            ProviderMatchPreference.INDIFFERENT
                    ));
                }
            }
        }
    }
}
