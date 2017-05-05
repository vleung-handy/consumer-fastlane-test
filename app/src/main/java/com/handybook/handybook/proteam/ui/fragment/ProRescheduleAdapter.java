package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.ui.view.ProTeamProConversationItemView;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProRescheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeam.ProTeamCategory mProTeamCategory;
    private final View.OnClickListener mOnClickListener;
    private String mAssignedProviderId;

    /**
     * We're using this flag to denote the first time conversations became available
     */
    private boolean mConversationLoaded = false;

    public ProRescheduleAdapter(
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final View.OnClickListener onClickListener
    ) {
        super();
        mProTeamCategory = proTeamCategory;
        mOnClickListener = onClickListener;
        initProTeamProViewModels();
    }

    public void setAssignedProviderId(@NonNull String assignedProviderId) {
        mAssignedProviderId = assignedProviderId;
    }

    private void initProTeamProViewModels() {
        mProTeamProViewModels = new ArrayList<>();

        if (mProTeamCategory != null) {
            final List<Provider> preferredPros = mProTeamCategory.getPreferred();
            if (preferredPros != null) {
                for (Provider eachPro : preferredPros) {
                    //we only want to show on the screen where chat is enabled.
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.PREFERRED,
                            false
                    ));
                }
            }
            final List<Provider> indifferentPros = mProTeamCategory.getIndifferent();
            if (indifferentPros != null) {
                for (Provider eachPro : indifferentPros) {
                    //we only want to show on the screen where chat is enabled.
                    mProTeamProViewModels.add(ProTeamProViewModel.from(
                            eachPro,
                            ProviderMatchPreference.INDIFFERENT,
                            false
                    ));
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ProTeamProConversationItemView itemView =
                new ProTeamProConversationItemView(
                        parent.getContext(),
                        true,
                        mAssignedProviderId
                );
        itemView.setOnClickListener(mOnClickListener);

        return new ConversationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ConversationHolder) holder).bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mProTeamProViewModels.size();
    }

    public ProTeamProViewModel getItem(final int index) {
        return mProTeamProViewModels.get(index);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemViewType(final int position) {
        return 0;
    }
}
