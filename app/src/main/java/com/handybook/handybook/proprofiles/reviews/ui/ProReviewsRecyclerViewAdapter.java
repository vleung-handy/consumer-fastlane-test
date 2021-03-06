package com.handybook.handybook.proprofiles.reviews.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.handybook.handybook.proprofiles.reviews.model.ProReviews;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO create a new viewholder footer type, so that we can show a loading indicator
 * ^incompatible for use with EmptiableRecyclerView
 */
class ProReviewsRecyclerViewAdapter
        extends RecyclerView.Adapter<ProReviewsRecyclerViewAdapter.ViewHolder> {

    private List<ProReviewViewModel> mProReviewViewModels;

    private List<ProReviewViewModel> getViewModelsFromProviderReviews(
            @Nullable final ProReviews proReviews
    ) {
        final List<ProReviewViewModel> proReviewViewModels = new ArrayList<>();
        if (proReviews != null && proReviews.getReviews() != null) {
            for (ProReviews.Review review : proReviews.getReviews()) {
                proReviewViewModels.add(new ProReviewViewModel(review));
            }
        }
        return proReviewViewModels;
    }

    ProReviewsRecyclerViewAdapter(@Nullable final ProReviews proReviews) {
        setHasStableIds(true);
        mProReviewViewModels = getViewModelsFromProviderReviews(proReviews);
    }

    void append(@Nullable final ProReviews proReviews) {
        mProReviewViewModels.addAll(getViewModelsFromProviderReviews(proReviews));
        notifyDataSetChanged();
    }

    public void clear() {
        mProReviewViewModels.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProReviewsRecyclerViewItem view = new ProReviewsRecyclerViewItem(parent.getContext());

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ProReviewViewModel viewModel = getItem(position);
        holder.updateView(viewModel);
    }

    @NonNull
    private ProReviewViewModel getItem(final int position) {
        return mProReviewViewModels.get(position);
    }

    @Override
    public int getItemCount() {
        return mProReviewViewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ProReviewsRecyclerViewItem mView;

        public ViewHolder(ProReviewsRecyclerViewItem view) {
            super(view);
            mView = view;
        }

        void updateView(ProReviewViewModel viewModel) {
            mView.setRating(viewModel.getRating());
            mView.setBodyText(viewModel.getReviewText());
            mView.setDateText(viewModel.getDateText());
        }
    }
}
