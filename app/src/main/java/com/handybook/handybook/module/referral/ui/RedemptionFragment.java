package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.module.referral.event.ReferralsEvent;
import com.handybook.handybook.module.referral.model.RedemptionDetails;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.TextUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RedemptionFragment extends InjectedFragment
{
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;

    private String mReferralGuid;

    public static RedemptionFragment newInstance()
    {
        return new RedemptionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReferralGuid = ReferralIntentUtil.getReferralGuidFromIntent(getActivity().getIntent());
        if (mReferralGuid == null)
        {
            startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_redemption, container, false);
        ButterKnife.bind(this, view);

        bus.post(new ReferralsEvent.RequestRedemptionDetails(mReferralGuid));

        return view;
    }

    @Subscribe
    public void onReceiveRequestRedemptionDetails(
            final ReferralsEvent.ReceiveRedemptionDetailsSuccess event
    )
    {
        final RedemptionDetails redemptionDetails = event.getRedemptionDetails();
        final String currencySymbol = redemptionDetails.getLocalizationData().getCurrencySymbol();
        final int receiverCouponAmount = redemptionDetails.getReceiverCouponAmount();
        final String senderFirstName = redemptionDetails.getSender().getFirstName();

        final String receiverCouponAmountFormatted =
                TextUtils.formatPrice(receiverCouponAmount, currencySymbol, null);
        mTitle.setText(getString(R.string.redemption_title_formatted, senderFirstName,
                receiverCouponAmountFormatted));
        mSubtitle.setText(getString(R.string.redemption_subtitle_formatted,
                receiverCouponAmountFormatted));
    }
}
