package com.handybook.handybook.proteam.mypros;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.core.ui.view.SmallVerticalMiniProProfile;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.logger.handylogger.model.MyProsLog;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamCategoryType;
import com.handybook.handybook.proteam.ui.activity.ProTeamEditActivity;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ProTeamRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<ProTeamRecyclerViewItemVM> mProTeamRecyclerViewItemVMs;

    /**
     * used for logging purposes only
     */
    private Bus mBus;

    /**
     * creating the viewmodels from ProTeam in this class rather than passing them in from outside
     * to ensure the very specific viewmodel ordering requirements.
     * see getViewModelsFromProTeam(ProTeam proTeam)
     *
     * @param proTeam the pro team to be used to display the recycler view
     * @param bus used for logging purposes only
     */
    ProTeamRecyclerViewAdapter(@Nullable ProTeam proTeam, @NonNull Bus bus) {
        /*
        specific view model list ordering requirements:
        - the first item of this list is the favorite pro
        - sorted by pro team category: cleaners, then handymen
        - after the above, it is sorted by # of jobs descending
         */
        mProTeamRecyclerViewItemVMs = getViewModelsFromProTeam(proTeam);
        mBus = bus;
        setHasStableIds(true);

    }

    private List<ProTeamRecyclerViewItemVM> getViewModelsFromProTeam(@Nullable ProTeam proTeam) {
        List<ProTeamRecyclerViewItemVM> viewModels = new ArrayList<>();

        if (proTeam != null) {
            ProTeam.ProTeamCategory cleaningCategory
                    = proTeam.getCategory(ProTeamCategoryType.CLEANING);
            ProTeam.ProTeamCategory handymenCategory
                    = proTeam.getCategory(ProTeamCategoryType.HANDYMEN);

            //need to sort pro team members by # jobs completed descending
            Comparator<Provider> providerComparator = new Comparator<Provider>() {
                @Override
                public int compare(final Provider o1, final Provider o2) {
                    //first, sort by favorite
                    //there is always only ONE favorite, so not bothering to further sort by more properties
                    //there will never be an instance in which both o1 and o2 are favorites
                    if (o2.isFavorite()) { return 1; }
                    if (o1.isFavorite()) { return -1; }

                    //could combine some of this logic, but separating it out for better readability
                    //for non-favorites, sort by booking count
                    int bookingCount1 = o1.getBookingCount() == null ? 0 : o1.getBookingCount();
                    int bookingCount2 = o2.getBookingCount() == null ? 0 : o2.getBookingCount();
                    if (bookingCount1 < bookingCount2) { return 1; }
                    if (bookingCount1 > bookingCount2) { return -1; }
                    return 0;
                }
            };

            //add cleaners. if there is a favorite it should be the first one after sorting
            List<Provider> sortedCleanersList = new ArrayList<>();
            if (cleaningCategory != null && cleaningCategory.getPreferred() != null) {
                sortedCleanersList.addAll(cleaningCategory.getPreferred());
                Collections.sort(sortedCleanersList, providerComparator);

                for (Provider provider : sortedCleanersList) {
                    ProTeamRecyclerViewItemVM carouselVM = ProTeamRecyclerViewItemVM.fromProvider(
                            provider
                    );
                    viewModels.add(carouselVM);
                }
            }

            //then add handymen
            List<Provider> sortedHandymenList = new ArrayList<>();
            if (handymenCategory != null && handymenCategory.getPreferred() != null) {
                sortedHandymenList.addAll(handymenCategory.getPreferred());
                Collections.sort(sortedCleanersList, providerComparator);
                for (Provider provider : sortedHandymenList) {
                    ProTeamRecyclerViewItemVM carouselVM = ProTeamRecyclerViewItemVM.fromProvider(
                            provider
                    );
                    viewModels.add(carouselVM);
                }
            }
        }

        return viewModels;
    }

    /**
     * @return true only if this adapter has >0 pro team items (headers are not counted)
     */
    public boolean isEmpty() {
        return mProTeamRecyclerViewItemVMs.isEmpty();
    }

    public void clear() {
        mProTeamRecyclerViewItemVMs.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.my_pros_pro_team_recycler_view_item,
                    parent,
                    false
            );
            return new ItemViewHolder(view);
        }
        else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.my_pros_pro_team_choose_favorite_recycler_view_item,
                    parent,
                    false
            );
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mBus.post(new LogEvent.AddLogEvent(new MyProsLog.ChooseFavoriteProButtonTapped()));
                    Context context = v.getContext();
                    context.startActivity(new Intent(context, ProTeamEditActivity.class));

                }
            });
            return new HeaderViewHolder(view);
        }
        throw new RuntimeException("viewType " + viewType + " is not supported");
    }

    @Override
    public int getItemViewType(final int position) {
        if (isHeaderPosition(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean shouldShowHeader() {
        /*
        only show "choose favorite pro" button if the recycler view isn't empty
        and if no favorite pro
         */
        return !mProTeamRecyclerViewItemVMs.isEmpty()
               && !mProTeamRecyclerViewItemVMs.get(0).isFavorite();
    }

    private int getHeaderLength() {
        return shouldShowHeader() ? 1 : 0;
    }

    private boolean isHeaderPosition(final int position) {
        return position >= 0 && position < getHeaderLength();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final ProTeamRecyclerViewItemVM viewModel = getItem(position);
            ((ItemViewHolder) holder).updateView(viewModel, position);
        }
        //no need to update header view
    }

    @Nullable
    private ProTeamRecyclerViewItemVM getItem(final int position) {
        if (position < getHeaderLength()) { return null; }
        return mProTeamRecyclerViewItemVMs.get(position - getHeaderLength());
    }

    @Override
    public int getItemCount() {
        return mProTeamRecyclerViewItemVMs.size() + getHeaderLength();
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(final View itemView) {
            super(itemView);
        }
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final View mView;

        ItemViewHolder(View view) {
            super(view);
            mView = view;
        }

        void updateView(final ProTeamRecyclerViewItemVM viewModel, int position) {
            SmallVerticalMiniProProfile verticalMiniProProfile
                    = (SmallVerticalMiniProProfile) mView.findViewById(R.id.pro_team_carousel_item_mini_pro_profile_card);

            verticalMiniProProfile.setProTeamIndicatorEnabled(true);
            verticalMiniProProfile.setIsProTeam(viewModel.isProTeam());
            verticalMiniProProfile.setIsProTeamFavorite(viewModel.isFavorite());
            verticalMiniProProfile.setTitle(viewModel.getDisplayName());
            verticalMiniProProfile.setImage(viewModel.getImageUrl());

            View favoriteProIndicatorText
                    = mView.findViewById(R.id.pro_team_favorite_pro_indicator_text);
            favoriteProIndicatorText.setVisibility(viewModel.isFavorite()
                                                   ? View.VISIBLE
                                                   : View.INVISIBLE);

            //show the pro team category text if this item is the first in category
            TextView proTeamCategoryText
                    = (TextView) mView.findViewById(R.id.pro_team_category_text);
            ProTeamRecyclerViewItemVM previousItem
                    = getItem(position - 1);
            if (position == getHeaderLength()
                || (viewModel.getProTeamCategoryText() != null
                    && previousItem != null
                    && !viewModel.getProTeamCategoryText()
                                 .equalsIgnoreCase(previousItem.getProTeamCategoryText()))
                    ) {
                proTeamCategoryText.setText(viewModel.getProTeamCategoryText());
                proTeamCategoryText.setVisibility(View.VISIBLE);
            }
            else {
                proTeamCategoryText.setVisibility(View.INVISIBLE);
            }
            verticalMiniProProfile.setRatingAndJobsCount(
                    viewModel.getAverageRating(),
                    viewModel.getJobCount()
            );

            if (viewModel.isProfileEnabled()) {
                final Context context = mView.getContext();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        context.startActivity(ProProfileActivity.buildIntent(
                                context,
                                viewModel.getProviderId(),
                                SourcePage.MY_PROS
                        ));
                    }
                });
            }
        }
    }
}
