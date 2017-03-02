package com.handybook.handybook.proteam.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.proteam.holder.ProTeamFacebookHolder;
import com.handybook.handybook.proteam.holder.ProTeamProHolder;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProTeamCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FACEBOOK_HEADER = 1;

    private final ProTeam.ProTeamCategory mProTeamCategory;
    private List<ProTeamProViewModel> mProTeamProViewModels;
    private boolean mShouldShowProImage;
    private final ProTeamProViewModel.OnInteractionListener mOnInteractionListener;
    private ProTeamFacebookHolder mFacebookHeaderHolder;
    private int mHeaderCount = 0;
    private boolean mShouldShowHandymanIndicators;

    public ProTeamCategoryAdapter(
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            final boolean shouldShowProImage,
            final boolean shouldShowHandymanIndicators,
            @NonNull final ProTeamProViewModel.OnInteractionListener onInteractionListener
    ) {
        mProTeamCategory = proTeamCategory;
        mShouldShowProImage = shouldShowProImage;
        mShouldShowHandymanIndicators = shouldShowHandymanIndicators;
        mOnInteractionListener = onInteractionListener;
        initProTeamProViewModels();
    }

    public void setFacebookHeaderHolder(ProTeamFacebookHolder proTeamFacebookHolder) {
        mFacebookHeaderHolder = proTeamFacebookHolder;
        enableFacebookHeader(true);
    }

    public void enableFacebookHeader(boolean enable) {
        mHeaderCount = enable ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FACEBOOK_HEADER) {
            return mFacebookHeaderHolder;
        }
        else {
            final View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.layout_pro_team_pro_card, parent, false);
            return new ProTeamProHolder(itemView, mShouldShowProImage, mOnInteractionListener);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position >= mHeaderCount) {
            ((ProTeamProHolder) holder).bindProTeamProViewModel(
                    mProTeamProViewModels.get(position - mHeaderCount));
        }
    }

    @Override
    public int getItemCount() {
        return mProTeamProViewModels.size() + mHeaderCount;
    }

    @Override
    public int getItemViewType(final int position) {
        return position < mHeaderCount ? FACEBOOK_HEADER : super.getItemViewType(position);
    }

    private void initProTeamProViewModels() {
        mProTeamProViewModels = new ArrayList<>();
        if (mProTeamCategory != null) {
            final List<ProTeamPro> preferredPros = mProTeamCategory.getPreferred();
            if (preferredPros != null) {
                for (ProTeamPro eachPro : preferredPros) {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.PREFERRED,
                            mShouldShowHandymanIndicators
                    ));
                }
            }
            final List<ProTeamPro> indifferentPros = mProTeamCategory.getIndifferent();
            if (indifferentPros != null) {
                for (ProTeamPro eachPro : indifferentPros) {
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.INDIFFERENT,
                            mShouldShowHandymanIndicators
                    ));
                }
            }
        }
    }
}
