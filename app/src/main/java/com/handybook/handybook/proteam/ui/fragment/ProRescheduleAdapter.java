package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.library.ui.viewholder.SingleViewHolder;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProRescheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int NORMAL = Integer.MIN_VALUE;

    private List<SingleViewHolder> mHeaders = new ArrayList<>();
    private List<ProTeamProViewModel> mProTeamProViewModels;
    private final ProTeam.ProTeamCategory mProTeamCategory;
    private final View.OnClickListener mOnClickListener;
    private String mProviderId;

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

    public void addHeader(@NonNull View header) {
        mHeaders.add(new SingleViewHolder(header));
    }

    public void setProviderId(@NonNull String providerId) {
        mProviderId = providerId;
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
        if (viewType == NORMAL) {
            final View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.layout_pro_team_conversation_item, parent, false);
            itemView.setOnClickListener(mOnClickListener);

            return new ConversationHolder(itemView, true, mProviderId);
        }
        else // Header
        {
            return mHeaders.get(viewType);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (position >= mHeaders.size() &&
            position < mProTeamProViewModels.size() + mHeaders.size()) {
            ((ConversationHolder) holder).bind(getItem(position - mHeaders.size()));
        }
    }

    @Override
    public int getItemCount() {
        return mProTeamProViewModels.size() + mHeaders.size();
    }

    public int getHeaderCount() {
        return mHeaders.size();
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
        // We return the position as type if it's header.
        return position >= mHeaders.size() ? NORMAL : position;
    }
}
