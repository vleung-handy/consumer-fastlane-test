package com.handybook.handybook.proteam.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.ui.view.MiniProProfile;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.view.ProTeamSectionListHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewProTeamCategoryAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    ) {
        mContext = context;
        mActionCallbacks = actionCallbacks;
        mItems = new ArrayList<>();

        if (proTeamCategory != null
            && proTeamCategory.getPreferred() != null
            && !proTeamCategory.getPreferred().isEmpty()) {
            final Provider favoritePro = proTeamCategory.getFavoritePro();
            mItems.add(mContext.getString(R.string.favorite_pro));
            mItems.add(favoritePro);
            mItems.add(mContext.getString(R.string.pro_team));

            final List<com.handybook.handybook.booking.model.Provider> preferredPros
                    = new ArrayList<>();
            if (proTeamCategory.getPreferred() != null) {
                preferredPros.addAll(proTeamCategory.getPreferred());
            }
            if (favoritePro != null && !preferredPros.isEmpty()) {
                preferredPros.remove(favoritePro);
            }
            if (!preferredPros.isEmpty()) {
                mItems.addAll(preferredPros);
            }
            else {
                mItems.add(null);
            }
        }
    }

    @Override
    public int getItemViewType(final int position) {
        final Object item = mItems.get(position);
        if (item != null && item instanceof String) {
            return VIEW_TYPE_HEADER;
        }
        else {
            return VIEW_TYPE_PRO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            final ViewGroup parent,
            final int viewType
    ) {
        switch (viewType) {
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((BaseViewHolder) holder).bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(final View itemView) {
            super(itemView);
        }

        abstract void bind(Object item);
    }


    private static class HeaderViewHolder extends BaseViewHolder {

        public HeaderViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void bind(final Object item) {
            final ProTeamSectionListHeaderView itemView =
                    (ProTeamSectionListHeaderView) this.itemView;
            final String text = (String) item;
            itemView.setTitle(text);
            itemView.setHelpIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Context context = view.getContext();
                    String title;
                    String message;
                    final String favoriteProTitle = context.getString(R.string.favorite_pro);
                    if (text.equals(favoriteProTitle)) {
                        title = favoriteProTitle;
                        message = context.getString(R.string.favorite_pro_help_message);
                    }
                    else {
                        title = context.getString(R.string.pro_team);
                        message = context.getString(R.string.pro_team_help_message);
                    }
                    new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(true)
                            .setPositiveButton(R.string.ok, null)
                            .show();
                }
            });
        }
    }


    static class ProViewHolder extends BaseViewHolder {

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

        ProViewHolder(final View itemView, @NonNull final ActionCallbacks actionCallbacks) {
            super(itemView);
            mActionCallbacks = actionCallbacks;
        }

        @Override
        void bind(final Object item) {
            ButterKnife.bind(this, itemView);
            if (item != null) {
                final Provider pro = (Provider) item;
                showActiveState(pro);
                mProTeamProCardHolder.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        mActionCallbacks.onLongClick(pro);
                        return true;
                    }
                });
                mProTeamProCardHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mActionCallbacks.onHeartClick(pro);
                    }
                });
            }
            else {
                showEmptyState();
            }
        }

        private void showActiveState(final Provider pro) {
            mEmptyStateHolder.setVisibility(View.GONE);
            mProTeamProCardHolder.setVisibility(View.VISIBLE);
            initHeartIcon(pro);
            initProProfile(pro);
        }

        private void initHeartIcon(final Provider pro) {
            mHeartIcon.setChecked(pro.isFavorite());
            mHeartIcon.setClickable(false);
        }

        private void initProProfile(final Provider pro) {
            mProProfile.setTitle(pro.getName());
            mProProfile.setRatingAndJobsCount(pro.getAverageRating(), pro.getBookingCount());
            mProProfile.setProTeamIndicatorEnabled(false);
            mProProfile.setHandymanIndicatorEnabled(false);
            mProProfile.setImage(pro.getImageUrl());
        }

        private void showEmptyState() {
            mProTeamProCardHolder.setVisibility(View.GONE);
            mEmptyStateHolder.setVisibility(View.VISIBLE);
            if (getAdapterPosition() == FAVORITE_PRO_POSITION) {
                mEmptyStateTitle.setText(R.string.no_favorite_pro_title);
                mEmptyStateSubtitle.setText(R.string.no_favorite_pro_subtitle);
            }
            else {
                mEmptyStateTitle.setText(R.string.pro_team);
                mEmptyStateSubtitle.setText(R.string.no_pro_team_subtitle);
            }
        }
    }


    public interface ActionCallbacks {

        void onHeartClick(final Provider proTeamPro);

        void onLongClick(final Provider proTeamPro);
    }
}
