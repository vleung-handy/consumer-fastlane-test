package com.handybook.handybook.referral.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProCarouselVM;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProTeamCarouselView;

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

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_pro_referral, container, false);
        ButterKnife.bind(this, view);

        List<ProCarouselVM> models = new ArrayList<>();
        models.add(new ProCarouselVM(
                           "https://media.handy.com/images/acae1e25-bae3-4ef4-9bf7-e81619c32a94",
                           "2432",
                           "4.5",
                           "Roum S."
                   )
        );
        models.add(new ProCarouselVM(
                           "https://media.handy.com/images/acae1e25-bae3-4ef4-9bf7-e81619c32a94",
                           "2432",
                           "4.5",
                           "Roum S."
                   )
        );
        models.add(new ProCarouselVM(
                           "https://media.handy.com/images/acae1e25-bae3-4ef4-9bf7-e81619c32a94",
                           "2432",
                           "4.5",
                           "Roum S."
                   )
        );

        mCarousel.bind(models, null);

        return view;

    }
}
