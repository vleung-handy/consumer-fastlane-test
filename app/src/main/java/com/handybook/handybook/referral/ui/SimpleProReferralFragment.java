package com.handybook.handybook.referral.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.ratingflow.RatingFlowLog;
import com.handybook.handybook.referral.model.ProReferral;
import com.handybook.handybook.referral.model.ReferralChannels;
import com.handybook.handybook.referral.model.ReferralDescriptor;
import com.handybook.handybook.referral.model.ReferralInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Similar to {@link ProReferralFragment}, except that this one doesn't involve all the
 * fancy display of the pro's information
 */
public class SimpleProReferralFragment extends BaseReferralFragment {

    @Bind(R.id.simple_pro_header)
    TextView mShareHeader;

    @Bind(R.id.simple_pro_subtitle)
    TextView mShareSubTitle;

    @Bind(R.id.simple_pro_url)
    TextView mShareUrl;

    @Bind(R.id.simple_pro_copy_container)
    LinearLayout mCopyContainer;

    private ProReferral mProReferral;

    public static SimpleProReferralFragment newInstance(
            @NonNull final ProReferral proReferral,
            @NonNull final ReferralDescriptor referralDescriptor
    ) {

        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PRO_REFERRAL, proReferral);
        args.putSerializable(BundleKeys.REFERRAL_DESCRIPTOR, referralDescriptor);

        SimpleProReferralFragment fragment = new SimpleProReferralFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProReferral = (ProReferral) getArguments().getSerializable(BundleKeys.PRO_REFERRAL);
        mReferralDescriptor
                = (ReferralDescriptor) getArguments().getSerializable(BundleKeys.REFERRAL_DESCRIPTOR);

        Log.d("logging", "onCreate: ReferralPageShown");
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ReferralPageShown()));
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_simple_pro_referral, container, false);
        ButterKnife.bind(this, view);
        mShareHeader.setText(mProReferral.getHeader());
        mShareSubTitle.setText(mProReferral.getSubTitle());

        if (TextUtils.isBlank(mProReferral.getShareUrl())) {
            mCopyContainer.setVisibility(View.GONE);
        }
        else {
            mCopyContainer.setVisibility(View.VISIBLE);
            mShareUrl.setText(mProReferral.getShareUrl());
        }

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.simple_pro_button_sms)
    public void onTextButtonClicked() {
        shareSmsClicked();
    }

    @OnClick(R.id.simple_pro_button_email)
    public void onEmailButtonClicked() {
        shareEmailClicked();
    }

    /**
     * When user clicks on the "share link", it'll just copy onto clipboard and show a toast.
     */
    @OnClick(R.id.simple_pro_copy_container)
    public void copyUrlClicked() {
        ClipboardManager clipboard = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        Uri copyUri = Uri.parse(mShareUrl.getText().toString());
        ClipData clip = ClipData.newUri(getActivity().getContentResolver(), "URI", copyUri);
        clipboard.setPrimaryClip(clip);
        showToast(R.string.referral_copied_to_clipboard);
    }

    /**
     * When user clicks to launch the generic share intent to pick their medium of choice to share
     */
    @OnClick(R.id.simple_pro_more)
    public void moreWaysToShare() {
        launchGenericShareIntent();
    }

    /**
     * This needs to override the base method so that we can log differently. This will be called
     * after a launchGenericShareIntent is triggered
     *
     * @param guid
     * @param referralMedium
     */
    @Override
    protected void sendShareButtonTappedLog(final String guid, final String referralMedium) {
        Log.d("logging", "shareButtonTapped: ");
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ShareButtonTapped(
                referralMedium,
                guid,
                getCouponCode(),
                null,
                mReferralDescriptor.getSenderCreditAmount(),
                mReferralDescriptor.getReceiverCouponAmount()
        )));
    }

    @Override
    protected void sendShareMethodSelectedLog(final String guid, final String referralMedium) {
        Log.d("logging", "shareMethodSelected: ");
        bus.post(new LogEvent.AddLogEvent(new RatingFlowLog.ShareMethodSelected(
                referralMedium,
                guid,
                getCouponCode(),
                null,
                mReferralDescriptor.getSenderCreditAmount(),
                mReferralDescriptor.getReceiverCouponAmount()
        )));
    }

    @Nullable
    @Override
    protected String getProviderId() {
        return mProReferral == null ? null : mProReferral.getProvider().getId();
    }

    @Override
    protected ReferralChannels getReferralChannels() {
        return mProReferral == null ? null : mProReferral.getReferralInfo();
    }

    @Nullable
    @Override
    protected String getCouponCode() {
        return mReferralDescriptor == null ? null : mReferralDescriptor.getCouponCode();
    }

    @Override
    protected void onLaunchShareIntent() {
        //no op
    }

    @Override
    protected ReferralInfo getReferralInfo(@ReferralChannels.Channel final String channel) {
        return getReferralChannels() == null
               ? null
               : getReferralChannels().getReferralInfoForChannel(channel);
    }
}
