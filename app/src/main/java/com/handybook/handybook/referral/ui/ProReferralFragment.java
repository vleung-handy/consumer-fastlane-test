package com.handybook.handybook.referral.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProCarouselVM;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProTeamCarouselView;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralDescriptor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO: JIA:
 * NOTE: This view will now show up, because the feature is in the dark.
 */
public class ProReferralFragment extends InjectedFragment {

    @Bind(R.id.pro_referral_carousel)
    ProTeamCarouselView mCarousel;

    private ReferralDescriptor mReferralDescriptor;

    public static ProReferralFragment newInstance(ReferralDescriptor referralDescriptor) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        ProReferralFragment fragment = new ProReferralFragment();
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
        View view = inflater.inflate(R.layout.fragment_pro_referral, container, false);
        ButterKnife.bind(this, view);

        mReferralDescriptor
                = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);

        List<ProCarouselVM> models = new ArrayList<>();
        for (ProReferral pr : mReferralDescriptor.getProReferralInfo()) {
            models.add(ProCarouselVM.fromProReferral(pr));
        }

        mCarousel.bind(models, null);

        //TODO JIA: Bind everything else
        return view;
    }


}
