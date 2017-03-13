package com.handybook.handybook.referral.proteam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handybook.handybook.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProTeamCarouselFragment extends Fragment implements CarouselVH.RecommendClickListener {

    public static final String EXTRA_MODELS = "extra-models";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<ProCarouselVM> mProfiles;
    private CarouselVH.RecommendClickListener mRecommendClickListener;

    public static ProTeamCarouselFragment newInstance(ArrayList<ProCarouselVM> models) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MODELS, models);
        ProTeamCarouselFragment fragment = new ProTeamCarouselFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_pro_team_carousel, container, false);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mProfiles = (List<ProCarouselVM>) getArguments().getSerializable(EXTRA_MODELS);
        }

        if (mProfiles != null && !mProfiles.isEmpty()) {

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(new CarouselRecyclerAdapter(mProfiles, this));

            if (mProfiles.size() == 1) {
                mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
            else {
                mRecyclerView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
            }
        }
        else {
            throw new RuntimeException(
                    "ProTeamCarouselFragment is not supposed to be used without any providers.");
        }

        return view;
    }

    public void setRecommendClickListener(@Nullable final CarouselVH.RecommendClickListener recommendClickListener) {
        mRecommendClickListener = recommendClickListener;
    }

    @Override
    public void onRecommendClick(@NonNull final ProCarouselVM pro) {
        //relay it to the parent fragment if it cares about the data.
        if (mRecommendClickListener != null) {
            mRecommendClickListener.onRecommendClick(pro);
        }

        Toast.makeText(
                getContext(),
                "Recommend click for pro: " + pro.getDisplayName(),
                Toast.LENGTH_SHORT
        ).show();
    }
}
