package com.handybook.handybook.referral.proteam;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;

import java.util.List;

public class CarouselRecyclerAdapter extends RecyclerView.Adapter<CarouselVH> {

    private List<ProCarouselVM> mProCarouselVMs;
    private CarouselVH.RecommendClickListener mRecommendClickListener;

    public CarouselRecyclerAdapter(
            @NonNull List<ProCarouselVM> proCarouselVMs,
            @NonNull CarouselVH.RecommendClickListener listener
    ) {
        mProCarouselVMs = proCarouselVMs;
        mRecommendClickListener = listener;
    }

    @Override
    public CarouselVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.carousel_item, parent, false);
        return new CarouselVH(view, mRecommendClickListener);
    }

    @Override
    public void onBindViewHolder(CarouselVH holder, int position) {
        holder.bind(mProCarouselVMs.get(position));
    }

    @Override
    public int getItemCount() {
        return mProCarouselVMs.size();
    }
}
