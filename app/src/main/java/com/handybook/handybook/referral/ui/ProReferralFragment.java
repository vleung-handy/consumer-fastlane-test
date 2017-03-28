package com.handybook.handybook.referral.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.ui.view.proteamcarousel.CarouselPagerAdapter;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProCarouselVM;
import com.handybook.handybook.library.ui.view.proteamcarousel.ProTeamCarouselView;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Screen that shows a carousel of pro team members to share with friends
 */
public class ProReferralFragment extends BaseReferralFragment {

    @Bind(R.id.pro_referral_carousel)
    ProTeamCarouselView mCarousel;

    @Bind(R.id.pro_referral_subtitle)
    TextView mSubtitle;

    @Bind(R.id.pro_referral_title)
    TextView mTitle;

    @Bind(R.id.pro_referral_share_link)
    TextView mShareUrl;

    private List<ProCarouselVM> mCarouselVMs;
    private ProReferral mSelectedPro;

    public static ProReferralFragment newInstance(
            ReferralDescriptor referralDescriptor,
            String source
    ) {
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);
        args.putString(BundleKeys.REFERRAL_PAGE_SOURCE, source);
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

        mCarouselVMs = new ArrayList<>();
        for (ProReferral pr : mReferralDescriptor.getProReferralInfo()) {
            mCarouselVMs.add(ProCarouselVM.fromProReferral(pr));
        }

        final String currencyChar = userManager.getCurrentUser().getCurrencyChar();
        String formattedSenderCreditAmount = TextUtils.formatPrice(
                mReferralDescriptor.getSenderCreditAmount(),
                currencyChar,
                null
        );

        final String sharingLink = BASE_REFERRAL_URL + mReferralDescriptor.getCouponCode();
        mShareUrl.setText(sharingLink);
        mTitle.setText(getResources().getQuantityString(
                R.plurals.pro_referral_title,
                mCarouselVMs.size()
        ));
        mSubtitle.setText(getString(R.string.pro_referral_subtitle, formattedSenderCreditAmount));
        mCarousel.bind(mCarouselVMs, new CarouselPagerAdapter.ActionListener() {
            @Override
            public void onPrimaryButtonClick(final ProCarouselVM pro, final View button) {
                int idx = mCarouselVMs.indexOf(pro);
                mSelectedPro = mReferralDescriptor.getProReferralInfo().get(idx);
                launchGenericShareIntent();
            }
        });

        return view;
    }

    /**
     * This is where the share button is clicked, launch the default share intent
     */
    @OnClick(R.id.pro_referral_share_button)
    public void sharedButtonClicked() {
        //make sure to clear out the selected pro, in case the user has previously clicked the
        //pro's recommend button.
        mSelectedPro = null;
        launchGenericShareIntent();
    }

    /**
     * This is where the share link is clicked. Just copy to clipboard
     */
    @OnClick(R.id.pro_referral_share_link)
    public void onShareUrlClick() {
        shareUrlClicked(mReferralDescriptor.getCouponCode());
    }

    @Nullable
    @Override
    protected String getProviderId() {
        return mSelectedPro == null || mSelectedPro.getProvider() == null
               ? null
               : mSelectedPro.getProvider().getId();
    }

    /**
     * If there is pro specific information, then return those, otherwise return the default share information
     * @return
     */
    @Override
    protected ReferralChannels getReferralChannels() {
        if (mSelectedPro != null) {
            return mSelectedPro.getReferralInfo();
        }
        else {
            return mReferralDescriptor.getReferralChannelsForSource(ReferralDescriptor.SOURCE_REFERRAL_PAGE);
        }
    }

    @Override
    protected String getCouponCode() {
        return mReferralDescriptor.getCouponCode();
    }

    @Override
    protected void onLaunchShareIntent() {
        //don't need to do anything here.
    }

    /**
     * If there is pro specific information, then return those, otherwise return the default share information
     * @param channel
     * @return
     */
    @Override
    protected ReferralInfo getReferralInfo(@ReferralChannels.Channel final String channel) {
        ReferralChannels referralChannels = getReferralChannels();
        return referralChannels.getReferralInfoForChannel(channel);
    }
}
