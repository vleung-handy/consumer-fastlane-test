package com.handybook.handybook.proteam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.view.MiniProProfile;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamPro;
import com.handybook.handybook.proteam.ui.view.ProTeamSectionListHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewProTeamCategoryAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_PRO = 2;
    private static final int FAVORITE_PRO_POSITION = 1;

    private final Context mContext;
    private List<Object> mItems;
    private ActionCallbacks mActionCallbacks;

    public NewProTeamCategoryAdapter(
            @NonNull final Context context,
            @Nullable final ProTeam.ProTeamCategory proTeamCategory,
            @NonNull final ActionCallbacks actionCallbacks
    )
    {
        mContext = context;
        mActionCallbacks = actionCallbacks;
        mItems = new ArrayList<>();

        if (proTeamCategory != null
                && proTeamCategory.getPreferred() != null
                && !proTeamCategory.getPreferred().isEmpty())
        {
            final ProTeamPro favoritePro = proTeamCategory.getFavoritePro();
            mItems.add(mContext.getString(R.string.favorite_pro));
            mItems.add(favoritePro);
            mItems.add(mContext.getString(R.string.backup_pros));

            final List<ProTeamPro> preferredPros = proTeamCategory.getPreferred();
            if (favoritePro != null && preferredPros != null && !preferredPros.isEmpty())
            {
                preferredPros.remove(favoritePro);
            }
            if (preferredPros != null && !preferredPros.isEmpty())
            {
                mItems.addAll(preferredPros);
            }
            else
            {
                mItems.add(null);
            }
        }
    }

    @Override
    public int getItemViewType(final int position)
    {
        final Object item = mItems.get(position);
        if (item != null && item instanceof String)
        {
            return VIEW_TYPE_HEADER;
        }
        else
        {
            return VIEW_TYPE_PRO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            final ViewGroup parent,
            final int viewType
    )
    {
        switch (viewType)
        {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(new ProTeamSectionListHeaderView(mContext));
            case VIEW_TYPE_PRO:
                final View itemView = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.layout_pro_team_pro_card_with_empty_state,
                                 parent, false
                        );
                return new ProViewHolder(itemView, mActionCallbacks);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {
        ((BaseViewHolder) holder).bind(mItems.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }

    private static abstract class BaseViewHolder extends RecyclerView.ViewHolder
    {
        public BaseViewHolder(final View itemView)
        {
            super(itemView);
        }

        abstract void bind(Object item);
    }


    private static class HeaderViewHolder extends BaseViewHolder
    {
        public HeaderViewHolder(final View itemView)
        {
            super(itemView);
        }

        @Override
        void bind(final Object item)
        {
            final ProTeamSectionListHeaderView itemView = (ProTeamSectionListHeaderView) this.itemView;
            itemView.setTitle((String) item);
            itemView.setHelpIconClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    // TODO: Implement
                }
            });
        }
    }


    static class ProViewHolder extends BaseViewHolder
    {
        private final ActionCallbacks mActionCallbacks;
        @Bind(R.id.pro_team_pro_card_holder)
        ViewGroup mProTeamProCardHolder;
        @Bind(R.id.pro_team_pro_card_checkbox)
        CheckBox mHeartIcon;
        @Bind(R.id.pro_team_pro_card_profile)
        MiniProProfile mProProfile;
        @Bind(R.id.empty_state_holder)
        ViewGroup mEmptyStateHolder;
        @Bind(R.id.empty_state_title)
        TextView mEmptyStateTitle;
        @Bind(R.id.empty_state_subtitle)
        TextView mEmptyStateSubtitle;

        ProViewHolder(final View itemView, @NonNull final ActionCallbacks actionCallbacks)
        {
            super(itemView);
            mActionCallbacks = actionCallbacks;
        }

        @Override
        void bind(final Object item)
        {
            ButterKnife.bind(this, itemView);
            if (item != null)
            {
                showActiveState((ProTeamPro) item);
            }
            else
            {
                showEmptyState(getAdapterPosition() == FAVORITE_PRO_POSITION);
            }
        }

        private void showActiveState(final ProTeamPro pro)
        {
            mEmptyStateHolder.setVisibility(View.GONE);
            mProTeamProCardHolder.setVisibility(View.VISIBLE);
            initHeartIcon(pro);
            initProProfile(pro);
        }

        private void initHeartIcon(final ProTeamPro pro)
        {
            mHeartIcon.setChecked(pro.isFavorite());
            mHeartIcon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    mActionCallbacks.onHeartClick(pro);
                }
            });
            mHeartIcon.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(final View view)
                {
                    mActionCallbacks.onLongClick(pro);
                    return true;
                }
            });
            // This listener will give the illusion that the checkbox doesn't change state which is
            // exactly what we want.
            mHeartIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(
                        final CompoundButton buttonView,
                        final boolean isChecked
                )
                {
                    mHeartIcon.setOnCheckedChangeListener(null);
                    mHeartIcon.setChecked(!isChecked);
                    mHeartIcon.setOnCheckedChangeListener(this);
                }
            });
        }

        private void initProProfile(final ProTeamPro pro)
        {
            mProProfile.setTitle(pro.getName());
            mProProfile.setRatingAndJobsCount(pro.getAverageRating(), pro.getBookingCount());
            mProProfile.setProTeamIndicatorEnabled(false);
            mProProfile.setHandymanIndicatorEnabled(false);
            mProProfile.setImage(pro.getImageUrl());
        }

        private void showEmptyState(final boolean isFavoriteProPosition)
        {
            mProTeamProCardHolder.setVisibility(View.GONE);
            mEmptyStateHolder.setVisibility(View.VISIBLE);
            if (isFavoriteProPosition)
            {
                mEmptyStateTitle.setText(R.string.no_favorite_pro_title);
                mEmptyStateSubtitle.setText(R.string.no_favorite_pro_subtitle);
            }
            else
            {
                mEmptyStateTitle.setText(R.string.no_pro_team_title);
                mEmptyStateSubtitle.setText(R.string.no_pro_team_subtitle);
            }
        }
    }


    public interface ActionCallbacks
    {
        void onHeartClick(final ProTeamPro proTeamPro);

        void onLongClick(final ProTeamPro proTeamPro);
    }
}
