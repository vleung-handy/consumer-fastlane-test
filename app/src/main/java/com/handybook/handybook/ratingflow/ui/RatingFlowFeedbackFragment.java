package com.handybook.handybook.ratingflow.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RatingFlowFeedbackFragment extends InjectedFragment {

    @Bind(R.id.rating_flow_pro_image)
    ImageView mProImage;
    @Bind(R.id.rating_flow_stars)
    RatingFlowFiveStarsView mStars;

    private Booking mBooking;
    private PrerateProInfo mPrerateProInfo;
    private int mProRating;

    @NonNull
    public static RatingFlowFeedbackFragment newInstance(
            @NonNull final Booking booking,
            @NonNull final PrerateProInfo prerateProInfo,
            final int proRating
    ) {

        final RatingFlowFeedbackFragment fragment = new RatingFlowFeedbackFragment();
        final Bundle arguments = new Bundle();
        arguments.putParcelable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(BundleKeys.PRERATE_PRO_INFO, prerateProInfo);
        arguments.putInt(BundleKeys.PRO_RATING, proRating);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBooking = getArguments().getParcelable(BundleKeys.BOOKING);
        mPrerateProInfo =
                (PrerateProInfo) getArguments().getSerializable(BundleKeys.PRERATE_PRO_INFO);
        mProRating = getArguments().getInt(BundleKeys.PRO_RATING);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {

        final View view = inflater.inflate(
                R.layout.fragment_rating_flow_feedback,
                container,
                false
        );
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        Picasso.with(getContext())
               .load(mBooking.getProvider().getImageUrl())
               .placeholder(R.drawable.img_pro_placeholder)
               .noFade()
               .into(mProImage);
        mStars.selectRating(mProRating);
        mStars.lock();
    }
}
